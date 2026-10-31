package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirPedestreUseCaseTest {

    @Mock
    private PedestreGateway pedestreGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ExcluirPedestreUseCase excluirPedestreUseCase;

    private Cliente clienteLogado;
    private Usuario usuarioLogado;

    @BeforeEach
    void setUp() {
        clienteLogado = Cliente.builder()
                .id(1L)
                .nome("Cliente Teste")
                .build();

        usuarioLogado = Usuario.builder()
                .id(10L)
                .nome("Admin")
                .cliente(clienteLogado)
                .build();
    }

    @Test
    @DisplayName("execute deve inativar, marcar como removido, preencher dataRemovido e limpar codigoCartaoAcesso ao excluir pedestre")
    void deveExcluirPedestreComSucesso() {
        Pedestre pedestre = Pedestre.builder()
                .id(100L)
                .nome("João da Silva")
                .tipo(TipoPedestre.PEDESTRE)
                .status(Status.ATIVO)
                .codigoCartaoAcesso("0000123456")
                .removido(false)
                .cliente(clienteLogado)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestre));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.PEDESTRE_EXCLUIR.getCodigo()));
        when(pedestreGateway.save(any(Pedestre.class))).thenAnswer(inv -> inv.getArgument(0));

        excluirPedestreUseCase.execute(100L);

        ArgumentCaptor<Pedestre> captor = ArgumentCaptor.forClass(Pedestre.class);
        verify(pedestreGateway).save(captor.capture());

        Pedestre salvo = captor.getValue();
        assertEquals(Status.INATIVO, salvo.getStatus());
        assertTrue(salvo.getRemovido());
        assertNotNull(salvo.getDataRemovido());
        assertNull(salvo.getCodigoCartaoAcesso());
    }

    @Test
    @DisplayName("execute deve exigir permissão VISITANTE_EXCLUIR quando o registro for do tipo VISITANTE")
    void deveExcluirVisitanteComSucesso() {
        Pedestre visitante = Pedestre.builder()
                .id(101L)
                .nome("Maria Visitante")
                .tipo(TipoPedestre.VISITANTE)
                .status(Status.ATIVO)
                .codigoCartaoAcesso("0000654321")
                .removido(false)
                .cliente(clienteLogado)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(101L)).thenReturn(Optional.of(visitante));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.VISITANTE_EXCLUIR.getCodigo()));

        excluirPedestreUseCase.execute(101L);

        assertEquals(Status.INATIVO, visitante.getStatus());
        assertTrue(visitante.getRemovido());
        assertNotNull(visitante.getDataRemovido());
        assertNull(visitante.getCodigoCartaoAcesso());
        verify(pedestreGateway).save(visitante);
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando id for nulo")
    void deveLancarExcecaoQuandoIdNulo() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(null));

        assertEquals("Identificador do pedestre não informado.", ex.getMessage());
        verifyNoInteractions(pedestreGateway);
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando usuário não estiver logado")
    void deveLancarExcecaoQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(100L));

        assertEquals("Usuário não está logado.", ex.getMessage());
        verifyNoInteractions(pedestreGateway);
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando pedestre não for encontrado")
    void deveLancarExcecaoQuandoPedestreNaoEncontrado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(100L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(100L));

        assertEquals("Pedestre não encontrado.", ex.getMessage());
        verify(pedestreGateway, never()).save(any());
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando pedestre já estiver removido")
    void deveLancarExcecaoQuandoPedestreJaRemovido() {
        Pedestre pedestreRemovido = Pedestre.builder()
                .id(100L)
                .tipo(TipoPedestre.PEDESTRE)
                .removido(true)
                .cliente(clienteLogado)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestreRemovido));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(100L));

        assertEquals("Pedestre não encontrado.", ex.getMessage());
        verify(pedestreGateway, never()).save(any());
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando pedestre pertencer a outro cliente")
    void deveLancarExcecaoQuandoPedestreDeOutroCliente() {
        Cliente outroCliente = Cliente.builder().id(2L).nome("Outro Cliente").build();
        Pedestre pedestreOutroCliente = Pedestre.builder()
                .id(100L)
                .tipo(TipoPedestre.PEDESTRE)
                .removido(false)
                .cliente(outroCliente)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestreOutroCliente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(100L));

        assertEquals("O pedestre não pertence ao seu cliente.", ex.getMessage());
        verify(pedestreGateway, never()).save(any());
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando usuário não tiver permissão para excluir pedestre")
    void deveLancarExcecaoQuandoSemPermissaoExcluirPedestre() {
        Pedestre pedestre = Pedestre.builder()
                .id(100L)
                .tipo(TipoPedestre.PEDESTRE)
                .removido(false)
                .cliente(clienteLogado)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestre));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.VISITANTE_EXCLUIR.getCodigo()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(100L));

        assertEquals("Você não possui permissão para excluir pedestres.", ex.getMessage());
        verify(pedestreGateway, never()).save(any());
    }

    @Test
    @DisplayName("execute deve lançar BusinessException quando usuário não tiver permissão para excluir visitante")
    void deveLancarExcecaoQuandoSemPermissaoExcluirVisitante() {
        Pedestre visitante = Pedestre.builder()
                .id(101L)
                .tipo(TipoPedestre.VISITANTE)
                .removido(false)
                .cliente(clienteLogado)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(101L)).thenReturn(Optional.of(visitante));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.PEDESTRE_EXCLUIR.getCodigo()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirPedestreUseCase.execute(101L));

        assertEquals("Você não possui permissão para excluir visitantes.", ex.getMessage());
        verify(pedestreGateway, never()).save(any());
    }
}
