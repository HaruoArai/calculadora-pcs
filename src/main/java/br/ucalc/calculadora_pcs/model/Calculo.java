package br.ucalc.calculadora_pcs.model;

import br.ucalc.calculadora_pcs.model.enums.TipoCorrecao;
import br.ucalc.calculadora_pcs.model.enums.TipoEmenda;
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

    private LocalDate dataAtualizacao; // até onde atualizar

    private LocalDate dataCitacao; // início dos juros

    private LocalDate dataParcela; // vencimento da parcela

    @Enumerated(EnumType.STRING)
    private TipoCorrecao tipoCorrecao;

    @Enumerated(EnumType.STRING)
    private TipoJuros tipoJuros;

    // VERIFICAR PRÓXIMA ETAPA
   // @Enumerated(EnumType.STRING)
    //private TipoEmenda tipoEmenda;

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

    //Colocar getters e setters NOVA EMENDAS

    public BigDecimal getValorDevidoInicial() { return valorDevidoInicial; }
    public void setValorDevidoInicial(BigDecimal valorDevidoInicial) { this.valorDevidoInicial = valorDevidoInicial; }

    public List<ItemCalculo> getItens() { return itens; }
    public void setItens(List<ItemCalculo> itens) { this.itens = itens; }
}
