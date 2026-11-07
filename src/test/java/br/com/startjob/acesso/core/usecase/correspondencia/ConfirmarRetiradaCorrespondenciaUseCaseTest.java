package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConfirmarRetiradaCorrespondenciaUseCaseTest {

    @Mock
    private CorrespondenciaGateway correspondenciaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ConfirmarRetiradaCorrespondenciaUseCase confirmarRetiradaUseCase;

    private Cliente cliente;
    private Usuario usuarioLogado;
    private Correspondencia correspondenciaExistente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(10L).nome("Condomínio Central").build();
        usuarioLogado = Usuario.builder().id(1L).nome("Porteiro João").cliente(cliente).build();
        correspondenciaExistente = Correspondencia.builder()
                .id(100L)
                .destinatario(Pedestre.builder().id(50L).nome("Morador Maria").build())
                .cliente(cliente)
                .tipo("PACOTE")
                .confirmaRetirada("N")
                .removido(false)
                .build();
    }

    @Test
    @DisplayName("Deve confirmar retirada com sucesso preenchendo nome, documento e dataRetirada")
    void deveConfirmarRetiradaComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondenciaExistente));
        when(correspondenciaGateway.save(any(Correspondencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Correspondencia resultado = confirmarRetiradaUseCase.execute(100L, "  João da Silva  ", "  12345678900  ");

        assertNotNull(resultado);
        assertEquals("S", resultado.getConfirmaRetirada());
        assertNotNull(resultado.getDataRetirada());
        assertEquals("João da Silva", resultado.getNomeQuemRetirou());
        assertEquals("12345678900", resultado.getDocumentoQuemRetirou());
        verify(correspondenciaGateway, times(1)).save(correspondenciaExistente);
    }

    @Test
    @DisplayName("Deve falhar quando nome de quem está retirando for vazio")
    void deveFalharQuandoNomeVazio() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> confirmarRetiradaUseCase.execute(100L, "   ", "123456"));

        assertEquals("Informe o nome de quem está retirando.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, webPermissaoUseCase, correspondenciaGateway);
    }

    @Test
    @DisplayName("Deve falhar quando documento de quem está retirando for vazio")
    void deveFalharQuandoDocumentoVazio() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> confirmarRetiradaUseCase.execute(100L, "João", "   "));

        assertEquals("Informe o documento de quem está retirando.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, webPermissaoUseCase, correspondenciaGateway);
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possui permissão WEB_CORRESPONDENCIA_EDITAR")
    void deveFalharQuandoSemPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> confirmarRetiradaUseCase.execute(100L, "João", "123456"));

        assertEquals("Você não possui permissão para confirmar retirada de correspondências.", ex.getMessage());
        verify(correspondenciaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar quando correspondência pertence a outro cliente")
    void deveFalharQuandoCorrespondenciaDeOutroCliente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        correspondenciaExistente.setCliente(Cliente.builder().id(99L).build());
        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondenciaExistente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> confirmarRetiradaUseCase.execute(100L, "João", "123456"));

        assertEquals("A correspondência não pertence ao seu cliente.", ex.getMessage());
        verify(correspondenciaGateway, never()).save(any());
    }

    @Test
    @DisplayName("Deve falhar quando correspondência já teve retirada confirmada")
    void deveFalharQuandoJaRetirada() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        correspondenciaExistente.setConfirmaRetirada("S");
        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondenciaExistente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> confirmarRetiradaUseCase.execute(100L, "João", "123456"));

        assertEquals("A retirada desta correspondência já foi confirmada.", ex.getMessage());
        verify(correspondenciaGateway, never()).save(any());
    }
}
