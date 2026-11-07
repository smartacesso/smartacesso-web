package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarCorrespondenciaUseCaseTest {

    @Mock
    private CorrespondenciaGateway correspondenciaGateway;

    @Mock
    private PedestreGateway pedestreGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private SalvarCorrespondenciaUseCase salvarCorrespondenciaUseCase;

    private Cliente cliente;
    private Usuario usuarioLogado;
    private Pedestre destinatario;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).nome("Condomínio Central").build();
        usuarioLogado = Usuario.builder().id(10L).nome("Porteiro João").cliente(cliente).build();
        destinatario = Pedestre.builder()
                .id(50L)
                .nome("Morador Maria")
                .cpf("12345678900")
                .cliente(cliente)
                .removido(false)
                .build();
    }

    @Test
    @DisplayName("Deve criar nova correspondência com sucesso preenchendo defaults de recebimento e usuário")
    void deveCriarNovaCorrespondenciaComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(pedestreGateway.findById(50L)).thenReturn(Optional.of(destinatario));

        byte[] foto = new byte[]{1, 2, 3};
        Correspondencia nova = Correspondencia.builder()
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("pacote")
                .codigoRastreio("  BR123456789  ")
                .observacao("  Caixa frágil  ")
                .fotoVolume(foto)
                .build();

        when(correspondenciaGateway.save(any(Correspondencia.class))).thenAnswer(inv -> {
            Correspondencia arg = inv.getArgument(0);
            arg.setId(100L);
            return arg;
        });

        Correspondencia resultado = salvarCorrespondenciaUseCase.execute(nova);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("PACOTE", resultado.getTipo());
        assertEquals("BR123456789", resultado.getCodigoRastreio());
        assertEquals("Caixa frágil", resultado.getObservacao());
        assertEquals("N", resultado.getConfirmaRetirada());
        assertNotNull(resultado.getDataRecebimento());
        assertNull(resultado.getDataRetirada());
        assertEquals(cliente, resultado.getCliente());
        assertEquals(usuarioLogado, resultado.getUsuarioRecebeu());
        assertEquals(destinatario, resultado.getDestinatario());
        assertArrayEquals(foto, resultado.getFotoVolume());
        assertEquals("jpg", resultado.getExtensaoFoto());
        assertEquals(Boolean.FALSE, resultado.getRemovido());
        verify(correspondenciaGateway, times(1)).save(nova);
    }

    @Test
    @DisplayName("Deve preencher dataRetirada automaticamente quando confirmaRetirada for S")
    void devePreencherDataRetiradaQuandoConfirmado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(pedestreGateway.findById(50L)).thenReturn(Optional.of(destinatario));
        when(correspondenciaGateway.save(any(Correspondencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Correspondencia correspondencia = Correspondencia.builder()
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("CARTA")
                .confirmaRetirada("S")
                .build();

        Correspondencia resultado = salvarCorrespondenciaUseCase.execute(correspondencia);

        assertEquals("S", resultado.getConfirmaRetirada());
        assertNotNull(resultado.getDataRetirada());
    }

    @Test
    @DisplayName("Deve editar correspondência existente preservando foto, dataRecebimento e usuarioRecebeu")
    void deveEditarCorrespondenciaExistenteComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(pedestreGateway.findById(50L)).thenReturn(Optional.of(destinatario));

        LocalDateTime dataRecOriginal = LocalDateTime.now().minusHours(5);
        byte[] fotoAntiga = new byte[]{9, 8, 7};
        Usuario porteiroOriginal = Usuario.builder().id(99L).nome("Porteiro Turno Manhã").cliente(cliente).build();

        Correspondencia existente = Correspondencia.builder()
                .id(100L)
                .destinatario(destinatario)
                .usuarioRecebeu(porteiroOriginal)
                .cliente(cliente)
                .tipo("CARTA")
                .codigoRastreio("OLD123")
                .dataRecebimento(dataRecOriginal)
                .confirmaRetirada("N")
                .fotoVolume(fotoAntiga)
                .extensaoFoto("png")
                .removido(false)
                .build();

        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(existente));
        when(correspondenciaGateway.save(any(Correspondencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Correspondencia editada = Correspondencia.builder()
                .id(100L)
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("DOCUMENTO")
                .codigoRastreio("NEW456")
                .confirmaRetirada("S")
                .observacao("Retirado pelo próprio morador")
                .build();

        Correspondencia resultado = salvarCorrespondenciaUseCase.execute(editada, false);

        assertEquals(100L, resultado.getId());
        assertEquals("DOCUMENTO", resultado.getTipo());
        assertEquals("NEW456", resultado.getCodigoRastreio());
        assertEquals(dataRecOriginal, resultado.getDataRecebimento());
        assertEquals(porteiroOriginal, resultado.getUsuarioRecebeu());
        assertEquals("S", resultado.getConfirmaRetirada());
        assertNotNull(resultado.getDataRetirada());
        assertArrayEquals(fotoAntiga, resultado.getFotoVolume());
        assertEquals("png", resultado.getExtensaoFoto());
    }

    @Test
    @DisplayName("Deve remover foto ao editar correspondência quando removerFoto for true")
    void deveRemoverFotoAoEditarQuandoFlagAtiva() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(pedestreGateway.findById(50L)).thenReturn(Optional.of(destinatario));

        Correspondencia existente = Correspondencia.builder()
                .id(100L)
                .destinatario(destinatario)
                .cliente(cliente)
                .tipo("PACOTE")
                .fotoVolume(new byte[]{1, 2, 3})
                .extensaoFoto("jpg")
                .removido(false)
                .build();

        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(existente));
        when(correspondenciaGateway.save(any(Correspondencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Correspondencia editada = Correspondencia.builder()
                .id(100L)
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("PACOTE")
                .build();

        Correspondencia resultado = salvarCorrespondenciaUseCase.execute(editada, true);

        assertNull(resultado.getFotoVolume());
        assertNull(resultado.getExtensaoFoto());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possui permissão WEB_CORRESPONDENCIA_EDITAR")
    void deveFalharQuandoSemPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of());

        Correspondencia correspondencia = Correspondencia.builder()
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("CARTA")
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> salvarCorrespondenciaUseCase.execute(correspondencia)
        );
        assertEquals("Você não possui permissão para salvar correspondências.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando destinatário não informado")
    void deveFalharQuandoDestinatarioNaoInformado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .tipo("CARTA")
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> salvarCorrespondenciaUseCase.execute(correspondencia)
        );
        assertEquals("Destinatário é obrigatório.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando tipo de volume for inválido")
    void deveFalharQuandoTipoInvalido() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("CAIXA_INVALIDA")
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> salvarCorrespondenciaUseCase.execute(correspondencia)
        );
        assertEquals("Tipo de volume inválido.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando destinatário pertence a outro cliente")
    void deveFalharQuandoDestinatarioDeOutroCliente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));

        Cliente outroCliente = Cliente.builder().id(99L).nome("Outro").build();
        Pedestre destinatarioOutroCliente = Pedestre.builder()
                .id(50L)
                .nome("Outro Morador")
                .cliente(outroCliente)
                .build();

        when(pedestreGateway.findById(50L)).thenReturn(Optional.of(destinatarioOutroCliente));

        Correspondencia correspondencia = Correspondencia.builder()
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("CARTA")
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> salvarCorrespondenciaUseCase.execute(correspondencia)
        );
        assertEquals("O destinatário não pertence ao seu cliente.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao editar correspondência que pertence a outro cliente")
    void deveFalharAoEditarCorrespondenciaDeOutroCliente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo()));
        when(pedestreGateway.findById(50L)).thenReturn(Optional.of(destinatario));

        Cliente outroCliente = Cliente.builder().id(99L).nome("Outro").build();
        Correspondencia existenteOutro = Correspondencia.builder()
                .id(100L)
                .destinatario(destinatario)
                .cliente(outroCliente)
                .tipo("CARTA")
                .build();

        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(existenteOutro));

        Correspondencia editada = Correspondencia.builder()
                .id(100L)
                .destinatario(Pedestre.builder().id(50L).build())
                .tipo("CARTA")
                .build();

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> salvarCorrespondenciaUseCase.execute(editada)
        );
        assertEquals("A correspondência não pertence ao seu cliente.", ex.getMessage());
    }
}
