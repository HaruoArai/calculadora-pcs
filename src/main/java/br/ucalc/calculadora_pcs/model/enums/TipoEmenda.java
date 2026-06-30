package br.ucalc.calculadora_pcs.model.enums;

public enum TipoEmenda {

    NENHUMA("Nenhuma"),
    EC113("SELIC (EC 113/2021)"),
    EC136("SELIC até 09/09/2025 → IPCA + 2% (EC 136/2025)");

    private final String descricao;

    TipoEmenda(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
