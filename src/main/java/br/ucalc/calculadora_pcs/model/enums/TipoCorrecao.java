package br.ucalc.calculadora_pcs.model.enums;

public enum TipoCorrecao {
    TR(
            "TR (07/1994 em diante)"
    ),
    IPCAE(
            "IPCA-E (07/1994 em diante)"
    ),
    SELIC(
            "SELIC (07/1994 em diante)"
    ),
    TR_IPCAE(
            "TR (07/1994 até 25/03/2015) → de IPCA-E (26/03/2015 em diante)"
    ),
    TR_SELIC(
            "TR (07/1994 até 08/12/2021) → SELIC (09/12/2021 em diante)"
    ),
    IPCAE_SELIC(
            "IPCA-E (01/07/1994 até 08/12/2021) → SELIC (09/12/2021 em diante)"
    ),
    TR_IPCAE_SELIC(
            "TR (01/07/1994 até 25/03/2015) → IPCA-E (26/03/2015 até 08/12/2021) → SELIC (09/12/2021 em diante)"
    );

    private final String descricao;

    TipoCorrecao(String descricao) {
        this.descricao = descricao;
    }
    public String getDescricao() {
        return descricao;
    }
}
