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

    public ItemCalculo() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Calculo getCalculo() { return calculo; }
    public void setCalculo(Calculo calculo) { this.calculo = calculo; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

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
}
