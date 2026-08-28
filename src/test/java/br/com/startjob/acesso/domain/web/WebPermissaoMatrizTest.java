package br.com.startjob.acesso.domain.web;

import br.com.startjob.acesso.domain.enumeration.PerfilAcesso;
import br.com.startjob.acesso.domain.enumeration.WebPermissao;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebPermissaoMatrizTest {

    @Test
    void administradorRecebeTodasAsPermissoes() {
        assertThat(WebPermissaoMatriz.permissoesPadrao(PerfilAcesso.ADMINISTRADOR))
                .containsExactlyInAnyOrder(WebPermissao.values());
    }

    @Test
    void porteiroNaoEditaPedestreNemUsuarios() {
        var codigos = WebPermissaoMatriz.codigosPadrao(PerfilAcesso.PORTEIRO);
        assertThat(codigos).contains("WEB_VISITANTE_VER", "WEB_VISITANTE_EDITAR");
        assertThat(codigos).doesNotContain("WEB_PEDESTRE_VER", "WEB_USUARIO_EDITAR", "WEB_ADMIN_CLIENTES_VER");
    }

    @Test
    void analistaPermaneceSemPermissoesPadraoComoNoLegado() {
        assertThat(WebPermissaoMatriz.permissoesPadrao(PerfilAcesso.ANALISTA)).isEmpty();
    }

    @Test
    void responsavelSemPermissoesWeb() {
        assertThat(WebPermissaoMatriz.permissoesPadrao(PerfilAcesso.RESPONSAVEL)).isEmpty();
    }
}
