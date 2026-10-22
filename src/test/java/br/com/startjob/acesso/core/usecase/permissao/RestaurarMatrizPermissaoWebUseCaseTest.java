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
class RestaurarMatrizPermissaoWebUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPerfilPermissaoGateway webPerfilPermissaoGateway;

    private WebPermissaoUseCase webPermissaoUseCase;
    private RestaurarMatrizPermissaoWebUseCase restaurarMatrizPermissaoWebUseCase;

    private Cliente cliente;
    private Usuario usuarioAdmin;

    @BeforeEach
    void setUp() {
        webPermissaoUseCase = new WebPermissaoUseCase(webPerfilPermissaoGateway);
        restaurarMatrizPermissaoWebUseCase = new RestaurarMatrizPermissaoWebUseCase(
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
    @DisplayName("Deve restaurar a matriz de permissões padrão para o cliente do usuário logado e retornar permissões atualizadas")
    @SuppressWarnings("unchecked")
    void deveRestaurarPadraoComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(0L);

        List<WebPermissaoLinha> resultado = restaurarMatrizPermissaoWebUseCase.execute();

        assertNotNull(resultado);
        assertEquals(WebPermissao.values().length, resultado.size());

        ArgumentCaptor<Map<PerfilAcesso, Set<String>>> captor = ArgumentCaptor.forClass(Map.class);
        verify(webPerfilPermissaoGateway).salvarMatrizCliente(eq(10L), captor.capture());

        Map<PerfilAcesso, Set<String>> matrizRestaurada = captor.getValue();
        assertEquals(WebPermissao.values().length, matrizRestaurada.get(PerfilAcesso.ADMINISTRADOR).size());
        assertTrue(matrizRestaurada.get(PerfilAcesso.GERENTE).contains(WebPermissao.PEDESTRE_VER.getCodigo()));
        assertTrue(matrizRestaurada.get(PerfilAcesso.RESPONSAVEL).isEmpty());
    }

    @Test
    @DisplayName("Deve falhar quando usuário logado não possuir permissão CONFIG_WEB_PERMISSOES_EDITAR")
    void deveFalharQuandoUsuarioSemPermissao() {
        Usuario gerente = Usuario.builder()
                .id(2L)
                .nome("Gerente")
                .perfil(PerfilAcesso.GERENTE)
                .cliente(cliente)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(gerente));
        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(0L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> restaurarMatrizPermissaoWebUseCase.execute());
        assertEquals("Você não possui permissão para restaurar a matriz de permissões.", ex.getMessage());
        verify(webPerfilPermissaoGateway, never()).salvarMatrizCliente(anyLong(), any());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não estiver autenticado")
    void deveFalharQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> restaurarMatrizPermissaoWebUseCase.execute());
        assertEquals("Usuário não está logado.", ex.getMessage());
    }
}
