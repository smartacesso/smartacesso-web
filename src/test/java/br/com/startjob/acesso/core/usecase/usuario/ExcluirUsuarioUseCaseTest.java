package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcluirUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ExcluirUsuarioUseCase useCase;

    private Cliente clientePadrao;
    private Usuario usuarioLogado;
    private Usuario usuarioAlvo;

    @BeforeEach
    void setUp() {
        clientePadrao = Cliente.builder()
                .id(1L)
                .nome("Cliente Teste")
                .build();

        usuarioLogado = Usuario.builder()
                .id(10L)
                .nome("Admin")
                .cliente(clientePadrao)
                .build();

        usuarioAlvo = Usuario.builder()
                .id(20L)
                .nome("Usuario Comum")
                .cliente(clientePadrao)
                .removido(false)
                .build();
    }

    @Test
    @DisplayName("Deve excluir usuario logicamente com sucesso quando permissao for valida")
    void deveExcluirUsuarioComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.USUARIO_EDITAR.getCodigo()));
        when(usuarioGateway.findById(20L)).thenReturn(Optional.of(usuarioAlvo));

        useCase.execute(20L);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioGateway).save(captor.capture());

        Usuario usuarioSalvo = captor.getValue();
        assertThat(usuarioSalvo.getId()).isEqualTo(20L);
        assertThat(usuarioSalvo.getRemovido()).isTrue();
        assertThat(usuarioSalvo.getDataRemovido()).isNotNull();
    }

    @Test
    @DisplayName("Deve lancar excecao quando idUsuario for nulo")
    void deveLancarExcecaoQuandoIdUsuarioNulo() {
        assertThatThrownBy(() -> useCase.execute(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Identificador do usuário não informado.");

        verify(usuarioGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario logado nao for encontrado")
    void deveLancarExcecaoQuandoUsuarioLogadoNaoEncontrado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(20L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário não está logado.");

        verify(usuarioGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario logado nao tiver permissao USUARIO_EDITAR")
    void deveLancarExcecaoQuandoNaoPossuiPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of("OUTRA_PERMISSAO"));

        assertThatThrownBy(() -> useCase.execute(20L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Você não possui permissão para excluir usuários.");

        verify(usuarioGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario alvo nao for encontrado")
    void deveLancarExcecaoQuandoUsuarioAlvoNaoEncontrado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.USUARIO_EDITAR.getCodigo()));
        when(usuarioGateway.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário não encontrado.");

        verify(usuarioGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario alvo ja estiver removido")
    void deveLancarExcecaoQuandoUsuarioAlvoJaRemovido() {
        usuarioAlvo.setRemovido(true);

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.USUARIO_EDITAR.getCodigo()));
        when(usuarioGateway.findById(20L)).thenReturn(Optional.of(usuarioAlvo));

        assertThatThrownBy(() -> useCase.execute(20L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Usuário não encontrado.");

        verify(usuarioGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario pertencer a outro cliente")
    void deveLancarExcecaoQuandoOutroCliente() {
        Cliente outroCliente = Cliente.builder().id(99L).nome("Outro").build();
        usuarioAlvo.setCliente(outroCliente);

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.USUARIO_EDITAR.getCodigo()));
        when(usuarioGateway.findById(20L)).thenReturn(Optional.of(usuarioAlvo));

        assertThatThrownBy(() -> useCase.execute(20L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("O usuário não pertence ao seu cliente.");

        verify(usuarioGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lancar excecao quando usuario tentar excluir a si mesmo")
    void deveLancarExcecaoAoExcluirProprioUsuario() {
        Usuario mesmoUsuario = Usuario.builder()
                .id(10L)
                .nome("Admin")
                .cliente(clientePadrao)
                .removido(false)
                .build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.USUARIO_EDITAR.getCodigo()));
        when(usuarioGateway.findById(10L)).thenReturn(Optional.of(mesmoUsuario));

        assertThatThrownBy(() -> useCase.execute(10L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Não é permitido excluir o próprio usuário logado.");

        verify(usuarioGateway, never()).save(any());
    }
}
