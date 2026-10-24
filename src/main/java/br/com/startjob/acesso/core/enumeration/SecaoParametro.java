package br.com.startjob.acesso.core.enumeration;

public enum SecaoParametro {

    GERAIS("Gerais"),
    CADASTRO("Cadastro"),
    CARTAO("Cartão e matrícula"),
    BIOMETRIA("Biometria"),
    FACIAL("Facial e Hikvision"),
    QR_CODE("QR Code"),
    ACESSO("Regras de acesso"),
    CATRACA("Catraca e leitores"),
    MENSAGENS("Mensagens da catraca"),
    MENSAGENS_AVANCADAS("Mensagens avançadas"),
    INTEGRACAO("Integrações"),
    ROTINAS("Rotinas automáticas");

    private final String titulo;

    SecaoParametro(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }
}
