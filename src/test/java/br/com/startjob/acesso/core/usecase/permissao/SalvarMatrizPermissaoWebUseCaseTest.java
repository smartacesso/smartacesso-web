package br.com.startjob.acesso.core.usecase.permissao;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarMatrizPermissaoWebUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPerfilPermissaoGateway webPerfilPermissaoGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    private SalvarMatrizPermissaoWebUseCase salvarMatrizPermissaoWebUseCase;

    private Usuario usuarioAdmin;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        salvarMatrizPermissaoWebUseCase = new SalvarMatrizPermissaoWebUseCase(
                usuarioGateway, webPerfilPermissaoGateway, webPermissaoUseCase);

        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        usuarioAdmin = Usuario.builder()
                .id(1L)
                .nome("Admin")
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("Deve salvar matriz de permissões do cliente com sucesso")
    @SuppressWarnings("unchecked")
    void deveSalvarMatrizComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioAdmin))
                .thenReturn(Set.of(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR.getCodigo()));

        WebPermissaoLinha linha = WebPermissaoLinha.builder()
                .codigo(WebPermissao.PEDESTRE_VER.getCodigo())
                .administrador(true)
                .gerente(true)
                .operador(false)
                .porteiro(true)
                .cuidador(false)
                .responsavel(true)
                .build();

        when(webPermissaoUseCase.carregarMatrizEfetiva(10L)).thenReturn(Map.of());
        when(webPermissaoUseCase.montarLinhas(any())).thenReturn(List.of(linha));

        List<WebPermissaoLinha> resultado = salvarMatrizPermissaoWebUseCase.execute(List.of(linha));

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        ArgumentCaptor<Map<PerfilAcesso, Set<String>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(webPerfilPermissaoGateway).salvarMatrizCliente(eq(10L), captor.capture());

        Map<PerfilAcesso, Set<String>> matrizSalva = captor.getValue();
        assertTrue(matrizSalva.get(PerfilAcesso.ADMINISTRADOR).contains("WEB_PEDESTRE_VER"));
        assertTrue(matrizSalva.get(PerfilAcesso.GERENTE).contains("WEB_PEDESTRE_VER"));
        assertFalse(matrizSalva.get(PerfilAcesso.OPERADOR).contains("WEB_PEDESTRE_VER"));
        assertTrue(matrizSalva.get(PerfilAcesso.PORTEIRO).contains("WEB_PEDESTRE_VER"));
        assertFalse(matrizSalva.get(PerfilAcesso.CUIDADOR).contains("WEB_PEDESTRE_VER"));
        assertTrue(matrizSalva.get(PerfilAcesso.RESPONSAVEL).contains("WEB_PEDESTRE_VER"));
    }

    @Test
    @DisplayName("Deve falhar quando código de permissão não existir no enum WebPermissao")
    void deveFalharQuandoCodigoInvalido() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioAdmin))
                .thenReturn(Set.of(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR.getCodigo()));

        WebPermissaoLinha linhaInvalida = WebPermissaoLinha.builder()
                .codigo("PERMISSAO_INEXISTENTE")
                .administrador(true)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> salvarMatrizPermissaoWebUseCase.execute(List.of(linhaInvalida)));
        assertEquals("Código de permissão inválido: PERMISSAO_INEXISTENTE", ex.getMessage());
        verify(webPerfilPermissaoGateway, never()).salvarMatrizCliente(anyLong(), any());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possuir permissão CONFIG_WEB_PERMISSOES_EDITAR")
    void deveFalharQuandoUsuarioSemPermissaoEditar() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioAdmin))
                .thenReturn(Set.of(WebPermissao.CONFIG_WEB_PERMISSOES_VER.getCodigo()));

        WebPermissaoLinha linha = WebPermissaoLinha.builder()
                .codigo(WebPermissao.PEDESTRE_VER.getCodigo())
                .administrador(true)
                .build();

        BusinessException ex = assertThrows(BusinessException.class,
                () -> salvarMatrizPermissaoWebUseCase.execute(List.of(linha)));
        assertEquals("Você não possui permissão para editar a matriz de permissões.", ex.getMessage());
        verify(webPerfilPermissaoGateway, never()).salvarMatrizCliente(anyLong(), any());
    }
}
