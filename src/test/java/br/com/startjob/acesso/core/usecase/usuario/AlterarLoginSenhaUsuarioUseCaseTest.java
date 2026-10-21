package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PasswordGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
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
class AlterarLoginSenhaUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private PasswordGateway passwordGateway;

    @InjectMocks
    private AlterarLoginSenhaUsuarioUseCase useCase;

    private Cliente cliente;
    private Usuario adminLogado;
    private Usuario usuarioAlvo;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();

        adminLogado = Usuario.builder()
                .id(1L)
                .nome("Admin")
                .login("admin")
                .perfil(PerfilAcesso.ADMINISTRADOR)
                .cliente(cliente)
                .build();

        usuarioAlvo = Usuario.builder()
                .id(5L)
                .nome("Usuario Comum")
                .login("comum")
                .senha("hashAntigo")
                .perfil(PerfilAcesso.OPERADOR)
                .cliente(cliente)
                .removido(false)
                .build();
    }

    @Test
    @DisplayName("Deve alterar login e senha com sucesso quando administrador e login livre")
    void deveAlterarLoginESenhaComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(adminLogado));
        when(usuarioGateway.findById(5L)).thenReturn(Optional.of(usuarioAlvo));
        when(usuarioGateway.findByLoginAndCliente("novo.login", 10L)).thenReturn(Optional.empty());
        when(passwordGateway.hash("NovaSenha123")).thenReturn("novoHash123");
        when(usuarioGateway.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = useCase.execute(5L, "novo.login", "NovaSenha123", "NovaSenha123");

        assertNotNull(resultado);
        assertEquals("novo.login", resultado.getLogin());
        assertEquals("novoHash123", resultado.getSenha());

        verify(usuarioGateway, times(1)).save(usuarioAlvo);
        verify(passwordGateway, times(1)).hash("NovaSenha123");
    }

    @Test
    @DisplayName("Deve permitir manter o mesmo login e alterar apenas a senha")
    void devePermitirManterMesmoLoginEAlterarSenha() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(adminLogado));
        when(usuarioGateway.findById(5L)).thenReturn(Optional.of(usuarioAlvo));
        // findByLoginAndCliente retorna o próprio usuário alvo
        when(usuarioGateway.findByLoginAndCliente("comum", 10L)).thenReturn(Optional.of(usuarioAlvo));
        when(passwordGateway.hash("NovaSenha123")).thenReturn("novoHash123");
        when(usuarioGateway.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario resultado = useCase.execute(5L, "comum", "NovaSenha123", "NovaSenha123");

        assertNotNull(resultado);
        assertEquals("comum", resultado.getLogin());
        assertEquals("novoHash123", resultado.getSenha());

        verify(usuarioGateway, times(1)).save(usuarioAlvo);
    }

    @Test
    @DisplayName("Deve lançar exceção se as senhas não conferirem")
    void deveLancarExcecaoQuandoSenhasNaoConferem() {
        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(5L, "login", "senhaA", "senhaB"));
        assertEquals("As senhas não conferem.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não estiver logado")
    void deveLancarExcecaoQuandoNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(5L, "login", "senha", "senha"));
        assertEquals("Usuário não está logado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário logado não for ADMINISTRADOR")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoForAdmin() {
        Usuario operadorLogado = Usuario.builder()
                .id(2L)
                .perfil(PerfilAcesso.OPERADOR)
                .cliente(cliente)
                .build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(operadorLogado));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(5L, "login", "senha", "senha"));
        assertEquals("Apenas administradores podem alterar o login e senha de usuários.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário alvo não for encontrado")
    void deveLancarExcecaoQuandoUsuarioAlvoNaoEncontrado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(adminLogado));
        when(usuarioGateway.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(99L, "login", "senha", "senha"));
        assertEquals("Usuário não encontrado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário alvo estiver removido")
    void deveLancarExcecaoQuandoUsuarioAlvoRemovido() {
        usuarioAlvo.setRemovido(true);
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(adminLogado));
        when(usuarioGateway.findById(5L)).thenReturn(Optional.of(usuarioAlvo));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(5L, "login", "senha", "senha"));
        assertEquals("Usuário não encontrado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário alvo pertencer a outro cliente")
    void deveLancarExcecaoQuandoUsuarioAlvoPertencerAOutroCliente() {
        Cliente outroCliente = Cliente.builder().id(999L).nome("Outro").build();
        usuarioAlvo.setCliente(outroCliente);

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(adminLogado));
        when(usuarioGateway.findById(5L)).thenReturn(Optional.of(usuarioAlvo));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(5L, "login", "senha", "senha"));
        assertEquals("O usuário não pertence ao seu cliente.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve impedir atualização se o novo login já existe para outro usuário")
    void deveImpedirAtualizacaoQuandoLoginPertenceAOutroUsuario() {
        Usuario outroUsuario = Usuario.builder()
                .id(8L)
                .login("login.duplicado")
                .cliente(cliente)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(adminLogado));
        when(usuarioGateway.findById(5L)).thenReturn(Optional.of(usuarioAlvo));
        when(usuarioGateway.findByLoginAndCliente("login.duplicado", 10L)).thenReturn(Optional.of(outroUsuario));

        BusinessException ex = assertThrows(BusinessException.class, () ->
                useCase.execute(5L, "login.duplicado", "senha", "senha"));
        assertEquals("Login já cadastrado para outro usuário.", ex.getMessage());
        verify(usuarioGateway, never()).save(any());
    }
}
