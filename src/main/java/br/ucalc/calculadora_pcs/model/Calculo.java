package br.ucalc.calculadora_pcs.model;

import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "calculo")
public class Calculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "processo_id")
    private Processo processo;

    private LocalDate dataInicioCorrecao;
    private LocalDate dataFimCorrecao;

    private LocalDate dataInicioJuros;
    private LocalDate dataFimJuros;

    private LocalDate competenciaInicial;
    private LocalDate competenciaFinal;

    @Enumerated(EnumType.STRING)
    private TipoCorrecao tipoCorrecao;

    @Enumerated(EnumType.STRING)
    private TipoJuros tipoJuros;

    // Valor devido inicial informado pelo usuário (base de cálculo do 1º mês)
    private BigDecimal valorDevidoInicial;

    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculo> itens = new ArrayList<>();

    public Calculo() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Processo getProcesso() { return processo; }
    public void setProcesso(Processo processo) { this.processo = processo; }

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

    public TipoCorrecao getTipoCorrecao() { return tipoCorrecao; }
    public void setTipoCorrecao(TipoCorrecao tipoCorrecao) { this.tipoCorrecao = tipoCorrecao; }

    public TipoJuros getTipoJuros() { return tipoJuros; }
    public void setTipoJuros(TipoJuros tipoJuros) { this.tipoJuros = tipoJuros; }

    public BigDecimal getValorDevidoInicial() { return valorDevidoInicial; }
    public void setValorDevidoInicial(BigDecimal valorDevidoInicial) { this.valorDevidoInicial = valorDevidoInicial; }

    public List<ItemCalculo> getItens() { return itens; }
    public void setItens(List<ItemCalculo> itens) { this.itens = itens; }
}
