package br.ucalc.calculadora_pcs.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "parcela_calculo")
public class ParcelaCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataParcela;

    private BigDecimal valorParcela;

    @ManyToOne
    @JoinColumn(name = "calculo_id")
    private Calculo calculo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataParcela() { return dataParcela; }
    public void setDataParcela(LocalDate dataParcela) { this.dataParcela = dataParcela; }

    public BigDecimal getValorParcela() { return valorParcela; }
    public void setValorParcela(BigDecimal valorParcela) { this.valorParcela = valorParcela; }

    public Calculo getCalculo() { return calculo; }
    public void setCalculo(Calculo calculo) { this.calculo = calculo; }
}
