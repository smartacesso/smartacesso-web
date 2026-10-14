package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.usecase.regra.ConsultaRegraUseCase;
import br.com.startjob.acesso.core.usecase.regra.ExcluirRegraUseCase;
import br.com.startjob.acesso.entrypoint.api.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegraControllerTest {

    @Mock
    private ConsultaRegraUseCase consultaRegraUseCase;

    @Mock
    private ExcluirRegraUseCase excluirRegraUseCase;

    @InjectMocks
    private RegraController regraController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(regraController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/regras deve retornar 200 OK com regras paginadas")
    void deveBuscarRegrasComSucesso() throws Exception {
        Empresa empresa = Empresa.builder().id(10L).nome("Empresa ABC").build();
        Regra regra = Regra.builder()
                .id(1L)
                .nome("Regra Funcionários")
                .status(Status.ATIVO)
                .validade(LocalDate.of(2026, 12, 31))
                .empresa(empresa)
                .tipo(TipoRegra.ACESSO_HORARIO)
                .tipoPedestre(TipoPedestre.PEDESTRE)
                .descricao("Regra para turno padrão")
                .horarios(List.of(
                        Horario.builder().id(100L).nome("Turno Manhã").status(Status.ATIVO).build()
                ))
                .build();

        Page<Regra> page = new PageImpl<>(List.of(regra), PageRequest.of(0, 10), 1);
        when(consultaRegraUseCase.execute(eq("Funcionários"), eq(10L), eq("ABC"), eq(TipoPedestre.PEDESTRE), eq(TipoRegra.ACESSO_HORARIO), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/regras")
                        .param("nome", "Funcionários")
                        .param("idEmpresa", "10")
                        .param("empresa", "ABC")
                        .param("tipoPedestre", "PEDESTRE")
                        .param("tipo", "ACESSO_HORARIO")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Regra Funcionários"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"))
                .andExpect(jsonPath("$.content[0].idEmpresa").value(10))
                .andExpect(jsonPath("$.content[0].nomeEmpresa").value("Empresa ABC"))
                .andExpect(jsonPath("$.content[0].tipo").value("ACESSO_HORARIO"))
                .andExpect(jsonPath("$.content[0].tipoDescricao").value("Acesso por horário"))
                .andExpect(jsonPath("$.content[0].tipoPedestre").value("PEDESTRE"))
                .andExpect(jsonPath("$.content[0].horarios[0].id").value(100))
                .andExpect(jsonPath("$.content[0].horarios[0].nome").value("Turno Manhã"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(consultaRegraUseCase).execute(eq("Funcionários"), eq(10L), eq("ABC"), eq(TipoPedestre.PEDESTRE), eq(TipoRegra.ACESSO_HORARIO), any());
    }

    @Test
    @DisplayName("GET /api/v1/regras deve suportar tipoRegra e nomeEmpresa como parâmetros alternativos")
    void deveSuportarParametrosAlternativos() throws Exception {
        Page<Regra> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(consultaRegraUseCase.execute(eq(null), eq(null), eq("Tech"), eq(TipoPedestre.AMBOS), eq(TipoRegra.ACESSO_ESCALA), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/regras")
                        .param("nomeEmpresa", "Tech")
                        .param("tipoPedestre", "AMBOS")
                        .param("tipoRegra", "ACESSO_ESCALA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(consultaRegraUseCase).execute(eq(null), eq(null), eq("Tech"), eq(TipoPedestre.AMBOS), eq(TipoRegra.ACESSO_ESCALA), any());
    }

    @Test
    @DisplayName("GET /api/v1/regras sem filtros deve retornar lista paginada")
    void deveBuscarRegrasSemFiltros() throws Exception {
        Page<Regra> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(consultaRegraUseCase.execute(isNull(), isNull(), isNull(), isNull(), isNull(), any()))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/regras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("DELETE /api/v1/regras/{id} deve retornar 204 No Content ao excluir regra com sucesso")
    void deveExcluirRegraComSucesso() throws Exception {
        doNothing().when(excluirRegraUseCase).execute(10L);

        mockMvc.perform(delete("/api/v1/regras/{id}", 10L))
                .andExpect(status().isNoContent());

        verify(excluirRegraUseCase).execute(10L);
    }

    @Test
    @DisplayName("DELETE /api/v1/regras/{id} deve retornar 400 Bad Request quando BusinessException for lançada")
    void deveRetornarBadRequestQuandoBusinessException() throws Exception {
        doThrow(new br.com.startjob.acesso.core.exception.BusinessException("Você não possui permissão para excluir regras de acesso."))
                .when(excluirRegraUseCase).execute(10L);

        mockMvc.perform(delete("/api/v1/regras/{id}", 10L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Você não possui permissão para excluir regras de acesso."));

        verify(excluirRegraUseCase).execute(10L);
    }
}
