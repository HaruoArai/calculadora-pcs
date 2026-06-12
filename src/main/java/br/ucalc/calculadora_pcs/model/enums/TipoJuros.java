package br.ucalc.calculadora_pcs.model.enums;

public enum TipoJuros {
    MORA_MENSAL("0,5% ao mês (Juros de Mora)"),
    POUPANCA("Caderneta de Poupança"),
    TODAS("Todos (Mora e Poupança)");

    private final String descricao;

    TipoJuros(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
