package br.ucalc.calculadora_pcs.service;

import br.ucalc.calculadora_pcs.model.Calculo;
import br.ucalc.calculadora_pcs.model.enums.TipoEmenda;
import br.ucalc.calculadora_pcs.model.IndiceEconomico;
import br.ucalc.calculadora_pcs.model.ItemCalculo;
import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import br.ucalc.calculadora_pcs.model.enums.TipoRegraJuros;
import br.ucalc.calculadora_pcs.repository.IndiceEconomicoRepository;
import org.springframework.stereotype.Service;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;

import br.ucalc.calculadora_pcs.dto.ParcelaFormDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
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

    public List<ItemCalculo> gerarTabela(
            Calculo calculo,
            List<ParcelaFormDTO> parcelas) {

        LocalDate dataFinal =
                calculo.getDataAtualizacao();

        YearMonth mesFinal =
                YearMonth.from(dataFinal);

        LocalDate dataFinalCorrecaoJuros = dataFinal;
        LocalDate dataInicioSelic = null;

        if (calculo.getTipoEmenda() == TipoEmenda.EC113) {

            dataFinalCorrecaoJuros = LocalDate.of(2021, 12, 8);

            dataInicioSelic = LocalDate.of(2021, 12, 9);
        }

        LocalDate dataCitacao =
                calculo.getDataCitacao();

        boolean semJuros =
                calculo.getTipoJuros() == TipoJuros.SEM_JUROS;

        List<ItemCalculo> itens = new ArrayList<>();

        for (ParcelaFormDTO parcela : parcelas) {

            LocalDate dataParcela =
                    parcela.getDataParcela();

            YearMonth mesInicial =
                    YearMonth.from(dataParcela);

            BigDecimal valorBase =
                    parcela.getValorParcela();

            YearMonth mesAtual = mesInicial;

            ItemCalculo item = new ItemCalculo();
            item.setCalculo(calculo);
            item.setData(dataParcela); // Assim o resultado mostra a data real da parcela, como 15/01/2025, e não sempre o dia 1
            item.setValorDevido(valorBase);

            // DATA DE INÍCIO DOS JUROS DA LINHA
            LocalDate inicioJurosLinha = null;

            if (!semJuros && dataCitacao != null) {
                switch (calculo.getTipoRegraJuros()) {

                    case CONGELADO_CITACAO:
                        inicioJurosLinha = dataCitacao;
                        break;

                    case ACOMPANHA_PARCELA:
                    default:
                        inicioJurosLinha =
                                dataParcela.isAfter(dataCitacao)
                                        ? dataParcela
                                        : dataCitacao;
                        break;
                }
            }

            // ---- Correção monetária (fator acumulado) ----
            BigDecimal fatorCorrecao =
                    calcularFatorCorrecao(
                            calculo.getTipoCorrecao(),
                            dataParcela,
                            dataFinalCorrecaoJuros);

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
                    (semJuros || inicioJurosLinha == null)
                            ? BigDecimal.ZERO
                            : calcularJurosAcumulado(
                            calculo.getTipoJuros(),
                            inicioJurosLinha,
                            dataFinalCorrecaoJuros);

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

            // ---- SELIC EC 113/2021 ----
            BigDecimal taxaSelic = BigDecimal.ZERO;
            BigDecimal valorSelic = BigDecimal.ZERO;

            if (calculo.getTipoEmenda() == TipoEmenda.EC113
                    && dataInicioSelic != null
                    && !dataInicioSelic.isAfter(dataFinal)) {

                LocalDate inicioSelicLinha =
                        dataParcela.isBefore(dataInicioSelic)
                                ? dataInicioSelic
                                : dataParcela;

                taxaSelic =
                        calcularSelicAcumulada(
                                inicioSelicLinha,
                                dataFinal);

                BigDecimal baseSelic =
                        valorAtualizado.add(valorJuros);

                valorSelic =
                        baseSelic
                                .multiply(
                                        taxaSelic.divide(
                                                CEM,
                                                ESCALA_INTERMEDIARIA,
                                                RoundingMode.HALF_UP))
                                .setScale(
                                        ESCALA_FINAL,
                                        RoundingMode.HALF_UP);
            }

            item.setTaxaSelic(taxaSelic);
            item.setValorSelic(valorSelic);

            // ---- Total ----
            item.setTotal(
                    valorAtualizado
                            .add(valorJuros)
                            .add(valorSelic));

            itens.add(item);
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
            case IGPM -> TipoIndice.IGPM;
        };
    }

    private BigDecimal buscarIndiceJuros(
            TipoJuros tipoJuros,
            YearMonth mes) {

        YearMonth marcoPoupanca = YearMonth.of(2009, 8);

        switch (tipoJuros) {

            case SEM_JUROS:
                return BigDecimal.ZERO;

            case MORA:
                return BigDecimal.valueOf(0.5);

            case MORA_POUPANCA:

                if (mes.isBefore(marcoPoupanca)) {
                    return BigDecimal.valueOf(0.5);
                }

                return indiceEconomicoRepository
                        .findByTipoAndReferencia(
                                TipoIndice.POUPANCA,
                                mes)
                        .map(IndiceEconomico::getValor)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Índice POUPANCA não cadastrado para " + mes));

            default:
                throw new IllegalStateException(
                        "Tipo de juros inválido");
        }
    }

    private BigDecimal calcularJurosAcumulado(
            TipoJuros tipoJuros,
            LocalDate inicio,
            LocalDate fim) {

        if (inicio.isAfter(fim)) {
            return BigDecimal.ZERO;
        }

        switch (tipoJuros) {

            case SEM_JUROS:
                return BigDecimal.ZERO;

            case MORA:
                return calcularJurosPorIndice(
                        TipoJuros.MORA,
                        inicio,
                        fim);

            case MORA_POUPANCA:
                return calcularJurosMoraPoupanca(
                        inicio,
                        fim);

            default:
                throw new IllegalStateException(
                        "Tipo de juros inválido");
        }
    }

    private BigDecimal calcularFatorCorrecao(
            TipoCorrecao tipoCorrecao,
            LocalDate inicio,
            LocalDate fim) {

        if (inicio.isAfter(fim)) {
            return BigDecimal.ONE;
        }

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
            case IGPM:
                return calcularFatorIndice(
                        TipoIndice.IGPM,
                        inicio,
                        fim);
            case SELIC:
                return calcularFatorSelicSimples(
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

    private BigDecimal calcularFatorSelicSimples(
            LocalDate inicio,
            LocalDate fim) {

        BigDecimal taxaAcumulada = BigDecimal.ZERO;

        YearMonth mes = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);

        while (!mes.isAfter(mesFim)) {

            int dias = diasProRata30(inicio, fim, mes);

            YearMonth mesReferencia = mes;

            BigDecimal indice =
                    indiceEconomicoRepository
                            .findByTipoAndReferencia(
                                    TipoIndice.SELIC,
                                    mesReferencia)
                            .map(IndiceEconomico::getValor)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Índice SELIC não cadastrado para " + mesReferencia));

            BigDecimal indiceProRata =
                    indice
                            .divide(BigDecimal.valueOf(30),
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(dias));

            taxaAcumulada = taxaAcumulada.add(indiceProRata);

            mes = mes.plusMonths(1);
        }

        return BigDecimal.ONE.add(
                taxaAcumulada.divide(
                        CEM,
                        ESCALA_INTERMEDIARIA,
                        RoundingMode.HALF_UP)
        ).setScale(7, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularFatorIndice(
            TipoIndice tipoIndice,
            LocalDate inicio,
            LocalDate fim) {

        BigDecimal fator = BigDecimal.ONE;

        YearMonth mes = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);

        while (!mes.isAfter(mesFim)) {

            int dias = diasProRata30(inicio, fim, mes);

            BigDecimal indice =
                    indiceEconomicoRepository
                            .findByTipoAndReferencia(
                                    tipoIndice,
                                    mes)
                            .map(IndiceEconomico::getValor)
                            .orElse(BigDecimal.ZERO);

            BigDecimal indiceProRata =
                    indice
                            .divide(BigDecimal.valueOf(30), ESCALA_INTERMEDIARIA, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(dias));

            BigDecimal fatorMes =
                    BigDecimal.ONE.add(
                            indiceProRata.divide(
                                    CEM,
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP));

            fator = fator.multiply(fatorMes).setScale(7, RoundingMode.HALF_UP);

            mes = mes.plusMonths(1);
        }

        return fator;
    }

    private BigDecimal calcularFatorTrIpcae(
            LocalDate inicioParcela,
            LocalDate dataAtualizacao) {

        LocalDate fimTR =
                LocalDate.of(2015, 3, 25);

        LocalDate inicioIPCAE =
                LocalDate.of(2015, 3, 26);

        if (inicioParcela.isAfter(dataAtualizacao)) {
            return BigDecimal.ONE;
        }

        // Se todo o período termina antes do IPCA-E, aplica só TR
        if (dataAtualizacao.isBefore(inicioIPCAE)) {
            return calcularFatorIndice(
                    TipoIndice.TR,
                    inicioParcela,
                    dataAtualizacao);
        }

        // Se a parcela já nasceu após o fim da TR
        if (inicioParcela.isAfter(fimTR)) {
            return calcularFatorIndice(
                    TipoIndice.IPCA_E,
                    inicioParcela,
                    dataAtualizacao);
        }

        BigDecimal fatorTR =
                calcularFatorIndice(
                        TipoIndice.TR,
                        inicioParcela,
                        fimTR);

        BigDecimal fatorIPCAE =
                calcularFatorIndice(
                        TipoIndice.IPCA_E,
                        inicioIPCAE,
                        dataAtualizacao);

        return fatorTR.multiply(fatorIPCAE).setScale(7, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularSelicAcumulada(
            LocalDate inicio,
            LocalDate fim) {

        BigDecimal acumulado = BigDecimal.ZERO;

        YearMonth mes = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);

        while (!mes.isAfter(mesFim)) {

            int dias = diasProRata30(inicio, fim, mes);

            YearMonth mesReferencia = mes;

            BigDecimal indiceSelic =
                    indiceEconomicoRepository
                            .findByTipoAndReferencia(
                                    TipoIndice.SELIC,
                                    mesReferencia)
                            .map(IndiceEconomico::getValor)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Índice SELIC não cadastrado para " + mesReferencia));

            BigDecimal indiceProRata =
                    indiceSelic
                            .divide(BigDecimal.valueOf(30),
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(dias));

            acumulado = acumulado.add(indiceProRata);

            mes = mes.plusMonths(1);
        }

        return acumulado;
    }

    private int diasProRata30(
            LocalDate inicio,
            LocalDate fim,
            YearMonth mes) {

        YearMonth mesInicio = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);

        int diaInicio = diaBase30(inicio);
        int diaFim = diaBase30(fim);

        if (mes.equals(mesInicio) && mes.equals(mesFim)) {
            return diaFim - diaInicio + 1;
        }

        if (mes.equals(mesInicio)) {
            return 30 - diaInicio + 1;
        }

        if (mes.equals(mesFim)) {
            return diaFim;
        }

        return 30;
    }

    private int diaBase30(LocalDate data) {
        boolean ultimoDiaFevereiro =
                data.getMonthValue() == 2
                        && data.getDayOfMonth() == data.lengthOfMonth();

        if (ultimoDiaFevereiro) {
            return 30;
        }

        return Math.min(data.getDayOfMonth(), 30);
    }

    private BigDecimal calcularJurosPorIndice(
            TipoJuros tipoJuros,
            LocalDate inicio,
            LocalDate fim) {

        BigDecimal acumulado = BigDecimal.ZERO;

        YearMonth mes = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);

        while (!mes.isAfter(mesFim)) {

            int dias =
                    diasProRata30(
                            inicio,
                            fim,
                            mes);

            BigDecimal indiceMes =
                    buscarIndiceJuros(
                            tipoJuros,
                            mes);

            BigDecimal indiceProRata =
                    indiceMes
                            .divide(
                                    BigDecimal.valueOf(30),
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP)
                            .multiply(
                                    BigDecimal.valueOf(dias));

            acumulado =
                    acumulado.add(indiceProRata);

            mes = mes.plusMonths(1);
        }

        return acumulado;
    }

    private BigDecimal calcularJurosMoraPoupanca(
            LocalDate inicio,
            LocalDate fim) {

        LocalDate fimMora =
                LocalDate.of(2009, 7, 31);

        LocalDate inicioPoupanca =
                LocalDate.of(2009, 8, 1);

        if (fim.isBefore(inicioPoupanca)) {
            return calcularJurosPorIndice(
                    TipoJuros.MORA,
                    inicio,
                    fim);
        }

        if (inicio.isAfter(fimMora)) {
            return calcularJurosPoupanca(
                    inicio,
                    fim);
        }

        BigDecimal jurosMora =
                calcularJurosPorIndice(
                        TipoJuros.MORA,
                        inicio,
                        fimMora);

        BigDecimal jurosPoupanca =
                calcularJurosPoupanca(
                        inicioPoupanca,
                        fim);

        return jurosMora.add(jurosPoupanca);
    }

    private BigDecimal calcularJurosPoupanca(
            LocalDate inicio,
            LocalDate fim) {

        BigDecimal acumulado = BigDecimal.ZERO;

        YearMonth mes = YearMonth.from(inicio);
        YearMonth mesFim = YearMonth.from(fim);

        while (!mes.isAfter(mesFim)) {

            int dias =
                    diasProRata30(
                            inicio,
                            fim,
                            mes);

            YearMonth mesReferencia = mes;

            BigDecimal indiceMes =
                    indiceEconomicoRepository
                            .findByTipoAndReferencia(
                                    TipoIndice.POUPANCA,
                                    mesReferencia)
                            .map(IndiceEconomico::getValor)
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "Índice POUPANCA não cadastrado para "
                                                    + mesReferencia));

            BigDecimal indiceProRata =
                    indiceMes
                            .divide(
                                    BigDecimal.valueOf(30),
                                    ESCALA_INTERMEDIARIA,
                                    RoundingMode.HALF_UP)
                            .multiply(
                                    BigDecimal.valueOf(dias));

            acumulado =
                    acumulado.add(indiceProRata);

            mes = mes.plusMonths(1);
        }

        return acumulado;
    }

}
