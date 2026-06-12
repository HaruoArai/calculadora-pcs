package br.ucalc.calculadora_pcs.model.enums;

public enum TipoCorrecao {
    IPCA_E("IPCA-E"),
    TR("TR"),
    SELIC("Selic"),
    TODAS("Todos os índices (IPCA-E, TR e Selic)");

    private final String descricao;

    TipoCorrecao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
