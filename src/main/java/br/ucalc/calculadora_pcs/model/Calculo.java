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

    private LocalDate dataInicial;
    private LocalDate dataFinal;

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

    public List<ItemCalculo> getItens() { return itens; }
    public void setItens(List<ItemCalculo> itens) { this.itens = itens; }
}
