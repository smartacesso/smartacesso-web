package br.com.startjob.acesso.core.usecase.permissao;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaMatrizPermissaoWebUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPerfilPermissaoGateway webPerfilPermissaoGateway;

    private WebPermissaoUseCase webPermissaoUseCase;
    private ConsultaMatrizPermissaoWebUseCase consultaMatrizPermissaoWebUseCase;

    private Usuario usuarioAdmin;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        webPermissaoUseCase = new WebPermissaoUseCase(webPerfilPermissaoGateway);
        consultaMatrizPermissaoWebUseCase = new ConsultaMatrizPermissaoWebUseCase(usuarioGateway, webPermissaoUseCase);

        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        usuarioAdmin = Usuario.builder()
                .id(1L)
                .nome("Admin")
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .cliente(cliente)
                .build();
    }

    @Test
    @DisplayName("Deve retornar matriz padrão quando cliente não possui configuração customizada no banco")
    void deveRetornarMatrizPadraoQuandoClienteSemCustomizacao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(0L);

        List<WebPermissaoLinha> linhas = consultaMatrizPermissaoWebUseCase.execute();

        assertNotNull(linhas);
        assertEquals(WebPermissao.values().length, linhas.size());

        WebPermissaoLinha pedestreVer = linhas.stream()
                .filter(l -> l.getCodigo().equals(WebPermissao.PEDESTRE_VER.getCodigo()))
                .findFirst()
                .orElseThrow();
        assertEquals("Ver cadastro de pedestres", pedestreVer.getDescricao());
        assertTrue(pedestreVer.isAdministrador());
        assertTrue(pedestreVer.isGerente());
        assertTrue(pedestreVer.isOperador());
        assertFalse(pedestreVer.isPorteiro());
        assertTrue(pedestreVer.isCuidador());
        assertFalse(pedestreVer.isResponsavel());
    }

    @Test
    @DisplayName("Deve retornar matriz customizada do banco quando cliente possui registros salvos")
    void deveRetornarMatrizCustomizadaQuandoClientePossuiRegistros() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioAdmin));
        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(2L);

        WebPerfilPermissao permAdmin = WebPerfilPermissao.builder()
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .codigoPermissao(WebPermissao.CONFIG_WEB_PERMISSOES_VER.getCodigo())
                .habilitado(true)
                .build();
        WebPerfilPermissao permPorteiro = WebPerfilPermissao.builder()
                .perfil(PerfilAcesso.PORTEIRO)
                .codigoPermissao(WebPermissao.PEDESTRE_VER.getCodigo())
                .habilitado(true)
                .build();
        WebPerfilPermissao permOperadorDesabilitado = WebPerfilPermissao.builder()
                .perfil(PerfilAcesso.OPERADOR)
                .codigoPermissao(WebPermissao.PEDESTRE_VER.getCodigo())
                .habilitado(false)
                .build();

        when(webPerfilPermissaoGateway.findByClienteAndPerfil(10L, PerfilAcesso.ADMINISTRADOR))
                .thenReturn(List.of(permAdmin));
        when(webPerfilPermissaoGateway.findByClienteAtivo(10L))
                .thenReturn(List.of(permAdmin, permPorteiro, permOperadorDesabilitado));

        List<WebPermissaoLinha> linhas = consultaMatrizPermissaoWebUseCase.execute();

        WebPermissaoLinha pedestreVer = linhas.stream()
                .filter(l -> l.getCodigo().equals(WebPermissao.PEDESTRE_VER.getCodigo()))
                .findFirst()
                .orElseThrow();

        assertTrue(pedestreVer.isPorteiro());
        assertFalse(pedestreVer.isOperador());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possui permissão de visualizar nem editar matriz")
    void deveFalharQuandoUsuarioSemPermissao() {
        Usuario porteiro = Usuario.builder()
                .id(2L)
                .nome("Porteiro")
                .perfil(PerfilAcesso.PORTEIRO)
                .cliente(cliente)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(porteiro));
        when(webPerfilPermissaoGateway.countByClienteAtivo(10L)).thenReturn(0L);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaMatrizPermissaoWebUseCase.execute());
        assertEquals("Você não possui permissão para visualizar a matriz de permissões.", ex.getMessage());
    }
}
