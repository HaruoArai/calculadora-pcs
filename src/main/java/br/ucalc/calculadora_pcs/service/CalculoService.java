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
    private static final int ESCALA_INTERMEDIARIA = 10; // casas decimais durante os cálculos
    private static final int ESCALA_FINAL = 2;          // casas decimais nos valores em R$

    private final IndiceEconomicoRepository indiceEconomicoRepository;

    public CalculoService(IndiceEconomicoRepository indiceEconomicoRepository) {
        this.indiceEconomicoRepository = indiceEconomicoRepository;
    }

    public List<ItemCalculo> gerarTabela(Calculo calculo) {
        YearMonth mesInicial =
                YearMonth.from(calculo.getDataParcela());
        YearMonth mesFinal =
                YearMonth.from(calculo.getDataAtualizacao());

        YearMonth mesCitacao =
                YearMonth.from(calculo.getDataCitacao());

        BigDecimal valorBase = calculo.getValorDevidoInicial();

        List<ItemCalculo> itens = new ArrayList<>();


        YearMonth mesAtual = mesInicial;
        while (!mesAtual.isAfter(mesFinal)) {

            ItemCalculo item = new ItemCalculo();
            item.setCalculo(calculo);
            item.setData(mesAtual.atDay(1)); // 1º dia do mês de referência
            item.setValorDevido(valorBase);

            // DATA DE INÍCIO DOS JUROS DA LINHA
            YearMonth inicioJurosLinha =
                    mesAtual.isAfter(mesCitacao)
                            ? mesAtual
                            : mesCitacao;

            // ---- Correção monetária (fator acumulado) ----
            BigDecimal fatorCorrecao =
                    calcularFatorCorrecao(
                            calculo.getTipoCorrecao(),
                            mesAtual,
                            mesFinal);

            item.setIndiceCorrecao(fatorCorrecao);

            BigDecimal valorAtualizado =
                    valorBase
                            .multiply(fatorCorrecao)
                            .setScale(
                                    ESCALA_FINAL,
                                    RoundingMode.HALF_UP);

            item.setValorAtualizado(valorAtualizado);

            // ---- Juros ----
            BigDecimal indiceJurosMes =
                    calcularJurosAcumulado(
                            calculo.getTipoJuros(),
                            inicioJurosLinha,
                            mesFinal);

            item.setIndiceJuros(indiceJurosMes);

            BigDecimal valorJuros =
                    valorAtualizado
                            .multiply(
                                    indiceJurosMes.divide(
                                            CEM,
                                            ESCALA_INTERMEDIARIA,
                                            RoundingMode.HALF_UP))
                            .setScale(
                                    ESCALA_FINAL,
                                    RoundingMode.HALF_UP);

            item.setValorJuros(valorJuros);

            // ---- Total ----
            item.setTotal(valorAtualizado.add(valorJuros));

            itens.add(item);
            mesAtual = mesAtual.plusMonths(1);
        }

        return itens;
    }

    private BigDecimal buscarIndiceCorrecao(
            TipoCorrecao tipoCorrecao,
            YearMonth mes) {
        TipoIndice tipoIndice = descobrirIndiceCorrecao(
                tipoCorrecao,
                mes);
        return indiceEconomicoRepository
                .findByTipoAndReferencia(
                        tipoIndice,
                        mes)
                .map(IndiceEconomico::getValor)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Índice "
                                        + tipoIndice
                                        + " não cadastrado para "
                                        + mes));
    }

    private TipoIndice descobrirIndiceCorrecao(
            TipoCorrecao tipoCorrecao,
            YearMonth referencia) {
        YearMonth marcoIpcae =
                YearMonth.of(2015, 3);
        YearMonth marcoSelic =
                YearMonth.of(2021, 12);
        return switch (tipoCorrecao) {
            case TR_IPCAE -> {
                if (referencia.isBefore(marcoIpcae)) {
                    yield TipoIndice.TR;
                }
                yield TipoIndice.IPCA_E;
            }
            case TR -> TipoIndice.TR;
            case IPCA_E -> TipoIndice.IPCA_E;
            case SELIC -> TipoIndice.SELIC;
        };
    }

    private BigDecimal buscarIndiceJuros(
            br.ucalc.calculadora_pcs.model.enums.TipoJuros tipoJuros,
            YearMonth mes) {

        switch (tipoJuros) {
            case SEM_JUROS:
                return BigDecimal.ZERO;
            case MORA:
                return BigDecimal.valueOf(0.5);
            case MORA_POUPANCA:
                throw new UnsupportedOperationException(
                        "MORA_POUPANCA ainda não implementado");
            default:
                throw new IllegalStateException(
                        "Tipo de juros inválido");
        }
    }

    private BigDecimal calcularJurosAcumulado(
            TipoJuros tipoJuros,
            YearMonth inicioJurosLinha,
            YearMonth dataAtualizacao) {

        BigDecimal acumulado = BigDecimal.ZERO;

        YearMonth mes = inicioJurosLinha;

        while (!mes.isAfter(dataAtualizacao)) {

            acumulado = acumulado.add(
                    buscarIndiceJuros(
                            tipoJuros,
                            mes));

            mes = mes.plusMonths(1);
        }

        return acumulado;
    }

    private BigDecimal calcularFatorCorrecao(
            TipoCorrecao tipoCorrecao,
            YearMonth inicio,
            YearMonth fim) {

        switch (tipoCorrecao) {

            case TR:
                return calcularFatorIndice(
                        TipoIndice.TR,
                        inicio,
                        fim);
            case IPCA_E:
                return calcularFatorIndice(
                        TipoIndice.IPCA_E,
                        inicio,
                        fim);
            case SELIC:
                return calcularFatorIndice(
                        TipoIndice.SELIC,
                        inicio,
                        fim);
            case TR_IPCAE:
                return calcularFatorTrIpcae(
                        inicio,
                        fim);
            default:
                throw new UnsupportedOperationException(
                        "Tipo de correção não implementado");
        }
    }

    private BigDecimal calcularFatorIndice(
            TipoIndice tipoIndice,
            YearMonth inicio,
            YearMonth fim) {

        BigDecimal fator = BigDecimal.ONE;

        YearMonth mes = inicio;

        while (!mes.isAfter(fim)) {

            BigDecimal indice =
                    indiceEconomicoRepository
                            .findByTipoAndReferencia(
                                    tipoIndice,
                                    mes)
                            .map(IndiceEconomico::getValor)
                            .orElse(BigDecimal.ZERO);

            BigDecimal fatorMes =
                    BigDecimal.ONE.add(
                            indice.divide(
                                    CEM,
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP));

            fator = fator.multiply(fatorMes);

            mes = mes.plusMonths(1);
        }

        return fator;
    }

    private BigDecimal calcularFatorTrIpcae(
            YearMonth inicioParcela,
            YearMonth dataAtualizacao) {

        YearMonth marcoTR =
                YearMonth.of(2015, 3);

        // Se a parcela já nasceu após o fim da TR
        if (inicioParcela.isAfter(marcoTR)) {

            return calcularFatorIndice(
                    TipoIndice.IPCA_E,
                    inicioParcela,
                    dataAtualizacao);
        }

        BigDecimal fatorTR =
                calcularFatorIndice(
                        TipoIndice.TR,
                        inicioParcela,
                        marcoTR);

        BigDecimal fatorIPCAE =
                calcularFatorIndice(
                        TipoIndice.IPCA_E,
                        YearMonth.of(2015, 4),
                        dataAtualizacao);

        return fatorTR.multiply(fatorIPCAE);
    }
}
