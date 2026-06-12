package br.ucalc.calculadora_pcs.dto;

import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CalculoFormDTO {

    @NotBlank(message = "Informe o autor")
    private String autor;

    @NotBlank(message = "Informe o réu")
    private String reu;

    private String autos;
    private String comarca;
    private String vara;
    private String pgenet;

    @NotNull(message = "Informe a data inicial")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicial;

    @NotNull(message = "Informe a data final")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFinal;

    @NotNull(message = "Selecione o tipo de correção")
    private TipoCorrecao tipoCorrecao;

    @NotNull(message = "Selecione o tipo de juros")
    private TipoJuros tipoJuros;

    @NotNull(message = "Informe o valor devido")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valorDevidoInicial;

    // Getters e Setters
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getReu() { return reu; }
    public void setReu(String reu) { this.reu = reu; }

    public String getAutos() { return autos; }
    public void setAutos(String autos) { this.autos = autos; }

    public String getComarca() { return comarca; }
    public void setComarca(String comarca) { this.comarca = comarca; }

    public String getVara() { return vara; }
    public void setVara(String vara) { this.vara = vara; }

    public String getPgenet() { return pgenet; }
    public void setPgenet(String pgenet) { this.pgenet = pgenet; }

    public LocalDate getDataInicial() { return dataInicial; }
    public void setDataInicial(LocalDate dataInicial) { this.dataInicial = dataInicial; }

    public LocalDate getDataFinal() { return dataFinal; }
    public void setDataFinal(LocalDate dataFinal) { this.dataFinal = dataFinal; }

    public TipoCorrecao getTipoCorrecao() { return tipoCorrecao; }
    public void setTipoCorrecao(TipoCorrecao tipoCorrecao) { this.tipoCorrecao = tipoCorrecao; }

    public TipoJuros getTipoJuros() { return tipoJuros; }
    public void setTipoJuros(TipoJuros tipoJuros) { this.tipoJuros = tipoJuros; }

    public BigDecimal getValorDevidoInicial() { return valorDevidoInicial; }
    public void setValorDevidoInicial(BigDecimal valorDevidoInicial) { this.valorDevidoInicial = valorDevidoInicial; }
}
