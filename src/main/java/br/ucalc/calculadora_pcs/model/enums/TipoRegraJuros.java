package br.ucalc.calculadora_pcs.model.enums;

public enum TipoRegraJuros {

    ACOMPANHA_PARCELA(
            "Juros variável por parcela"
    ),

    CONGELADO_CITACAO(
            "Período fixo pela citação"
    );

    private final String descricao;

    TipoRegraJuros(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
