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

    private BigDecimal valorDevido;       // base de cálculo do mês
    private BigDecimal indiceCorrecao;    // ex: índice IPCA-E do mês (em %)
    private BigDecimal valorAtualizado;   // valorDevido corrigido

    private BigDecimal indiceJuros;       // ex: 0,5% ou rendimento da poupança
    private BigDecimal valorJuros;        // valor dos juros do mês

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

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
