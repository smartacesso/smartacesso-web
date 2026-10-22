package br.com.startjob.acesso.core.usecase.login;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebPermissaoUseCaseTest {

    @Mock
    private WebPerfilPermissaoGateway webPerfilPermissaoGateway;

    private WebPermissaoUseCase webPermissaoUseCase;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        webPermissaoUseCase = new WebPermissaoUseCase(webPerfilPermissaoGateway);
        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
    }

    @Test
    @DisplayName("Deve resolver permissões padrão quando cliente não possui matriz customizada no banco")
    void deveResolverPermissoesPadraoQuandoSemCustomizacao() {
        Usuario gerente = Usuario.builder()
                .id(1L)
                .perfil(PerfilAcesso.GERENTE)
                .cliente(cliente)
                .build();

        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(0L);

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(gerente);

        assertTrue(permissoes.contains(WebPermissao.EMPRESA_EDITAR.getCodigo()));
        assertTrue(permissoes.contains(WebPermissao.USUARIO_EDITAR.getCodigo()));
        assertFalse(permissoes.contains(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));
    }

    @Test
    @DisplayName("Deve refletir permissões customizadas salvas no banco (habilitando ou revogando permissões do perfil)")
    void deveRefletirPermissoesCustomizadasSalvasNoBanco() {
        Usuario gerente = Usuario.builder()
                .id(1L)
                .perfil(PerfilAcesso.GERENTE)
                .cliente(cliente)
                .build();

        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(2L);
        when(webPerfilPermissaoGateway.findByClienteAndPerfil(10L, PerfilAcesso.GERENTE))
                .thenReturn(List.of(
                        // Revogou EMPRESA_EDITAR (que por padrão era true para GERENTE)
                        WebPerfilPermissao.builder()
                                .perfil(PerfilAcesso.GERENTE)
                                .codigoPermissao(WebPermissao.EMPRESA_EDITAR.getCodigo())
                                .habilitado(false)
                                .build(),
                        // Concedeu CONFIG_REGRAS_EDITAR (que por padrão era false para GERENTE)
                        WebPerfilPermissao.builder()
                                .perfil(PerfilAcesso.GERENTE)
                                .codigoPermissao(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo())
                                .habilitado(true)
                                .build()
                ));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(gerente);

        assertFalse(permissoes.contains(WebPermissao.EMPRESA_EDITAR.getCodigo()));
        assertTrue(permissoes.contains(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));
        // Permissões não presentes no banco continuam recebendo o fallback padrão do perfil
        assertTrue(permissoes.contains(WebPermissao.USUARIO_EDITAR.getCodigo()));
    }
}
