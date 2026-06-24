package br.ucalc.calculadora_pcs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParcelaFormDTO {

    @NotNull(message = "Informe a data da parcela")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataParcela;

    @NotNull(message = "Informe o valor da parcela")
    private BigDecimal valorParcela;

    public LocalDate getDataParcela() {
        return dataParcela;
    }

    public void setDataParcela(LocalDate dataParcela) {
        this.dataParcela = dataParcela;
    }

    public BigDecimal getValorParcela() {
        return valorParcela;
    }

    public void setValorParcela(BigDecimal valorParcela) {
        this.valorParcela = valorParcela;
    }
}