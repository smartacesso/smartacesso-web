package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.usecase.empresa.ConsultaEmpresaUseCase;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmpresaControllerTest {

    @Mock
    private ConsultaEmpresaUseCase consultaEmpresaUseCase;

    @InjectMocks
    private EmpresaController empresaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(empresaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/empresas deve retornar 200 OK com empresas paginadas contendo departamentos, centros e cargos")
    void deveBuscarEmpresasComSucesso() throws Exception {
        Empresa empresa = Empresa.builder()
                .id(1L)
                .nome("Empresa ABC")
                .cnpj("12.345.678/0001-90")
                .email("contato@abc.com")
                .telefone("11999998888")
                .status(Status.ATIVO)
                .departamentos(List.of(
                        Departamento.builder().id(10L).nome("Departamento TI").status(Status.ATIVO).build()
                ))
                .centros(List.of(
                        CentroCusto.builder().id(20L).nome("Centro Financeiro").status(Status.ATIVO).build()
                ))
                .cargos(List.of(
                        Cargo.builder().id(30L).nome("Analista de Sistemas").status(Status.ATIVO).build()
                ))
                .build();

        Page<Empresa> page = new PageImpl<>(List.of(empresa), PageRequest.of(0, 10), 1);
        when(consultaEmpresaUseCase.execute(eq("ABC"), eq("123"), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/empresas")
                        .param("nome", "ABC")
                        .param("cnpj", "123")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].nome").value("Empresa ABC"))
                .andExpect(jsonPath("$.content[0].cnpj").value("12.345.678/0001-90"))
                .andExpect(jsonPath("$.content[0].status").value("ATIVO"))
                .andExpect(jsonPath("$.content[0].departamentos[0].id").value(10))
                .andExpect(jsonPath("$.content[0].departamentos[0].nome").value("Departamento TI"))
                .andExpect(jsonPath("$.content[0].departamentos[0].status").value("ATIVO"))
                .andExpect(jsonPath("$.content[0].centros[0].id").value(20))
                .andExpect(jsonPath("$.content[0].centros[0].nome").value("Centro Financeiro"))
                .andExpect(jsonPath("$.content[0].cargos[0].id").value(30))
                .andExpect(jsonPath("$.content[0].cargos[0].nome").value("Analista de Sistemas"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));

        verify(consultaEmpresaUseCase, times(1)).execute(eq("ABC"), eq("123"), any());
    }
}
