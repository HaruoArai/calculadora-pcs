package br.ucalc.calculadora_pcs.dto;

import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import br.ucalc.calculadora_pcs.model.enums.TipoEmenda;
import br.ucalc.calculadora_pcs.model.enums.TipoRegraJuros;

import java.time.LocalDate;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class CalculoFormDTO {

    @NotBlank(message = "Informe o autor")
    private String autor;

    @NotBlank(message = "Informe o réu")
    private String reu;

    private String autos;
    private String comarca;
    private String vara;
    private String pgenet;

    @NotNull(message = "Informe a data final da atualização")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAtualizacao;

    private TipoEmenda tipoEmenda = TipoEmenda.NENHUMA;

    private TipoRegraJuros tipoRegraJuros =
            TipoRegraJuros.ACOMPANHA_PARCELA;

    @NotNull(message = "Informe a data da citação")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataCitacao;

    @Valid
    private List<ParcelaFormDTO> parcelas = new ArrayList<>();

    @NotNull(message = "Selecione o tipo de correção")
    private TipoCorrecao tipoCorrecao;

    @NotNull(message = "Selecione o tipo de juros")
    private TipoJuros tipoJuros;

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

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public TipoEmenda getTipoEmenda() {
        return tipoEmenda;
    }
    public void setTipoEmenda(TipoEmenda tipoEmenda) {
        this.tipoEmenda = tipoEmenda;
    }

    public TipoRegraJuros getTipoRegraJuros() {
        return tipoRegraJuros;
    }
    public void setTipoRegraJuros(TipoRegraJuros tipoRegraJuros) {
        this.tipoRegraJuros = tipoRegraJuros;
    }

    public LocalDate getDataCitacao() {
        return dataCitacao;
    }
    public void setDataCitacao(LocalDate dataCitacao) {
        this.dataCitacao = dataCitacao;
    }

    public List<ParcelaFormDTO> getParcelas() {
        return parcelas;
    }
    public void setParcelas(List<ParcelaFormDTO> parcelas) {
        this.parcelas = parcelas;
    }

    public TipoCorrecao getTipoCorrecao() { return tipoCorrecao; }
    public void setTipoCorrecao(TipoCorrecao tipoCorrecao) { this.tipoCorrecao = tipoCorrecao; }

    public TipoJuros getTipoJuros() { return tipoJuros; }
    public void setTipoJuros(TipoJuros tipoJuros) { this.tipoJuros = tipoJuros; }

}
