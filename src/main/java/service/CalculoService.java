package service;

import br.ucalc.calculadora_pcs.model.Calculo;
import br.ucalc.calculadora_pcs.model.IndiceEconomico;
import br.ucalc.calculadora_pcs.model.ItemCalculo;
import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CalculoService {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);
    private static final BigDecimal SEIS_MIL = BigDecimal.valueOf(6000);
    private static final BigDecimal TRINTA = BigDecimal.valueOf(30);
    private static final int ESCALA_INTERMEDIARIA = 10; // casas decimais durante os cálculos
    private static final int ESCALA_FINAL = 2;          // casas decimais nos valores em R$

    private final IndiceEconomicoRepository indiceEconomicoRepository;

    public CalculoService(IndiceEconomicoRepository indiceEconomicoRepository) {
        this.indiceEconomicoRepository = indiceEconomicoRepository;
    }

    public List<ItemCalculo> gerarTabela(Calculo calculo) {
        YearMonth mesInicial = YearMonth.from(calculo.getDataInicial());
        YearMonth mesFinal = YearMonth.from(calculo.getDataFinal());
        BigDecimal valorBase = calculo.getValorDevidoInicial();

        List<ItemCalculo> itens = new ArrayList<>();
        BigDecimal fatorCorrecaoAcumulado = BigDecimal.ONE;

        YearMonth mesAtual = mesInicial;
        while (!mesAtual.isAfter(mesFinal)) {

            ItemCalculo item = new ItemCalculo();
            item.setCalculo(calculo);
            item.setData(mesAtual.atDay(1)); // 1º dia do mês de referência
            item.setValorDevido(valorBase);

            // ---- Correção monetária (fator acumulado) ----
            BigDecimal indiceMes = buscarIndiceCorrecao(calculo.getTipoCorrecao(), mesAtual);
            BigDecimal fatorMes = BigDecimal.ONE.add(
                    indiceMes.divide(CEM, ESCALA_INTERMEDIARIA, RoundingMode.HALF_UP)
            );
            fatorCorrecaoAcumulado = fatorCorrecaoAcumulado.multiply(fatorMes);
            item.setIndiceCorrecao(fatorCorrecaoAcumulado);

            BigDecimal valorAtualizado = valorBase
                    .multiply(fatorCorrecaoAcumulado)
                    .setScale(ESCALA_FINAL, RoundingMode.HALF_UP);
            item.setValorAtualizado(valorAtualizado);

            // ---- Juros de mora (0,5% a.m. -> dias corridos / 6000) ----
            long mesesDecorridos = ChronoUnit.MONTHS.between(mesInicial, mesAtual);
            BigDecimal diasCorridos = BigDecimal.valueOf(mesesDecorridos + 1).multiply(TRINTA);
            BigDecimal taxaJuros = diasCorridos.divide(SEIS_MIL, ESCALA_INTERMEDIARIA, RoundingMode.HALF_UP);
            item.setIndiceJuros(taxaJuros);

            BigDecimal valorJuros = valorAtualizado
                    .multiply(taxaJuros)
                    .setScale(ESCALA_FINAL, RoundingMode.HALF_UP);
            item.setValorJuros(valorJuros);

            // ---- Total ----
            item.setTotal(valorAtualizado.add(valorJuros));

            itens.add(item);
            mesAtual = mesAtual.plusMonths(1);
        }

        return itens;
    }

    private BigDecimal buscarIndiceCorrecao(TipoCorrecao tipoCorrecao, YearMonth mes) {
        TipoIndice tipoIndice = switch (tipoCorrecao) {
            case IPCA_E -> TipoIndice.IPCA_E;
            case TR -> TipoIndice.TR;
            case SELIC -> TipoIndice.SELIC;
            case TODAS -> throw new UnsupportedOperationException(
                    "Opção 'TODAS' ainda não implementada"); // TODO
        };

        return indiceEconomicoRepository.findByTipoAndReferencia(tipoIndice, mes)
                .map(IndiceEconomico::getValor)
                .orElseThrow(() -> new IllegalStateException(
                        "Índice " + tipoIndice + " não cadastrado para " + mes));
    }
}
