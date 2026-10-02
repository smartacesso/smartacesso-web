package br.com.startjob.acesso.core.domain.permissao;

import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Matriz padrão perfil × permissão. Espelha {@code WebPermissaoMatriz} do legado.
 * ANALISTA permanece sem permissões padrão (comportamento atual do switch legado).
 */
public final class WebPermissaoMatriz {

    private WebPermissaoMatriz() {
    }

    public static Set<WebPermissao> permissoesPadrao(PerfilAcesso perfil) {
        if (perfil == null) {
            return Collections.emptySet();
        }
        return switch (perfil) {
            case ADMINISTRADOR -> EnumSet.allOf(WebPermissao.class);
            case GERENTE -> gerente();
            case OPERADOR -> operador();
            case PORTEIRO -> porteiro();
            case CUIDADOR -> cuidador();
            case RESPONSAVEL -> EnumSet.noneOf(WebPermissao.class);
            default -> Collections.emptySet();
        };
    }

    public static Set<String> codigosPadrao(PerfilAcesso perfil) {
        Set<String> codigos = new HashSet<>();
        for (WebPermissao p : permissoesPadrao(perfil)) {
            codigos.add(p.getCodigo());
        }
        return codigos;
    }

    private static Set<WebPermissao> gerente() {
        EnumSet<WebPermissao> set = EnumSet.copyOf(permissoesCadastroGerencial());
        set.addAll(permissoesRelatorios());
        set.add(WebPermissao.DISPOSITIVO_EQUIPAMENTOS_VER);
        set.add(WebPermissao.DISPOSITIVO_CAMERAS_VER);
        set.add(WebPermissao.DISPOSITIVO_COMANDO_EXECUTAR);
        set.add(WebPermissao.AJUDA_DOWNLOAD_DESKTOP_VER);
        set.add(WebPermissao.PEDESTRE_DADOS_SENSIVEIS_VER);
        set.add(WebPermissao.PEDESTRE_LINK_FACIAL_GERAR);
        set.add(WebPermissao.HISTORICO_ACESSO_VER);
        return set;
    }

    private static Set<WebPermissao> operador() {
        return EnumSet.of(
                WebPermissao.PEDESTRE_VER, WebPermissao.PEDESTRE_EDITAR,
                WebPermissao.VISITANTE_VER, WebPermissao.VISITANTE_EDITAR,
                WebPermissao.CADASTRO_ERRO_VER,
                WebPermissao.TOTEM_AUTO_VER,
                WebPermissao.EMPRESA_VER, WebPermissao.EMPRESA_EDITAR,
                WebPermissao.CORRESPONDENCIA_VER, WebPermissao.CORRESPONDENCIA_EDITAR,
                WebPermissao.DISPOSITIVO_EQUIPAMENTOS_VER,
                WebPermissao.DISPOSITIVO_CAMERAS_VER,
                WebPermissao.DISPOSITIVO_COMANDO_EXECUTAR,
                WebPermissao.HISTORICO_ACESSO_VER,
                WebPermissao.PEDESTRE_DADOS_SENSIVEIS_VER);
    }

    private static Set<WebPermissao> porteiro() {
        return EnumSet.of(
                WebPermissao.VISITANTE_VER, WebPermissao.VISITANTE_EDITAR,
                WebPermissao.CADASTRO_ERRO_VER,
                WebPermissao.TOTEM_AUTO_VER,
                WebPermissao.CORRESPONDENCIA_VER, WebPermissao.CORRESPONDENCIA_EDITAR,
                WebPermissao.DISPOSITIVO_EQUIPAMENTOS_VER,
                WebPermissao.DISPOSITIVO_CAMERAS_VER,
                WebPermissao.DISPOSITIVO_COMANDO_EXECUTAR,
                WebPermissao.HISTORICO_ACESSO_VER,
                WebPermissao.PEDESTRE_DADOS_SENSIVEIS_VER);
    }

    private static Set<WebPermissao> cuidador() {
        return EnumSet.of(
                WebPermissao.PEDESTRE_VER, WebPermissao.PEDESTRE_EDITAR,
                WebPermissao.VISITANTE_VER, WebPermissao.VISITANTE_EDITAR,
                WebPermissao.CADASTRO_ERRO_VER,
                WebPermissao.TOTEM_AUTO_VER,
                WebPermissao.CORRESPONDENCIA_VER, WebPermissao.CORRESPONDENCIA_EDITAR,
                WebPermissao.DISPOSITIVO_EQUIPAMENTOS_VER,
                WebPermissao.DISPOSITIVO_CAMERAS_VER,
                WebPermissao.DISPOSITIVO_COMANDO_EXECUTAR,
                WebPermissao.HISTORICO_ACESSO_VER);
    }

    private static Set<WebPermissao> permissoesCadastroGerencial() {
        return EnumSet.of(
                WebPermissao.PEDESTRE_VER, WebPermissao.PEDESTRE_EDITAR, WebPermissao.PEDESTRE_EXCLUIR,
                WebPermissao.VISITANTE_VER, WebPermissao.VISITANTE_EDITAR, WebPermissao.VISITANTE_EXCLUIR,
                WebPermissao.CADASTRO_ERRO_VER,
                WebPermissao.APROVACAO_TOTEM_VER, WebPermissao.APROVACAO_TOTEM_EDITAR,
                WebPermissao.CORRESPONDENCIA_VER, WebPermissao.CORRESPONDENCIA_EDITAR,
                WebPermissao.AVISO_APP_VER, WebPermissao.AVISO_APP_EDITAR,
                WebPermissao.TOTEM_AUTO_VER,
                WebPermissao.ALTERACAO_MASSA_VER, WebPermissao.ALTERACAO_MASSA_EDITAR,
                WebPermissao.USUARIO_VER, WebPermissao.USUARIO_EDITAR,
                WebPermissao.EMPRESA_VER, WebPermissao.EMPRESA_EDITAR);
    }

    private static Set<WebPermissao> permissoesRelatorios() {
        return EnumSet.of(
                WebPermissao.RELATORIO_PEDESTRE_VER,
                WebPermissao.RELATORIO_VISITANTE_VER,
                WebPermissao.RELATORIO_OCUPACAO_VER,
                WebPermissao.RELATORIO_LIBERACOES_VER,
                WebPermissao.RELATORIO_PERMANENCIA_VER,
                WebPermissao.RELATORIO_REFEICAO_VER,
                WebPermissao.HISTORICO_ACESSO_VER);
    }
}
