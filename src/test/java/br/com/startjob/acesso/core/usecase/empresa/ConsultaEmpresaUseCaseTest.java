package br.com.startjob.acesso.core.usecase.empresa;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
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
class ConsultaEmpresaUseCaseTest {

    @Mock
    private EmpresaGateway empresaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @InjectMocks
    private ConsultaEmpresaUseCase consultaEmpresaUseCase;

    @Test
    @DisplayName("Deve consultar empresas paginadas com sucesso para o cliente do usuário logado")
    void deveConsultarEmpresasComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Empresa empresa = Empresa.builder()
                .id(100L)
                .nome("Empresa Matriz")
                .cnpj("12.345.678/0001-90")
                .status(Status.ATIVO)
                .departamentos(List.of(Departamento.builder().id(1L).nome("TI").status(Status.ATIVO).build()))
                .centros(List.of(CentroCusto.builder().id(2L).nome("Operações").status(Status.ATIVO).build()))
                .cargos(List.of(Cargo.builder().id(3L).nome("Engenheiro").status(Status.ATIVO).build()))
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Empresa> page = new PageImpl<>(List.of(empresa), pageable, 1);

        when(empresaGateway.buscarEmpresas(idCliente, "Matriz", "12345", pageable)).thenReturn(page);

        Page<Empresa> resultado = consultaEmpresaUseCase.execute("Matriz", "12345", pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        Empresa empresaResult = resultado.getContent().get(0);
        assertEquals("Empresa Matriz", empresaResult.getNome());
        assertEquals(1, empresaResult.getDepartamentos().size());
        assertEquals("TI", empresaResult.getDepartamentos().get(0).getNome());
        assertEquals(1, empresaResult.getCentros().size());
        assertEquals("Operações", empresaResult.getCentros().get(0).getNome());
        assertEquals(1, empresaResult.getCargos().size());
        assertEquals("Engenheiro", empresaResult.getCargos().get(0).getNome());

        verify(usuarioGateway, times(1)).findLoggedUser();
        verify(empresaGateway, times(1)).buscarEmpresas(idCliente, "Matriz", "12345", pageable);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.execute(null, null, pageable));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verify(empresaGateway, never()).buscarEmpresas(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não possuir cliente")
    void deveLancarExcecaoQuandoUsuarioSemCliente() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.execute(null, null, pageable));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verify(empresaGateway, never()).buscarEmpresas(any(), any(), any(), any());
    }

    @Test
    @DisplayName("Deve buscar empresa por ID com sucesso para o cliente do usuário logado")
    void deveBuscarEmpresaPorIdComSucesso() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Empresa empresa = Empresa.builder()
                .id(100L)
                .nome("Empresa ABC")
                .cnpj("12.345.678/0001-90")
                .cliente(cliente)
                .removido(false)
                .departamentos(List.of(Departamento.builder().id(1L).nome("TI").status(Status.ATIVO).build()))
                .centros(List.of(CentroCusto.builder().id(2L).nome("Financeiro").status(Status.ATIVO).build()))
                .cargos(List.of(Cargo.builder().id(3L).nome("Analista").status(Status.ATIVO).build()))
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        Empresa resultado = consultaEmpresaUseCase.buscarPorId(100L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Empresa ABC", resultado.getNome());
        assertEquals(1, resultado.getDepartamentos().size());
        assertEquals("TI", resultado.getDepartamentos().get(0).getNome());
        verify(usuarioGateway, times(1)).findLoggedUser();
        verify(empresaGateway, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando id for nulo em buscarPorId")
    void deveLancarExcecaoQuandoIdNuloEmBuscarPorId() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.buscarPorId(null));

        assertEquals("Identificador da empresa não informado.", ex.getMessage());
        verifyNoInteractions(usuarioGateway, empresaGateway);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário não estiver logado em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioNaoLogadoEmBuscarPorId() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.buscarPorId(100L));

        assertEquals("Usuário logado não encontrado", ex.getMessage());
        verify(empresaGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando usuário logado não possuir cliente em buscarPorId")
    void deveLancarExcecaoQuandoUsuarioSemClienteEmBuscarPorId() {
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(null).build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.buscarPorId(100L));

        assertEquals("Usuário logado não possui cliente associado", ex.getMessage());
        verify(empresaGateway, never()).findById(any());
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando empresa não for encontrada em buscarPorId")
    void deveLancarExcecaoQuandoEmpresaNaoEncontradaEmBuscarPorId() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(empresaGateway.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.buscarPorId(999L));

        assertEquals("Empresa não encontrada.", ex.getMessage());
        verify(empresaGateway, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando empresa estiver com removido=true em buscarPorId")
    void deveLancarExcecaoQuandoEmpresaRemovidaEmBuscarPorId() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Empresa empresaRemovida = Empresa.builder()
                .id(100L)
                .nome("Empresa Removida")
                .cliente(cliente)
                .removido(true)
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresaRemovida));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.buscarPorId(100L));

        assertEquals("Empresa não encontrada.", ex.getMessage());
        verify(empresaGateway, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Deve lançar BusinessException quando empresa pertencer a outro cliente em buscarPorId")
    void deveLancarExcecaoQuandoEmpresaDeOutroClienteEmBuscarPorId() {
        Long idCliente = 10L;
        Cliente cliente = Cliente.builder().id(idCliente).nome("Cliente Teste").build();
        Usuario usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();

        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));

        Cliente outroCliente = Cliente.builder().id(999L).nome("Outro").build();
        Empresa empresaOutro = Empresa.builder()
                .id(100L)
                .nome("Empresa Outro Cliente")
                .cliente(outroCliente)
                .removido(false)
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresaOutro));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> consultaEmpresaUseCase.buscarPorId(100L));

        assertEquals("A empresa não pertence ao seu cliente.", ex.getMessage());
        verify(empresaGateway, times(1)).findById(100L);
    }
}
