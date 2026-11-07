package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.correspondencia.CorrespondenciaFiltro;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaCorrespondenciaUseCaseTest {

    @Mock
    private CorrespondenciaGateway correspondenciaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private ConsultaCorrespondenciaUseCase consultaCorrespondenciaUseCase;

    private Cliente cliente;
    private Usuario usuarioLogado;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(10L).nome("Condomínio Central").build();
        usuarioLogado = Usuario.builder().id(1L).nome("Porteiro João").cliente(cliente).build();
    }

    @Test
    @DisplayName("Deve buscar correspondências paginadas com filtros normalizados")
    void deveBuscarCorrespondenciasPaginadasComFiltros() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_VER.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .id(100L)
                .destinatario(Pedestre.builder().id(50L).nome("Maria Silva").build())
                .tipo("PACOTE")
                .codigoRastreio("BR123")
                .confirmaRetirada("N")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Correspondencia> pageEsperada = new PageImpl<>(List.of(correspondencia), pageable, 1);

        when(correspondenciaGateway.buscarCorrespondencias(eq(10L), any(CorrespondenciaFiltro.class), eq(pageable)))
                .thenReturn(pageEsperada);

        LocalDate dataPesquisa = LocalDate.of(2026, 11, 7);
        CorrespondenciaFiltro filtro = CorrespondenciaFiltro.builder()
                .nomeDestinatario("  Maria  ")
                .codigoRastreio("  BR123  ")
                .tipo("  pacote ")
                .confirmaRetirada(" false ")
                .dataRecebimento(dataPesquisa)
                .build();

        Page<Correspondencia> resultado = consultaCorrespondenciaUseCase.execute(filtro, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        ArgumentCaptor<CorrespondenciaFiltro> filtroCaptor = ArgumentCaptor.forClass(CorrespondenciaFiltro.class);
        verify(correspondenciaGateway).buscarCorrespondencias(eq(10L), filtroCaptor.capture(), eq(pageable));

        CorrespondenciaFiltro filtroPassado = filtroCaptor.getValue();
        assertEquals("Maria", filtroPassado.getNomeDestinatario());
        assertEquals("BR123", filtroPassado.getCodigoRastreio());
        assertEquals("PACOTE", filtroPassado.getTipo());
        assertEquals("N", filtroPassado.getConfirmaRetirada());
        assertEquals(dataPesquisa, filtroPassado.getDataRecebimento());
    }

    @Test
    @DisplayName("Deve falhar ao consultar correspondências quando usuário não possui permissão")
    void deveFalharQuandoSemPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaCorrespondenciaUseCase.execute(new CorrespondenciaFiltro(), PageRequest.of(0, 10)));

        assertEquals("Você não possui permissão para consultar correspondências.", ex.getMessage());
        verifyNoInteractions(correspondenciaGateway);
    }

    @Test
    @DisplayName("Deve buscar correspondência por ID com sucesso")
    void deveBuscarPorIdComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_VER.getCodigo()));

        Correspondencia correspondencia = Correspondencia.builder()
                .id(100L)
                .cliente(cliente)
                .tipo("CARTA")
                .removido(false)
                .build();

        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondencia));

        Correspondencia resultado = consultaCorrespondenciaUseCase.buscarPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
    }

    @Test
    @DisplayName("Deve falhar ao buscar correspondência por ID de outro cliente")
    void deveFalharAoBuscarPorIdDeOutroCliente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CORRESPONDENCIA_VER.getCodigo()));

        Correspondencia correspondenciaOutroCliente = Correspondencia.builder()
                .id(100L)
                .cliente(Cliente.builder().id(99L).build())
                .tipo("CARTA")
                .removido(false)
                .build();

        when(correspondenciaGateway.findById(100L)).thenReturn(Optional.of(correspondenciaOutroCliente));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaCorrespondenciaUseCase.buscarPorId(100L));

        assertEquals("A correspondência não pertence ao seu cliente.", ex.getMessage());
    }
}
