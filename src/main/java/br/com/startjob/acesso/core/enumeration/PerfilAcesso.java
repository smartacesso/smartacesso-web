package br.com.startjob.acesso.core.enumeration;

public enum PerfilAcesso {
    ADMINISTRADOR,
    GERENTE,
    OPERADOR,
    PORTEIRO,
    RESPONSAVEL,
    /** @deprecated legado no banco; mesmo papel histórico de {@link #ANALISTA} */
    CUIDADOR,
    ANALISTA
}
