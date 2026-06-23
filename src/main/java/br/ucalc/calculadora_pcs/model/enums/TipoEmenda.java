package br.ucalc.calculadora_pcs.model.enums;

public enum TipoEmenda {

    NENHUMA("Nenhuma"),
    EC113("EC 113/2021"),
    EC136("IPCA + 2% a.a. a partir de 09/2025");

    private final String descricao;

    TipoEmenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
