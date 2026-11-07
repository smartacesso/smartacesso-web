package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
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
class ExcluirCorrespondenciaUseCaseTest {

    @Mock
    private CorrespondenciaGateway correspondenciaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ExcluirCorrespondenciaUseCase excluirCorrespondenciaUseCase;

    @Test
    @DisplayName("Deve excluir correspondência com sucesso (exclusão lógica) quando usuário tem permissão e pertence ao cliente")
    void deveExcluirCorrespondenciaComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Condomínio 10").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Porteiro").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .id(100L)
                .tipo("PACOTE")
                .removido(false)
                .cliente(cliente)
                .build();

        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondencia));

        excluirCorrespondenciaUseCase.execute(100L);

        ArgumentCaptor<Correspondencia> captor = ArgumentCaptor.forClass(Correspondencia.class);
        verify(correspondenciaGateway, times(1)).save(captor.capture());

        Correspondencia salva = captor.getValue();
        assertTrue(salva.getRemovido());
        assertNotNull(salva.getDataRemovido());
    }

    @Test
    @DisplayName("Deve lançar exceção quando identificador da correspondência for nulo")
    void deveLancarExcecaoQuandoIdNulo() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirCorrespondenciaUseCase.execute(null));

        assertEquals("Identificador da correspondência não informado.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, webPermissaoUseCase, correspondenciaGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não estiver logado")
    void deveLancarExcecaoQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirCorrespondenciaUseCase.execute(100L));

        assertEquals("Usuário não está logado.", ex.getMessage());
        verify(correspondenciaGateway, never()).findById(any());
        verify(correspondenciaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não possui permissão CORRESPONDENCIA_EDITAR")
    void deveLancarExcecaoQuandoUsuarioSemPermissao() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Operador").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of("OUTRA_PERMISSAO"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirCorrespondenciaUseCase.execute(100L));

        assertEquals("Você não possui permissão para excluir correspondências.", ex.getMessage());
        verify(correspondenciaGateway, never()).findById(any());
        verify(correspondenciaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando correspondência não for encontrada")
    void deveLancarExcecaoQuandoCorrespondenciaNaoEncontrada() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Porteiro").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirCorrespondenciaUseCase.execute(100L));

        assertEquals("Correspondência não encontrada.", ex.getMessage());
        verify(correspondenciaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando correspondência já estiver removida")
    void deveLancarExcecaoQuandoCorrespondenciaJaRemovida() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Porteiro").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .id(100L)
                .removido(true)
                .build();
        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondencia));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirCorrespondenciaUseCase.execute(100L));

        assertEquals("Correspondência não encontrada.", ex.getMessage());
        verify(correspondenciaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a correspondência não pertencer ao cliente do usuário logado")
    void deveLancarExcecaoQuandoCorrespondenciaDeOutroCliente() {
        Long idClienteLogado = 10L;
        Long idClienteCorrespondencia = 20L;

        Usuario usuarioLogado = Usuario.builder()
                .id(1L)
                .cliente(Cliente.builder().id(idClienteLogado).build())
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .id(100L)
                .removido(false)
                .cliente(Cliente.builder().id(idClienteCorrespondencia).build())
                .build();
        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondencia));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirCorrespondenciaUseCase.execute(100L));

        assertEquals("A correspondência não pertence ao seu cliente.", ex.getMessage());
        verify(correspondenciaGateway, never()).save(any());
    }
}
