package br.ucalc.calculadora_pcs.model;

import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoEmenda;
import br.ucalc.calculadora_pcs.model.enums.TipoJuros;
import br.ucalc.calculadora_pcs.model.enums.TipoRegraJuros;
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

    private LocalDate dataAtualizacao; // até onde atualizar

    private LocalDate dataCitacao; // início dos juros

    private LocalDate dataParcela; // vencimento da parcela

    @Enumerated(EnumType.STRING)
    private TipoCorrecao tipoCorrecao;

    @Enumerated(EnumType.STRING)
    private TipoJuros tipoJuros;

    @Enumerated(EnumType.STRING)
    private TipoRegraJuros tipoRegraJuros;

    @Enumerated(EnumType.STRING)
    private TipoEmenda tipoEmenda;

    @Column(nullable = false)
    private Boolean aplicarHonorarios = false;

    @Column(precision = 10, scale = 4)
    private BigDecimal percentualHonorarios;

    @Column(precision = 19, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal valorHonorarios = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalGeral = BigDecimal.ZERO;

    // Valor devido inicial informado pelo usuário (base de cálculo do 1º mês)
    private BigDecimal valorDevidoInicial;

    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCalculo> itens = new ArrayList<>();

    @OneToMany(mappedBy = "calculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParcelaCalculo> parcelas = new ArrayList<>();

    public Calculo() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Processo getProcesso() { return processo; }
    public void setProcesso(Processo processo) { this.processo = processo; }

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public LocalDate getDataCitacao() {
        return dataCitacao;
    }
    public void setDataCitacao(LocalDate dataCitacao) {
        this.dataCitacao = dataCitacao;
    }

    public LocalDate getDataParcela() {
        return dataParcela;
    }
    public void setDataParcela(LocalDate dataParcela) {
        this.dataParcela = dataParcela;
    }

    public TipoCorrecao getTipoCorrecao() { return tipoCorrecao; }
    public void setTipoCorrecao(TipoCorrecao tipoCorrecao) { this.tipoCorrecao = tipoCorrecao; }

    public TipoJuros getTipoJuros() { return tipoJuros; }
    public void setTipoJuros(TipoJuros tipoJuros) { this.tipoJuros = tipoJuros; }

    public TipoRegraJuros getTipoRegraJuros() {
        return tipoRegraJuros;
    }
    public void setTipoRegraJuros(TipoRegraJuros tipoRegraJuros) {
        this.tipoRegraJuros = tipoRegraJuros;
    }

    public TipoEmenda getTipoEmenda() {
        return tipoEmenda;
    }
    public void setTipoEmenda(TipoEmenda tipoEmenda) {
        this.tipoEmenda = tipoEmenda;
    }

    public Boolean getAplicarHonorarios() {
        return Boolean.TRUE.equals(aplicarHonorarios);
    }
    public void setAplicarHonorarios(Boolean aplicarHonorarios) {
        this.aplicarHonorarios = Boolean.TRUE.equals(aplicarHonorarios);
    }

    public BigDecimal getPercentualHonorarios() {
        return percentualHonorarios;
    }
    public void setPercentualHonorarios(BigDecimal percentualHonorarios) {
        this.percentualHonorarios = percentualHonorarios;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getValorHonorarios() {
        return valorHonorarios;
    }
    public void setValorHonorarios(BigDecimal valorHonorarios) {
        this.valorHonorarios = valorHonorarios;
    }

    public BigDecimal getTotalGeral() {
        return totalGeral;
    }
    public void setTotalGeral(BigDecimal totalGeral) {
        this.totalGeral = totalGeral;
    }

    public BigDecimal getValorDevidoInicial() { return valorDevidoInicial; }
    public void setValorDevidoInicial(BigDecimal valorDevidoInicial) { this.valorDevidoInicial = valorDevidoInicial; }

    public List<ItemCalculo> getItens() { return itens; }
    public void setItens(List<ItemCalculo> itens) { this.itens = itens; }

    public List<ParcelaCalculo> getParcelas() {
        return parcelas;
    }
    public void setParcelas(List<ParcelaCalculo> parcelas) {
        this.parcelas = parcelas;
    }
}
