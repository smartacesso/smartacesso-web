package br.com.startjob.acesso.core.usecase.empresa;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirEmpresaUseCaseTest {

    @Mock
    private EmpresaGateway empresaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ExcluirEmpresaUseCase excluirEmpresaUseCase;

    @Test
    @DisplayName("Deve excluir empresa com sucesso (exclusão lógica) quando usuário tem permissão e empresa pertence ao mesmo cliente")
    void deveExcluirEmpresaComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente 10").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder()
                .id(100L)
                .nome("Empresa Alfa")
                .removido(false)
                .cliente(cliente)
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        excluirEmpresaUseCase.execute(100L);

        ArgumentCaptor<Empresa> captor = ArgumentCaptor.forClass(Empresa.class);
        verify(empresaGateway, times(1)).save(captor.capture());

        Empresa empresaSalva = captor.getValue();
        assertTrue(empresaSalva.getRemovido());
        assertNotNull(empresaSalva.getDataRemovido());
    }

    @Test
    @DisplayName("Deve lançar exceção quando identificador da empresa for nulo")
    void deveLancarExcecaoQuandoIdNulo() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirEmpresaUseCase.execute(null));

        assertEquals("Identificador da empresa não informado.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, webPermissaoUseCase, empresaGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não estiver logado")
    void deveLancarExcecaoQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirEmpresaUseCase.execute(100L));

        assertEquals("Usuário não está logado.", ex.getMessage());
        verify(empresaGateway, never()).findById(any());
        verify(empresaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não possui permissão EMPRESA_EDITAR")
    void deveLancarExcecaoQuandoUsuarioSemPermissao() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Operador").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of("OUTRA_PERMISSAO"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirEmpresaUseCase.execute(100L));

        assertEquals("Você não possui permissão para excluir empresas.", ex.getMessage());
        verify(empresaGateway, never()).findById(any());
        verify(empresaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando empresa não for encontrada")
    void deveLancarExcecaoQuandoEmpresaNaoEncontrada() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));
        when(empresaGateway.findById(100L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirEmpresaUseCase.execute(100L));

        assertEquals("Empresa não encontrada.", ex.getMessage());
        verify(empresaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando empresa já estiver removida")
    void deveLancarExcecaoQuandoEmpresaJaRemovida() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder()
                .id(100L)
                .removido(true)
                .build();
        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirEmpresaUseCase.execute(100L));

        assertEquals("Empresa não encontrada.", ex.getMessage());
        verify(empresaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a empresa não pertencer ao cliente do usuário logado")
    void deveLancarExcecaoQuandoEmpresaDeOutroCliente() {
        Long idClienteLogado = 10L;
        Long idClienteEmpresa = 20L;

        Usuario usuarioLogado = Usuario.builder()
                .id(1L)
                .cliente(Cliente.builder().id(idClienteLogado).build())
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder()
                .id(100L)
                .removido(false)
                .cliente(Cliente.builder().id(idClienteEmpresa).build())
                .build();
        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirEmpresaUseCase.execute(100L));

        assertEquals("A empresa não pertence ao seu cliente.", ex.getMessage());
        verify(empresaGateway, never()).save(any());
    }
}
