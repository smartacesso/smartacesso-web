package br.com.startjob.acesso.core.enumeration;

import java.util.Arrays;

public enum WebPermissao {

    PEDESTRE_VER("WEB_PEDESTRE_VER", "Ver cadastro de pedestres"),
    PEDESTRE_EDITAR("WEB_PEDESTRE_EDITAR", "Editar cadastro de pedestres"),
    PEDESTRE_EXCLUIR("WEB_PEDESTRE_EXCLUIR", "Excluir pedestres"),
    PEDESTRE_DADOS_SENSIVEIS_VER("WEB_PEDESTRE_DADOS_SENSIVEIS_VER", "Ver dados sensíveis (CPF, e-mail, telefone)"),
    PEDESTRE_LINK_FACIAL_GERAR("WEB_PEDESTRE_LINK_FACIAL_GERAR", "Gerar link de cadastro facial"),

    VISITANTE_VER("WEB_VISITANTE_VER", "Ver cadastro de visitantes"),
    VISITANTE_EDITAR("WEB_VISITANTE_EDITAR", "Editar cadastro de visitantes"),
    VISITANTE_EXCLUIR("WEB_VISITANTE_EXCLUIR", "Excluir visitantes"),

    CADASTRO_ERRO_VER("WEB_CADASTRO_ERRO_VER", "Ver cadastros com erro"),
    APROVACAO_TOTEM_VER("WEB_APROVACAO_TOTEM_VER", "Ver aprovações pendentes (totem)"),
    APROVACAO_TOTEM_EDITAR("WEB_APROVACAO_TOTEM_EDITAR", "Aprovar/rejeitar cadastros do totem"),
    APROVACAO_LOGIN_APP_VER("WEB_APROVACAO_LOGIN_APP_VER", "Ver aprovações de login do app"),
    APROVACAO_LOGIN_APP_EDITAR("WEB_APROVACAO_LOGIN_APP_EDITAR", "Aprovar/rejeitar login do app"),

    CORRESPONDENCIA_VER("WEB_CORRESPONDENCIA_VER", "Ver correspondências"),
    CORRESPONDENCIA_EDITAR("WEB_CORRESPONDENCIA_EDITAR", "Editar correspondências"),

    AVISO_APP_VER("WEB_AVISO_APP_VER", "Ver avisos do app"),
    AVISO_APP_EDITAR("WEB_AVISO_APP_EDITAR", "Editar avisos do app"),

    TOTEM_AUTO_VER("WEB_TOTEM_AUTO_VER", "Cadastro automático (totem)"),
    ALTERACAO_MASSA_VER("WEB_ALTERACAO_MASSA_VER", "Ver alterações em massa"),
    ALTERACAO_MASSA_EDITAR("WEB_ALTERACAO_MASSA_EDITAR", "Executar alterações em massa"),

    USUARIO_VER("WEB_USUARIO_VER", "Ver usuários"),
    USUARIO_EDITAR("WEB_USUARIO_EDITAR", "Editar usuários"),

    ESTACAO_VER("WEB_ESTACAO_VER", "Ver máquinas"),
    ESTACAO_EDITAR("WEB_ESTACAO_EDITAR", "Editar máquinas"),

    EMPRESA_VER("WEB_EMPRESA_VER", "Ver empresas"),
    EMPRESA_EDITAR("WEB_EMPRESA_EDITAR", "Editar empresas"),

    RELATORIO_PEDESTRE_VER("WEB_RELATORIO_PEDESTRE_VER", "Relatório de pedestres"),
    RELATORIO_VISITANTE_VER("WEB_RELATORIO_VISITANTE_VER", "Relatório de visitantes"),
    RELATORIO_OCUPACAO_VER("WEB_RELATORIO_OCUPACAO_VER", "Relatório de ocupação"),
    RELATORIO_LIBERACOES_VER("WEB_RELATORIO_LIBERACOES_VER", "Relatório de liberações manuais"),
    RELATORIO_PERMANENCIA_VER("WEB_RELATORIO_PERMANENCIA_VER", "Relatório de permanência"),
    RELATORIO_REFEICAO_VER("WEB_RELATORIO_REFEICAO_VER", "Relatório de refeição"),
    HISTORICO_ACESSO_VER("WEB_HISTORICO_ACESSO_VER", "Histórico de acessos (desktop)"),

    CONFIG_RHID_VER("WEB_CONFIG_RHID_VER", "Ver configuração RHID"),
    CONFIG_RHID_EDITAR("WEB_CONFIG_RHID_EDITAR", "Editar configuração RHID"),
    CONFIG_REGRAS_VER("WEB_CONFIG_REGRAS_VER", "Ver regras de acesso"),
    CONFIG_REGRAS_EDITAR("WEB_CONFIG_REGRAS_EDITAR", "Editar regras de acesso"),
    CONFIG_PARAMETROS_VER("WEB_CONFIG_PARAMETROS_VER", "Ver parâmetros gerais"),
    CONFIG_PARAMETROS_EDITAR("WEB_CONFIG_PARAMETROS_EDITAR", "Editar parâmetros gerais"),
    CONFIG_WEB_PERMISSOES_VER("WEB_CONFIG_WEB_PERMISSOES_VER", "Ver matriz de permissões web"),
    CONFIG_WEB_PERMISSOES_EDITAR("WEB_CONFIG_WEB_PERMISSOES_EDITAR", "Editar matriz de permissões web"),

    DISPOSITIVO_EQUIPAMENTOS_VER("WEB_DISPOSITIVO_EQUIPAMENTOS_VER", "Equipamentos conectados"),
    DISPOSITIVO_CAMERAS_VER("WEB_DISPOSITIVO_CAMERAS_VER", "Câmeras Hikvision"),
    DISPOSITIVO_COMANDO_EXECUTAR("WEB_DISPOSITIVO_COMANDO_EXECUTAR", "Executar comando no desktop"),

    ADMIN_CLIENTES_VER("WEB_ADMIN_CLIENTES_VER", "Gestão de clientes"),
    ADMIN_CLIENTES_EDITAR("WEB_ADMIN_CLIENTES_EDITAR", "Editar clientes"),

    AJUDA_DOWNLOAD_DESKTOP_VER("WEB_AJUDA_DOWNLOAD_DESKTOP_VER", "Download do desktop");

    private final String codigo;
    private final String descricao;

    WebPermissao(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static WebPermissao porCodigo(String codigo) {
        if (codigo == null) {
            return null;
        }
        return Arrays.stream(values())
                .filter(p -> p.codigo.equals(codigo))
                .findFirst()
                .orElse(null);
    }
}
