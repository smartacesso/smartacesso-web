package br.com.startjob.acesso.core.usecase.empresa;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarEmpresaUseCaseTest {

    @Mock
    private EmpresaGateway empresaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private SalvarEmpresaUseCase salvarEmpresaUseCase;

    private Usuario usuarioLogado;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(1L).nome("Cliente Teste").build();
        usuarioLogado = Usuario.builder().id(10L).nome("Admin").cliente(cliente).build();
    }

    @Test
    @DisplayName("Deve criar nova empresa com sucesso quando dados válidos e usuário autorizado")
    void deveCriarEmpresaComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder()
                .nome("Nova Empresa")
                .cnpj("12.345.678/0001-90")
                .telefone("11988887777")
                .status(Status.ATIVO)
                .departamentos(List.of(
                        Departamento.builder().nome("TI").status(Status.ATIVO).build()
                ))
                .centros(List.of(
                        CentroCusto.builder().nome("Financeiro").status(Status.ATIVO).build()
                ))
                .cargos(List.of(
                        Cargo.builder().nome("Desenvolvedor").status(Status.ATIVO).build()
                ))
                .build();

        when(empresaGateway.save(any(Empresa.class))).thenAnswer(invocation -> {
            Empresa param = invocation.getArgument(0);
            param.setId(100L);
            return param;
        });

        Empresa resultado = salvarEmpresaUseCase.execute(empresa);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals("Nova Empresa", resultado.getNome());
        assertEquals(cliente, resultado.getCliente());
        assertEquals(Boolean.FALSE, resultado.getRemovido());
        verify(empresaGateway, times(1)).save(empresa);
    }

    @Test
    @DisplayName("Deve falhar ao salvar empresa quando usuário não está logado")
    void deveFalharQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        Empresa empresa = Empresa.builder().nome("Empresa").telefone("1199999999").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("Usuário não está logado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao salvar empresa quando usuário não possui permissão")
    void deveFalharQuandoSemPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of());

        Empresa empresa = Empresa.builder().nome("Empresa").telefone("1199999999").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("Você não possui permissão para salvar empresas.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao salvar empresa quando usuário não possui cliente associado")
    void deveFalharQuandoSemCliente() {
        Usuario usuarioSemCliente = Usuario.builder().id(11L).nome("Sem Cliente").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioSemCliente));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioSemCliente))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder().nome("Empresa").telefone("1199999999").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("Usuário logado não possui cliente associado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao salvar empresa quando nem telefone nem celular informados")
    void deveFalharSemTelefoneECelular() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder()
                .nome("Empresa Sem Tel")
                .status(Status.ATIVO)
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("msg.add.telefone", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao criar nova empresa se algum novo filho possuir identificador id")
    void deveFalharAoCriarComFilhoPossuindoId() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresa = Empresa.builder()
                .nome("Empresa")
                .telefone("1199999999")
                .departamentos(List.of(
                        Departamento.builder().id(5L).nome("TI").status(Status.ATIVO).build()
                ))
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("Não é permitido informar identificador para novos departamentos.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar empresa existente com sucesso mantendo cliente e sincronizando filhos")
    void deveAtualizarEmpresaComSucesso() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresaExistente = Empresa.builder()
                .id(100L)
                .nome("Empresa Antiga")
                .cnpj("12.345.678/0001-90")
                .telefone("1188887777")
                .status(Status.ATIVO)
                .cliente(cliente)
                .dataCriacao(LocalDateTime.now().minusDays(10))
                .departamentos(List.of(
                        Departamento.builder().id(10L).nome("TI Antigo").status(Status.ATIVO).build()
                ))
                .centros(List.of(
                        CentroCusto.builder().id(20L).nome("Financeiro Antigo").status(Status.ATIVO).build()
                ))
                .cargos(List.of(
                        Cargo.builder().id(30L).nome("Analista Antigo").status(Status.ATIVO).build()
                ))
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresaExistente));
        when(empresaGateway.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Empresa empresaEditada = Empresa.builder()
                .id(100L)
                .nome("Empresa Atualizada")
                .cnpj("12.345.678/0001-90")
                .celular("11999998888")
                .status(Status.ATIVO)
                .departamentos(List.of(
                        Departamento.builder().id(10L).nome("TI Novo").status(Status.ATIVO).build(),
                        Departamento.builder().nome("RH").status(Status.ATIVO).build()
                ))
                .centros(List.of(
                        CentroCusto.builder().id(20L).nome("Financeiro Novo").status(Status.ATIVO).build()
                ))
                .cargos(List.of(
                        Cargo.builder().id(30L).nome("Analista Novo").status(Status.ATIVO).build()
                ))
                .build();

        Empresa resultado = salvarEmpresaUseCase.execute(empresaEditada);

        assertNotNull(resultado);
        assertEquals("Empresa Atualizada", resultado.getNome());
        assertEquals(cliente, resultado.getCliente());
        assertEquals(empresaExistente.getDataCriacao(), resultado.getDataCriacao());
        verify(empresaGateway, times(1)).save(empresaEditada);
    }

    @Test
    @DisplayName("Deve falhar ao atualizar empresa quando empresa não encontrada")
    void deveFalharAoAtualizarEmpresaInexistente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        when(empresaGateway.findById(999L)).thenReturn(Optional.empty());

        Empresa empresa = Empresa.builder().id(999L).nome("Empresa").telefone("1199999999").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("Empresa não encontrada.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar empresa quando pertence a outro cliente")
    void deveFalharAoAtualizarEmpresaDeOutroCliente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Cliente outroCliente = Cliente.builder().id(2L).nome("Outro").build();
        Empresa empresaExistente = Empresa.builder()
                .id(100L)
                .nome("Empresa Outro")
                .telefone("1199999999")
                .cliente(outroCliente)
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresaExistente));

        Empresa empresa = Empresa.builder().id(100L).nome("Empresa").telefone("1199999999").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresa));
        assertEquals("A empresa não pertence ao seu cliente.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar empresa quando departamento com id informado não pertence à empresa")
    void deveFalharAoAtualizarDepartamentoNaoPertencente() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.EMPRESA_EDITAR.getCodigo()));

        Empresa empresaExistente = Empresa.builder()
                .id(100L)
                .nome("Empresa")
                .telefone("1199999999")
                .cliente(cliente)
                .departamentos(List.of(
                        Departamento.builder().id(10L).nome("TI").status(Status.ATIVO).build()
                ))
                .build();

        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresaExistente));

        Empresa empresaEditada = Empresa.builder()
                .id(100L)
                .nome("Empresa")
                .telefone("1199999999")
                .departamentos(List.of(
                        Departamento.builder().id(999L).nome("Invasor").status(Status.ATIVO).build()
                ))
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarEmpresaUseCase.execute(empresaEditada));
        assertEquals("Departamento informado não pertence a esta empresa.", ex.getMessage());
    }
}
