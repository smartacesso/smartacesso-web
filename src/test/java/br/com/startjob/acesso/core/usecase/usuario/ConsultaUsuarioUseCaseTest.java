package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ConsultaUsuarioUseCase consultaUsuarioUseCase;

    @Test
    @DisplayName("Deve consultar usuários paginados com sucesso para o cliente do usuário logado")
    void deveConsultarUsuariosComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Usuario usuario = Usuario.builder()
                .id(100L)
                .nome("Maria Silva")
                .cpf("12345678900")
                .cliente(cliente)
                .status(Status.ATIVO)
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> page = new PageImpl<>(List.of(usuario), pageable, 1);

        when(usuarioGateway.buscarUsuarios(idCliente, "Maria", "12345678900", pageable)).thenReturn(page);

        Page<Usuario> resultado = consultaUsuarioUseCase.execute("Maria", "12345678900", pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Maria Silva", resultado.getContent().get(0).getNome());

        verify(usuarioGateway, times(1)).findLoggedUser();
        verify(usuarioGateway, times(1)).buscarUsuarios(idCliente, "Maria", "12345678900", pageable);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não for encontrado em execute")
    void deveLancarExcecaoQuandoUsuarioNaoLogadoEmExecute() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.execute(null, null, pageable));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verify(usuarioGateway, never()).buscarUsuarios(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não possuir cliente em execute")
    void deveLancarExcecaoQuandoUsuarioSemClienteEmExecute() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.execute(null, null, pageable));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verify(usuarioGateway, never()).buscarUsuarios(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso para o cliente do usuário logado")
    void deveBuscarUsuarioPorIdComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Usuario usuario = Usuario.builder()
                .id(100L)
                .nome("Maria Silva")
                .login("maria.silva")
                .cliente(cliente)
                .removido(false)
                .status(Status.ATIVO)
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .build();

        when(usuarioGateway.findById(100L)).thenReturn(Optional.of(usuario));

        Usuario resultado = consultaUsuarioUseCase.buscarPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Maria Silva", resultado.getNome());
        assertEquals("maria.silva", resultado.getLogin());
        assertEquals(idCliente, resultado.getCliente().getId());

        verify(usuarioGateway, times(1)).findLoggedUser();
        verify(usuarioGateway, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando id for nulo em buscarPorId")
    void deveLancarExcecaoQuandoIdNuloEmBuscarPorId() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.buscarPorId(null));

        assertEquals("Identificador do usuário não informado.", ex.getMessage());
        verifyNoInteractions(usuarioGateway);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário não estiver logado em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioNaoLogadoEmBuscarPorId() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.buscarPorId(100L));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verify(usuarioGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não possuir cliente em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioSemClienteEmBuscarPorId() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.buscarPorId(100L));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verify(usuarioGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário não for encontrado em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoEmBuscarPorId() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(usuarioGateway.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.buscarPorId(999L));

        assertEquals("Usuário não encontrado.", ex.getMessage());
        verify(usuarioGateway, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário estiver com removido=true em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioRemovidoEmBuscarPorId() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Usuario usuarioRemovido = Usuario.builder()
                .id(100L)
                .nome("Usuario Removido")
                .cliente(cliente)
                .removido(true)
                .build();

        when(usuarioGateway.findById(100L)).thenReturn(Optional.of(usuarioRemovido));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.buscarPorId(100L));

        assertEquals("Usuário não encontrado.", ex.getMessage());
        verify(usuarioGateway, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário pertencer a outro cliente em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioDeOutroClienteEmBuscarPorId() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Cliente outroCliente = Cliente.builder().id(999L).nome("Outro").build();
        Usuario usuarioOutro = Usuario.builder()
                .id(100L)
                .nome("Usuario Outro Cliente")
                .cliente(outroCliente)
                .removido(false)
                .build();

        when(usuarioGateway.findById(100L)).thenReturn(Optional.of(usuarioOutro));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaUsuarioUseCase.buscarPorId(100L));

        assertEquals("O usuário não pertence ao seu cliente.", ex.getMessage());
        verify(usuarioGateway, times(1)).findById(100L);
    }
}
