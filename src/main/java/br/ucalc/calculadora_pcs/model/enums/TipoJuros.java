package br.ucalc.calculadora_pcs.model.enums;

public enum TipoJuros {
    SEM_JUROS(
            "Sem juros moratórios"
    ),
    MORA(
            "0,5% ao mês"
    ),
    MORA_POUPANCA(
            "0,5% ao mês → Caderneta da Poupança (01/08/2009 em diante)"
    );

    private final String descricao;

    TipoJuros(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
