package br.ucalc.calculadora_pcs.model.enums;

public enum TipoCorrecao {
    TR(
            "TR (07/1994 em diante)"
    ),
    IPCA_E(
            "IPCA-E (07/1994 em diante)"
    ),
    IGPM(
            "IGP-M (07/1994 em diante)"
    ),
    SELIC(
            "SELIC (07/1994 em diante)"
    ),
    TR_IPCAE(
            "TR (07/1994 até 25/03/2015) → de IPCA-E (26/03/2015 em diante)"
    );

    private final String descricao;

    TipoCorrecao(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
