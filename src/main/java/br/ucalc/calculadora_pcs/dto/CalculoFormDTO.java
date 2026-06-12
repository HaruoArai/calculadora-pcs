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

    @NotNull(message = "Informe a data inicial da correção")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicioCorrecao;

    @NotNull(message = "Informe a data final da correção")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFimCorrecao;

    @NotNull(message = "Informe a data inicial dos juros")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInicioJuros;

    @NotNull(message = "Informe a data final dos juros")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataFimJuros;

    @NotNull(message = "Informe a competência inicial")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate competenciaInicial;

    @NotNull(message = "Informe a competência final")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate competenciaFinal;

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

    public LocalDate getDataInicioCorrecao() {
        return dataInicioCorrecao;
    }
    public void setDataInicioCorrecao(LocalDate dataInicioCorrecao) {
        this.dataInicioCorrecao = dataInicioCorrecao;
    }

    public LocalDate getDataFimCorrecao() {
        return dataFimCorrecao;
    }
    public void setDataFimCorrecao(LocalDate dataFimCorrecao) {
        this.dataFimCorrecao = dataFimCorrecao;
    }

    public LocalDate getDataInicioJuros() {
        return dataInicioJuros;
    }
    public void setDataInicioJuros(LocalDate dataInicioJuros) {
        this.dataInicioJuros = dataInicioJuros;
    }

    public LocalDate getDataFimJuros() {
        return dataFimJuros;
    }
    public void setDataFimJuros(LocalDate dataFimJuros) {
        this.dataFimJuros = dataFimJuros;
    }

    public LocalDate getCompetenciaInicial() {
        return competenciaInicial;
    }
    public void setCompetenciaInicial(LocalDate competenciaInicial) {
        this.competenciaInicial = competenciaInicial;
    }

    public LocalDate getCompetenciaFinal() {
        return competenciaFinal;
    }
    public void setCompetenciaFinal(LocalDate competenciaFinal) {
        this.competenciaFinal = competenciaFinal;
    }

    public TipoCorrecao getTipoCorrecao() { return tipoCorrecao; }
    public void setTipoCorrecao(TipoCorrecao tipoCorrecao) { this.tipoCorrecao = tipoCorrecao; }

    public TipoJuros getTipoJuros() { return tipoJuros; }
    public void setTipoJuros(TipoJuros tipoJuros) { this.tipoJuros = tipoJuros; }

    public BigDecimal getValorDevidoInicial() { return valorDevidoInicial; }
    public void setValorDevidoInicial(BigDecimal valorDevidoInicial) { this.valorDevidoInicial = valorDevidoInicial; }
}
