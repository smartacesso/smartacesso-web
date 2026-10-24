package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaPedestreUseCaseTest {

    @Mock
    private PedestreGateway pedestreGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ConsultaPedestreUseCase consultaPedestreUseCase;

    @Test
    @DisplayName("Deve buscar pedestre por ID com sucesso para o cliente do usuário logado e limpar a senha")
    void deveBuscarPedestrePorIdComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Pedestre pedestre = Pedestre.builder()
                .id(100L)
                .nome("João da Silva")
                .tipo(TipoPedestre.PEDESTRE)
                .status(Status.ATIVO)
                .login("joao.silva")
                .senha("HASH_SECRETO")
                .cliente(cliente)
                .removido(false)
                .build();

        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestre));

        Pedestre resultado = consultaPedestreUseCase.buscarPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("João da Silva", resultado.getNome());
        assertEquals("joao.silva", resultado.getLogin());
        assertNull(resultado.getSenha());
        verify(usuarioGateway, times(1)).findLoggedUser();
        verify(pedestreGateway, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando id for nulo em buscarPorId")
    void deveLancarExcecaoQuandoIdNuloEmBuscarPorId() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.buscarPorId(null));

        assertEquals("Identificador do pedestre não informado.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, pedestreGateway);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário não estiver logado em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioNaoLogadoEmBuscarPorId() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.buscarPorId(100L));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verify(pedestreGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não possuir cliente em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioSemClienteEmBuscarPorId() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.buscarPorId(100L));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verify(pedestreGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando pedestre não for encontrado em buscarPorId")
    void deveLancarExcecaoQuandoPedestreNaoEncontradoEmBuscarPorId() {
        Cliente cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(pedestreGateway.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.buscarPorId(999L));

        assertEquals("Pedestre não encontrado.", ex.getMessage());
        verify(pedestreGateway, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando pedestre estiver com removido=true em buscarPorId")
    void deveLancarExcecaoQuandoPedestreRemovidoEmBuscarPorId() {
        Cliente cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Pedestre pedestreRemovido = Pedestre.builder()
                .id(100L)
                .nome("Pedestre Removido")
                .cliente(cliente)
                .removido(true)
                .build();

        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestreRemovido));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.buscarPorId(100L));

        assertEquals("Pedestre não encontrado.", ex.getMessage());
        verify(pedestreGateway, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando pedestre pertencer a outro cliente em buscarPorId")
    void deveLancarExcecaoQuandoPedestreDeOutroClienteEmBuscarPorId() {
        Cliente cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Cliente outroCliente = Cliente.builder().id(999L).nome("Outro").build();
        Pedestre pedestreOutro = Pedestre.builder()
                .id(100L)
                .nome("Pedestre Outro Cliente")
                .cliente(outroCliente)
                .removido(false)
                .build();

        when(pedestreGateway.findById(100L)).thenReturn(Optional.of(pedestreOutro));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.buscarPorId(100L));

        assertEquals("O pedestre não pertence ao seu cliente.", ex.getMessage());
        verify(pedestreGateway, times(1)).findById(100L);
    }
}
