package br.ucalc.calculadora_pcs.service;

import br.ucalc.calculadora_pcs.model.Calculo;
import br.ucalc.calculadora_pcs.model.IndiceEconomico;
import br.ucalc.calculadora_pcs.model.ItemCalculo;
import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import org.springframework.stereotype.Service;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;

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

            BigDecimal indiceJurosMes =
                    buscarIndiceJuros(
                            calculo.getTipoJuros(),
                            mesAtual);

            item.setIndiceJuros(indiceJurosMes);

            BigDecimal valorJuros = valorAtualizado
                    .multiply(
                            indiceJurosMes.divide(
                                    CEM,
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP
                            )
                    )
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

    private BigDecimal buscarIndiceJuros(
            br.ucalc.calculadora_pcs.model.enums.TipoJuros tipoJuros,
            YearMonth mes) {

        switch (tipoJuros) {

            case MORA_MENSAL:
                return BigDecimal.valueOf(0.5);

            case POUPANCA:

                return indiceEconomicoRepository
                        .findByTipoAndReferencia(
                                TipoIndice.POUPANCA,
                                mes)
                        .map(IndiceEconomico::getValor)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Índice POUPANCA não cadastrado para " + mes));

            case TODAS:
                throw new UnsupportedOperationException(
                        "Opção TODAS ainda não implementada");

            default:
                throw new IllegalStateException("Tipo de juros inválido");
        }
    }
}
