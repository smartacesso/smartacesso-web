package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreFiltro;
import br.com.startjob.acesso.core.domain.pedestre.PedestreResumo;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
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

import java.util.List;
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
    @DisplayName("Deve buscar pedestres paginados normalizando filtros e usando idCliente do usuário logado")
    void deveBuscarPedestresPaginadosComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        PedestreFiltro filtroEntrada = PedestreFiltro.builder()
                .matricula("  12345  ")
                .codigoCartaoAcesso("   ")
                .nome("  João  ")
                .contato(" 1199999 ")
                .cpf("529.982.247-25")
                .rg(" MG-123 ")
                .tipo(TipoPedestre.PEDESTRE)
                .idEmpresa(100L)
                .idDepartamento(200L)
                .idCentroCusto(300L)
                .idCargo(400L)
                .uuidLocal(" uuid-1 ")
                .status(Status.ATIVO)
                .possuiFoto(true)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        PedestreResumo resumo = PedestreResumo.builder()
                .id(1L)
                .nome("João da Silva")
                .matricula("12345")
                .cpf("52998224725")
                .tipo(TipoPedestre.PEDESTRE)
                .status(Status.ATIVO)
                .nomeEmpresa("Empresa ABC")
                .nomeRegraAtiva("Regra Padrão")
                .possuiFoto(true)
                .build();

        Page<PedestreResumo> paginaEsperada = new PageImpl<>(List.of(resumo), pageable, 1);
        when(pedestreGateway.buscarPedestres(eq(idCliente), any(PedestreFiltro.class), eq(pageable)))
                .thenReturn(paginaEsperada);

        Page<PedestreResumo> resultado = consultaPedestreUseCase.execute(filtroEntrada, pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("João da Silva", resultado.getContent().getFirst().getNome());

        ArgumentCaptor<PedestreFiltro> captor = ArgumentCaptor.forClass(PedestreFiltro.class);
        verify(pedestreGateway).buscarPedestres(eq(idCliente), captor.capture(), eq(pageable));

        PedestreFiltro filtroNormalizado = captor.getValue();
        assertEquals("12345", filtroNormalizado.getMatricula());
        assertNull(filtroNormalizado.getCodigoCartaoAcesso());
        assertEquals("João", filtroNormalizado.getNome());
        assertEquals("1199999", filtroNormalizado.getContato());
        assertEquals("52998224725", filtroNormalizado.getCpf());
        assertEquals("MG-123", filtroNormalizado.getRg());
        assertEquals(TipoPedestre.PEDESTRE, filtroNormalizado.getTipo());
        assertEquals(100L, filtroNormalizado.getIdEmpresa());
        assertEquals(200L, filtroNormalizado.getIdDepartamento());
        assertEquals(300L, filtroNormalizado.getIdCentroCusto());
        assertEquals(400L, filtroNormalizado.getIdCargo());
        assertEquals("uuid-1", filtroNormalizado.getUuidLocal());
        assertEquals(Status.ATIVO, filtroNormalizado.getStatus());
        assertEquals(Boolean.TRUE, filtroNormalizado.getPossuiFoto());
    }

    @Test
    @DisplayName("Deve lançar BusinessException em execute quando usuário logado não for encontrado")
    void deveLancarExcecaoEmExecuteQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.execute(new PedestreFiltro(), PageRequest.of(0, 10)));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verifyNoInteractions(pedestreGateway);
    }

    @Test
    @DisplayName("Deve lançar BusinessException em execute quando usuário logado não possuir cliente")
    void deveLancarExcecaoEmExecuteQuandoUsuarioSemCliente() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaPedestreUseCase.execute(new PedestreFiltro(), PageRequest.of(0, 10)));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verifyNoInteractions(pedestreGateway);
    }

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
