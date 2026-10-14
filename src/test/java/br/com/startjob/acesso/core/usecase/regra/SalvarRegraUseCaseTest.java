package br.com.startjob.acesso.core.usecase.regra;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoEscala;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalvarRegraUseCaseTest {

    @Mock
    private RegraGateway regraGateway;

    @Mock
    private EmpresaGateway empresaGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private WebPermissaoUseCase webPermissaoUseCase;

    @InjectMocks
    private SalvarRegraUseCase salvarRegraUseCase;

    private Usuario usuarioLogado;
    private Cliente cliente;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder().id(10L).nome("Cliente Teste").build();
        usuarioLogado = Usuario.builder().id(1L).nome("Admin").cliente(cliente).build();
        empresa = Empresa.builder().id(100L).nome("Empresa Matriz").cliente(cliente).removido(false).build();
    }

    private void mockUsuarioAutorizado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado))
                .thenReturn(Set.of(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));
    }

    @Test
    @DisplayName("Deve criar nova regra com horários com sucesso")
    void deveCriarNovaRegraHorarioComSucesso() {
        mockUsuarioAutorizado();
        when(empresaGateway.findById(100L)).thenReturn(Optional.of(empresa));

        Horario horario = Horario.builder()
                .nome("Turno Comercial")
                .status(Status.ATIVO)
                .diasSemana("12345")
                .horarioInicio(LocalDateTime.of(2026, 1, 1, 8, 0))
                .horarioFim(LocalDateTime.of(2026, 1, 1, 18, 0))
                .build();

        Regra regra = Regra.builder()
                .nome("Regra Comercial")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .empresa(Empresa.builder().id(100L).build())
                .horarios(new ArrayList<>(List.of(horario)))
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> {
            Regra arg = invocation.getArgument(0);
            arg.setId(500L);
            return arg;
        });

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(500L, resultado.getId());
        assertEquals("Regra Comercial", resultado.getNome());
        assertEquals(cliente, resultado.getCliente());
        assertEquals(Boolean.FALSE, resultado.getRemovido());
        assertEquals(1, resultado.getHorarios().size());
        verify(regraGateway).save(regra);
    }

    @Test
    @DisplayName("Deve criar nova regra por período com sucesso")
    void deveCriarNovaRegraPeriodoComSucesso() {
        mockUsuarioAutorizado();

        Regra regra = Regra.builder()
                .nome("Acesso Temporário")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_PERIODO)
                .tipoPedestre(TipoPedestre.VISITANTE)
                .dataInicioPeriodo(LocalDate.of(2026, 10, 1))
                .dataFimPeriodo(LocalDate.of(2026, 10, 31))
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(LocalDate.of(2026, 10, 1), resultado.getDataInicioPeriodo());
        assertEquals(LocalDate.of(2026, 10, 31), resultado.getDataFimPeriodo());
    }

    @Test
    @DisplayName("Deve criar regra por escala com sucesso")
    void deveCriarRegraEscalaComSucesso() {
        mockUsuarioAutorizado();

        Regra regra = Regra.builder()
                .nome("Turno 12x36")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_ESCALA)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .tipoEscala(TipoEscala.ESCALA_12_36)
                .horarioInicioTurno(LocalDateTime.of(2026, 1, 1, 7, 0))
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(TipoEscala.ESCALA_12_36, resultado.getTipoEscala());
        assertEquals(LocalDateTime.of(2026, 1, 1, 7, 0), resultado.getHorarioInicioTurno());
    }

    @Test
    @DisplayName("Deve criar regra por créditos com sucesso")
    void deveCriarRegraCreditoComSucesso() {
        mockUsuarioAutorizado();

        Regra regra = Regra.builder()
                .nome("Acesso 10 Créditos")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_CREDITO)
                .tipoPedestre(TipoPedestre.VISITANTE)
                .qtdeDeCreditos(10L)
                .diasValidadeCredito(30L)
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getQtdeDeCreditos());
        assertEquals(30L, resultado.getDiasValidadeCredito());
    }

    @Test
    @DisplayName("Deve criar regra de acesso único definindo automaticamente 1 crédito")
    void deveCriarRegraAcessoUnicoComSucesso() {
        mockUsuarioAutorizado();

        Regra regra = Regra.builder()
                .nome("Acesso Único Portaria")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_UNICO)
                .tipoPedestre(TipoPedestre.VISITANTE)
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getQtdeDeCreditos());
        assertNull(resultado.getDiasValidadeCredito());
    }

    @Test
    @DisplayName("Deve criar regra escala 3/3 com sucesso")
    void deveCriarRegraEscala33ComSucesso() {
        mockUsuarioAutorizado();

        Regra regra = Regra.builder()
                .nome("Escala Especial 3/3")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_ESCALA_3_3)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .dataInicioPeriodo(LocalDate.of(2026, 5, 1))
                .horarioInicioTurno(LocalDateTime.of(2026, 5, 1, 6, 0))
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(LocalDate.of(2026, 5, 1), resultado.getDataInicioPeriodo());
        assertEquals(LocalDateTime.of(2026, 5, 1, 6, 0), resultado.getHorarioInicioTurno());
    }

    @Test
    @DisplayName("Deve atualizar regra existente e validar horários existentes")
    void deveAtualizarRegraExistenteComSucesso() {
        mockUsuarioAutorizado();

        Horario horarioExistente = Horario.builder()
                .id(201L)
                .nome("Turno Manhã")
                .status(Status.ATIVO)
                .diasSemana("12345")
                .horarioInicio(LocalDateTime.of(2026, 1, 1, 8, 0))
                .horarioFim(LocalDateTime.of(2026, 1, 1, 12, 0))
                .build();

        Regra existente = Regra.builder()
                .id(50L)
                .nome("Regra Antiga")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .cliente(cliente)
                .horarios(List.of(horarioExistente))
                .build();

        when(regraGateway.findById(50L)).thenReturn(Optional.of(existente));

        Horario horarioAtualizado = Horario.builder()
                .id(201L)
                .nome("Turno Manhã Atualizado")
                .status(Status.ATIVO)
                .diasSemana("123456")
                .horarioInicio(LocalDateTime.of(2026, 1, 1, 8, 0))
                .horarioFim(LocalDateTime.of(2026, 1, 1, 13, 0))
                .build();

        Horario novoHorario = Horario.builder()
                .nome("Turno Tarde")
                .status(Status.ATIVO)
                .diasSemana("12345")
                .horarioInicio(LocalDateTime.of(2026, 1, 1, 14, 0))
                .horarioFim(LocalDateTime.of(2026, 1, 1, 18, 0))
                .build();

        Regra regraAtualizada = Regra.builder()
                .id(50L)
                .nome("Regra Atualizada")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.AMBOS)
                .horarios(List.of(horarioAtualizado, novoHorario))
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regraAtualizada);

        assertNotNull(resultado);
        assertEquals("Regra Atualizada", resultado.getNome());
        assertEquals(TipoPedestre.AMBOS, resultado.getTipoPedestre());
        assertEquals(2, resultado.getHorarios().size());
        verify(regraGateway).save(regraAtualizada);
    }

    @Test
    @DisplayName("Deve falhar ao salvar regra se regra for nula")
    void deveFalharQuandoRegraNula() {
        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(null));
        assertEquals("Dados da regra não informados.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não está logado")
    void deveFalharQuandoUsuarioNaoLogado() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.empty());

        Regra regra = Regra.builder().nome("Regra").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra));
        assertEquals("Usuário não está logado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possui permissão CONFIG_REGRAS_EDITAR")
    void deveFalharQuandoUsuarioSemPermissao() {
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(usuarioLogado));
        when(webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado)).thenReturn(Set.of());

        Regra regra = Regra.builder().nome("Regra").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra));
        assertEquals("Você não possui permissão para salvar regras de acesso.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando usuário não possui cliente associado")
    void deveFalharQuandoUsuarioSemCliente() {
        Usuario userSemCliente = Usuario.builder().id(99L).nome("Sem Cliente").build();
        when(usuarioGateway.findLoggedUser()).thenReturn(Optional.of(userSemCliente));
        when(webPermissaoUseCase.resolverPermissoesWeb(userSemCliente))
                .thenReturn(Set.of(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo()));

        Regra regra = Regra.builder().nome("Regra").build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra));
        assertEquals("Usuário logado não possui cliente associado.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando campos obrigatórios estiverem ausentes")
    void deveFalharCamposObrigatorios() {
        mockUsuarioAutorizado();

        Regra regraSemNome = Regra.builder().status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).build();
        assertEquals("Nome da regra é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regraSemNome)).getMessage());

        Regra regraSemStatus = Regra.builder().nome("Regra").tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).build();
        assertEquals("Status é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regraSemStatus)).getMessage());

        Regra regraSemTipo = Regra.builder().nome("Regra").status(Status.ATIVO).tipoPedestre(TipoPedestre.PEDESTRE).build();
        assertEquals("Tipo de regra é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regraSemTipo)).getMessage());

        Regra regraSemTipoPedestre = Regra.builder().nome("Regra").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).build();
        assertEquals("Tipo de pedestre é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regraSemTipoPedestre)).getMessage());
    }

    @Test
    @DisplayName("Deve falhar quando empresa informada não for encontrada ou pertencer a outro cliente")
    void deveFalharEmpresaInvalida() {
        mockUsuarioAutorizado();

        when(empresaGateway.findById(999L)).thenReturn(Optional.empty());

        Regra regra = Regra.builder()
                .nome("Regra")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_UNICO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .empresa(Empresa.builder().id(999L).build())
                .build();

        assertEquals("Empresa não encontrada.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra)).getMessage());

        Empresa empresaOutroCliente = Empresa.builder().id(200L).cliente(Cliente.builder().id(999L).build()).removido(false).build();
        when(empresaGateway.findById(200L)).thenReturn(Optional.of(empresaOutroCliente));

        Regra regraOutroCliente = Regra.builder()
                .nome("Regra")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_UNICO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .empresa(Empresa.builder().id(200L).build())
                .build();

        assertEquals("A empresa não pertence ao seu cliente.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regraOutroCliente)).getMessage());
    }

    @Test
    @DisplayName("Deve falhar ao criar nova regra se novo horário possuir identificador id")
    void deveFalharNovoHorarioComId() {
        mockUsuarioAutorizado();

        Horario horarioComId = Horario.builder().id(123L).nome("Turno").status(Status.ATIVO).diasSemana("123").horarioInicio(LocalDateTime.now()).horarioFim(LocalDateTime.now()).build();
        Regra regra = Regra.builder()
                .nome("Regra")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .horarios(List.of(horarioComId))
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra));
        assertEquals("Não é permitido informar identificador para novos horários.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar na edição quando regra informada não pertence ao cliente do usuário")
    void deveFalharEdicaoRegraDeOutroCliente() {
        mockUsuarioAutorizado();

        Regra regraOutroCliente = Regra.builder().id(50L).cliente(Cliente.builder().id(999L).build()).removido(false).build();
        when(regraGateway.findById(50L)).thenReturn(Optional.of(regraOutroCliente));

        Regra regra = Regra.builder()
                .id(50L)
                .nome("Regra")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_UNICO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra));
        assertEquals("A regra não pertence ao seu cliente.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve falhar na edição se horário informado não pertencer à regra")
    void deveFalharEdicaoHorarioNaoPertencente() {
        mockUsuarioAutorizado();

        Regra regraExistente = Regra.builder()
                .id(50L)
                .cliente(cliente)
                .removido(false)
                .horarios(List.of(Horario.builder().id(1L).build()))
                .build();
        when(regraGateway.findById(50L)).thenReturn(Optional.of(regraExistente));

        Horario horarioInvasor = Horario.builder().id(999L).nome("Invasor").status(Status.ATIVO).diasSemana("1").horarioInicio(LocalDateTime.now()).horarioFim(LocalDateTime.now()).build();
        Regra regra = Regra.builder()
                .id(50L)
                .nome("Regra")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .horarios(List.of(horarioInvasor))
                .build();

        BusinessException ex = assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(regra));
        assertEquals("Horário informado não pertence a esta regra.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve validar campos por tipo de regra")
    void deveValidarCamposPorTipo() {
        mockUsuarioAutorizado();

        // ACESSO_HORARIO sem horários
        Regra rHorario = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).build();
        assertEquals("Adicione pelo menos um horário.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rHorario)).getMessage());

        // ACESSO_PERIODO sem data inicial
        Regra rPeriodo1 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_PERIODO).tipoPedestre(TipoPedestre.PEDESTRE).dataFimPeriodo(LocalDate.now()).build();
        assertEquals("A data inicial do período é obrigatória.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rPeriodo1)).getMessage());

        // ACESSO_PERIODO sem data final
        Regra rPeriodo2 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_PERIODO).tipoPedestre(TipoPedestre.PEDESTRE).dataInicioPeriodo(LocalDate.now()).build();
        assertEquals("A data final do período é obrigatória.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rPeriodo2)).getMessage());

        // ACESSO_ESCALA sem tipo escala
        Regra rEscala1 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_ESCALA).tipoPedestre(TipoPedestre.PEDESTRE).horarioInicioTurno(LocalDateTime.now()).build();
        assertEquals("O tipo de escala é obrigatória.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rEscala1)).getMessage());

        // ACESSO_ESCALA sem horario inicio turno
        Regra rEscala2 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_ESCALA).tipoPedestre(TipoPedestre.PEDESTRE).tipoEscala(TipoEscala.ESCALA_12_36).build();
        assertEquals("O horário do início do turno é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rEscala2)).getMessage());

        // ACESSO_CREDITO sem qtde créditos
        Regra rCredito = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_CREDITO).tipoPedestre(TipoPedestre.PEDESTRE).build();
        assertEquals("A quantidade de creditos deve ser informada.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rCredito)).getMessage());

        // ACESSO_ESCALA_3_3 sem data inicial
        Regra rEscala33_1 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_ESCALA_3_3).tipoPedestre(TipoPedestre.PEDESTRE).horarioInicioTurno(LocalDateTime.now()).build();
        assertEquals("A data inicial do período é obrigatória.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rEscala33_1)).getMessage());

        // ACESSO_ESCALA_3_3 sem horario inicio turno
        Regra rEscala33_2 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_ESCALA_3_3).tipoPedestre(TipoPedestre.PEDESTRE).dataInicioPeriodo(LocalDate.now()).build();
        assertEquals("O horário do início do turno é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(rEscala33_2)).getMessage());
    }

    @Test
    @DisplayName("Deve validar campos obrigatórios dentro de um Horario")
    void deveValidarCamposDoHorario() {
        mockUsuarioAutorizado();

        Horario hSemNome = Horario.builder().status(Status.ATIVO).diasSemana("1").horarioInicio(LocalDateTime.now()).horarioFim(LocalDateTime.now()).build();
        Regra r1 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).horarios(List.of(hSemNome)).build();
        assertEquals("Nome do horário é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(r1)).getMessage());

        Horario hSemStatus = Horario.builder().nome("H").diasSemana("1").horarioInicio(LocalDateTime.now()).horarioFim(LocalDateTime.now()).build();
        Regra r2 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).horarios(List.of(hSemStatus)).build();
        assertEquals("Status do horário é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(r2)).getMessage());

        Horario hSemDias = Horario.builder().nome("H").status(Status.ATIVO).horarioInicio(LocalDateTime.now()).horarioFim(LocalDateTime.now()).build();
        Regra r3 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).horarios(List.of(hSemDias)).build();
        assertEquals("Adicione pelo menos um dia da semana.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(r3)).getMessage());

        Horario hSemIni = Horario.builder().nome("H").status(Status.ATIVO).diasSemana("1").horarioFim(LocalDateTime.now()).build();
        Regra r4 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).horarios(List.of(hSemIni)).build();
        assertEquals("O horário de início é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(r4)).getMessage());

        Horario hSemFim = Horario.builder().nome("H").status(Status.ATIVO).diasSemana("1").horarioInicio(LocalDateTime.now()).build();
        Regra r5 = Regra.builder().nome("R").status(Status.ATIVO).tipo(TipoRegra.ACESSO_HORARIO).tipoPedestre(TipoPedestre.PEDESTRE).horarios(List.of(hSemFim)).build();
        assertEquals("O horário final é obrigatório.",
                assertThrows(BusinessException.class, () -> salvarRegraUseCase.execute(r5)).getMessage());
    }

    @Test
    @DisplayName("Deve limpar campos incompatíveis com o tipo de regra selecionado")
    void deveLimparCamposIncompativeis() {
        mockUsuarioAutorizado();

        // Regra do tipo ACESSO_CREDITO deve limpar horarios, período e escala
        Horario horario = Horario.builder().nome("H").status(Status.ATIVO).diasSemana("1").horarioInicio(LocalDateTime.now()).horarioFim(LocalDateTime.now()).build();
        Regra regra = Regra.builder()
                .nome("Regra Limpeza")
                .status(Status.ATIVO)
                .tipo(TipoRegra.ACESSO_CREDITO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .qtdeDeCreditos(5L)
                .dataInicioPeriodo(LocalDate.now())
                .dataFimPeriodo(LocalDate.now().plusDays(10))
                .tipoEscala(TipoEscala.ESCALA_12_36)
                .horarioInicioTurno(LocalDateTime.now())
                .horarios(new ArrayList<>(List.of(horario)))
                .build();

        when(regraGateway.save(any(Regra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Regra resultado = salvarRegraUseCase.execute(regra);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getQtdeDeCreditos());
        assertNull(resultado.getDataInicioPeriodo());
        assertNull(resultado.getDataFimPeriodo());
        assertNull(resultado.getTipoEscala());
        assertNull(resultado.getHorarioInicioTurno());
        assertTrue(resultado.getHorarios().isEmpty());
    }
}
