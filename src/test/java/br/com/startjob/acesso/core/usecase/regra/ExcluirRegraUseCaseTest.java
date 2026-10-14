package br.com.startjob.acesso.core.usecase.regra;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.RegraGateway;
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
class ExcluirRegraUseCaseTest {

    @Mock
    private RegraGateway regraGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ExcluirRegraUseCase excluirRegraUseCase;

    @Test
    @DisplayName("Deve excluir regra com sucesso (exclusão lógica) quando usuário tem permissão CONFIG_REGRAS_EDITAR")
    void deveExcluirRegraComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente 10").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));

        Regra regra = Regra.builder()
                .id(100L)
                .nome("Regra Alfa")
                .removido(false)
                .cliente(cliente)
                .build();

        when(regraGateway.findById(100L)).thenReturn(Optional.of(regra));

        excluirRegraUseCase.execute(100L);

        ArgumentCaptor<Regra> captor = ArgumentCaptor.forClass(Regra.class);
        verify(regraGateway, times(1)).save(captor.capture());

        Regra regraSalva = captor.getValue();
        assertTrue(regraSalva.getRemovido());
        assertNotNull(regraSalva.getDataRemovido());
    }

    @Test
    @DisplayName("Deve lançar exceção quando identificador da regra for nulo")
    void deveLancarExcecaoQuandoIdNulo() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirRegraUseCase.execute(null));

        assertEquals("Identificador da regra não informado.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, webPermissaoUseCase, regraGateway);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não estiver logado")
    void deveLancarExcecaoQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirRegraUseCase.execute(100L));

        assertEquals("Usuário não está logado.", ex.getMessage());
        verify(regraGateway, never()).findById(any());
        verify(regraGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não possui permissão CONFIG_REGRAS_EDITAR")
    void deveLancarExcecaoQuandoUsuarioSemPermissao() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Operador").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of("OUTRA_PERMISSAO"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirRegraUseCase.execute(100L));

        assertEquals("Você não possui permissão para excluir regras de acesso.", ex.getMessage());
        verify(regraGateway, never()).findById(any());
        verify(regraGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando regra não for encontrada")
    void deveLancarExcecaoQuandoRegraNaoEncontrada() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente 10").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));
        when(regraGateway.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirRegraUseCase.execute(99L));

        assertEquals("Regra não encontrada.", ex.getMessage());
        verify(regraGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando regra já estiver logicamente removida")
    void deveLancarExcecaoQuandoRegraJaRemovida() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente 10").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));

        Regra regra = Regra.builder()
                .id(100L)
                .nome("Regra Removida")
                .removido(true)
                .cliente(cliente)
                .build();

        when(regraGateway.findById(100L)).thenReturn(Optional.of(regra));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirRegraUseCase.execute(100L));

        assertEquals("Regra não encontrada.", ex.getMessage());
        verify(regraGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando regra pertencer a outro cliente")
    void deveLancarExcecaoQuandoRegraPertencerAOutroCliente() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente 10").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));

        Cliente outroCliente = Cliente.builder().id(20L).nome("Outro Cliente").build();
        Regra regraOutro = Regra.builder()
                .id(100L)
                .nome("Regra Outro")
                .removido(false)
                .cliente(outroCliente)
                .build();

        when(regraGateway.findById(100L)).thenReturn(Optional.of(regraOutro));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> excluirRegraUseCase.execute(100L));

        assertEquals("A regra não pertence ao seu cliente.", ex.getMessage());
        verify(regraGateway, never()).save(any());
    }
}
