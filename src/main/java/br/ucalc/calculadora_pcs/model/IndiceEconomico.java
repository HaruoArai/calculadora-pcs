package br.ucalc.calculadora_pcs.model;

import br.ucalc.calculadora_pcs.model.enums.TipoIndice;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;

@Entity
@Table(name = "indice_economico", uniqueConstraints = @UniqueConstraint(columnNames = {"tipo", "referencia"}))
public class IndiceEconomico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoIndice tipo;

    private YearMonth referencia; // mês/ano de referência (ex: 2020-05)

    private BigDecimal valor; // valor percentual do índice naquele mês

    public IndiceEconomico() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoIndice getTipo() { return tipo; }
    public void setTipo(TipoIndice tipo) { this.tipo = tipo; }

    public YearMonth getReferencia() { return referencia; }
    public void setReferencia(YearMonth referencia) { this.referencia = referencia; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
