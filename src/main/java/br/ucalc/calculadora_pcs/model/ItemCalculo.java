package br.ucalc.calculadora_pcs.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "item_calculo")
public class ItemCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "calculo_id")
    private Calculo calculo;

    private LocalDate data;

    @Column(length = 120)
    private String descricaoParcela;

    private BigDecimal valorDevido;

    @Column(precision = 38, scale = 7) // base de cálculo do mês
    private BigDecimal indiceCorrecao;    // ex: índice IPCA-E do mês (em %)
    private BigDecimal valorAtualizado;   // valorDevido corrigido

    @Column(precision = 38, scale = 7)
    private BigDecimal indiceJuros;       // ex: 0,5% ou rendimento da poupança
    private BigDecimal valorJuros;        // valor dos juros do mês

    @Column(precision = 38, scale = 7)
    private BigDecimal taxaSelic;
    private BigDecimal valorSelic;

    private BigDecimal total;             // valorAtualizado + valorJuros

    // EC 136/2025
    private BigDecimal baseEc136;

    @Column(precision = 38, scale = 7)
    private BigDecimal correcaoSelicIpca;

    private BigDecimal valorCorrecaoSelicIpca;

    @Column(precision = 38, scale = 7)
    private BigDecimal jurosSelic;

    private BigDecimal valorJurosSelic;

    @Column(precision = 38, scale = 7)
    private BigDecimal indexadorIpca;

    private BigDecimal valorAtualizadoEc136;

    private BigDecimal correcaoSobreJurosSelic;

    @Column(precision = 38, scale = 7)
    private BigDecimal taxaJurosDoisPorCento;

    private BigDecimal valorJurosEc136;

    private BigDecimal valorTotalEc136;

    public ItemCalculo() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Calculo getCalculo() { return calculo; }
    public void setCalculo(Calculo calculo) { this.calculo = calculo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public String getDescricaoParcela() {
        return descricaoParcela;
    }
    public void setDescricaoParcela(String descricaoParcela) {
        this.descricaoParcela = descricaoParcela;
    }

    public BigDecimal getValorDevido() { return valorDevido; }
    public void setValorDevido(BigDecimal valorDevido) { this.valorDevido = valorDevido; }

    public BigDecimal getIndiceCorrecao() { return indiceCorrecao; }
    public void setIndiceCorrecao(BigDecimal indiceCorrecao) { this.indiceCorrecao = indiceCorrecao; }

    public BigDecimal getValorAtualizado() { return valorAtualizado; }
    public void setValorAtualizado(BigDecimal valorAtualizado) { this.valorAtualizado = valorAtualizado; }

    public BigDecimal getIndiceJuros() { return indiceJuros; }
    public void setIndiceJuros(BigDecimal indiceJuros) { this.indiceJuros = indiceJuros; }

    public BigDecimal getValorJuros() { return valorJuros; }
    public void setValorJuros(BigDecimal valorJuros) { this.valorJuros = valorJuros; }

    public BigDecimal getTaxaSelic() {
        return taxaSelic;
    }
    public void setTaxaSelic(BigDecimal taxaSelic) {
        this.taxaSelic = taxaSelic;
    }

    public BigDecimal getValorSelic() {
        return valorSelic;
    }
    public void setValorSelic(BigDecimal valorSelic) {
        this.valorSelic = valorSelic;
    }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public BigDecimal getBaseEc136() {
        return baseEc136;
    }
    public void setBaseEc136(BigDecimal baseEc136) {
        this.baseEc136 = baseEc136;
    }

    public BigDecimal getCorrecaoSelicIpca() {
        return correcaoSelicIpca;
    }
    public void setCorrecaoSelicIpca(BigDecimal correcaoSelicIpca) {
        this.correcaoSelicIpca = correcaoSelicIpca;
    }

    public BigDecimal getValorCorrecaoSelicIpca() {
        return valorCorrecaoSelicIpca;
    }
    public void setValorCorrecaoSelicIpca(BigDecimal valorCorrecaoSelicIpca) {
        this.valorCorrecaoSelicIpca = valorCorrecaoSelicIpca;
    }

    public BigDecimal getJurosSelic() {
        return jurosSelic;
    }
    public void setJurosSelic(BigDecimal jurosSelic) {
        this.jurosSelic = jurosSelic;
    }

    public BigDecimal getValorJurosSelic() {
        return valorJurosSelic;
    }
    public void setValorJurosSelic(BigDecimal valorJurosSelic) {
        this.valorJurosSelic = valorJurosSelic;
    }

    public BigDecimal getValorAtualizadoEc136() {
        return valorAtualizadoEc136;
    }
    public void setValorAtualizadoEc136(BigDecimal valorAtualizadoEc136) {
        this.valorAtualizadoEc136 = valorAtualizadoEc136;
    }

    public BigDecimal getIndexadorIpca() {
        return indexadorIpca;
    }
    public void setIndexadorIpca(BigDecimal indexadorIpca) {
        this.indexadorIpca = indexadorIpca;
    }

    public BigDecimal getCorrecaoSobreJurosSelic() {
        return correcaoSobreJurosSelic;
    }
    public void setCorrecaoSobreJurosSelic(BigDecimal correcaoSobreJurosSelic) {
        this.correcaoSobreJurosSelic = correcaoSobreJurosSelic;
    }

    public BigDecimal getTaxaJurosDoisPorCento() {
        return taxaJurosDoisPorCento;
    }
    public void setTaxaJurosDoisPorCento(BigDecimal taxaJurosDoisPorCento) {
        this.taxaJurosDoisPorCento = taxaJurosDoisPorCento;
    }

    public BigDecimal getValorTotalEc136() {
        return valorTotalEc136;
    }
    public void setValorTotalEc136(BigDecimal valorTotalEc136) {
        this.valorTotalEc136 = valorTotalEc136;
    }

    public BigDecimal getValorJurosEc136() {
        return valorJurosEc136;
    }
    public void setValorJurosEc136(BigDecimal valorJurosEc136) {
        this.valorJurosEc136 = valorJurosEc136;
    }
}
