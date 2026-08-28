package br.com.startjob.acesso.domain.enumeration;

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
