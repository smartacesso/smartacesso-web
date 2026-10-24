package br.com.startjob.acesso.core.enumeration;

public enum TipoQRCode {

    ESTATICO("Estático"),
    DINAMICO_TEMPO("Dinâmico por tempo"),
    DINAMICO_USO("Dinâmico por uso");

    private final String descricao;

    TipoQRCode(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
