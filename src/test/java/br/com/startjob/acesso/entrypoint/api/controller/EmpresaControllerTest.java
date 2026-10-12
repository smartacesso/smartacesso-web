package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.usecase.empresa.ConsultaEmpresaUseCase;
import br.com.startjob.acesso.core.usecase.empresa.ExcluirEmpresaUseCase;
import br.com.startjob.acesso.core.usecase.empresa.SalvarEmpresaUseCase;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmpresaControllerTest {

    @Mock
    private ConsultaEmpresaUseCase consultaEmpresaUseCase;

    @Mock
    private SalvarEmpresaUseCase salvarEmpresaUseCase;

    @Mock
    private ExcluirEmpresaUseCase excluirEmpresaUseCase;

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

    @Test
    @DisplayName("DELETE /api/v1/empresas/{id} deve retornar 204 No Content quando sucesso")
    void deveExcluirEmpresaComSucesso() throws Exception {
        doNothing().when(excluirEmpresaUseCase).execute(5L);

        mockMvc.perform(delete("/api/v1/empresas/{id}", 5L))
                .andExpect(status().isNoContent());

        verify(excluirEmpresaUseCase, times(1)).execute(5L);
    }

    @Test
    @DisplayName("DELETE /api/v1/empresas/{id} deve retornar 400 Bad Request quando sem permissao")
    void deveRetornar400QuandoSemPermissao() throws Exception {
        doThrow(new BusinessException("Você não possui permissão para excluir empresas."))
                .when(excluirEmpresaUseCase).execute(5L);

        mockMvc.perform(delete("/api/v1/empresas/{id}", 5L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Você não possui permissão para excluir empresas."));

        verify(excluirEmpresaUseCase, times(1)).execute(5L);
    }

    @Test
    @DisplayName("DELETE /api/v1/empresas/{id} deve retornar 400 Bad Request quando empresa nao encontrada")
    void deveRetornar400QuandoEmpresaNaoEncontrada() throws Exception {
        doThrow(new BusinessException("Empresa não encontrada."))
                .when(excluirEmpresaUseCase).execute(99L);

        mockMvc.perform(delete("/api/v1/empresas/{id}", 99L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Empresa não encontrada."));

        verify(excluirEmpresaUseCase, times(1)).execute(99L);
    }

    @Test
    @DisplayName("DELETE /api/v1/empresas/{id} deve retornar 400 Bad Request quando empresa nao pertence ao cliente")
    void deveRetornar400QuandoEmpresaDeOutroCliente() throws Exception {
        doThrow(new BusinessException("A empresa não pertence ao seu cliente."))
                .when(excluirEmpresaUseCase).execute(10L);

        mockMvc.perform(delete("/api/v1/empresas/{id}", 10L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("A empresa não pertence ao seu cliente."));

        verify(excluirEmpresaUseCase, times(1)).execute(10L);
    }

    @Test
    @DisplayName("POST /api/v1/empresas deve retornar 200 OK quando empresa criada com sucesso")
    void deveSalvarNovaEmpresaComSucesso() throws Exception {
        Empresa empresaSalva = Empresa.builder()
                .id(1L)
                .nome("Nova Empresa")
                .cnpj("12.345.678/0001-90")
                .telefone("11999998888")
                .status(Status.ATIVO)
                .departamentos(List.of(
                        Departamento.builder().id(10L).nome("TI").status(Status.ATIVO).build()
                ))
                .centros(List.of(
                        CentroCusto.builder().id(20L).nome("Financeiro").status(Status.ATIVO).build()
                ))
                .cargos(List.of(
                        Cargo.builder().id(30L).nome("Analista").status(Status.ATIVO).build()
                ))
                .build();

        when(salvarEmpresaUseCase.execute(any(Empresa.class))).thenReturn(empresaSalva);

        String jsonRequest = """
                {
                    "nome": "Nova Empresa",
                    "cnpj": "12.345.678/0001-90",
                    "telefone": "11999998888",
                    "status": "ATIVO",
                    "departamentos": [
                        { "nome": "TI", "status": "ATIVO" }
                    ],
                    "centros": [
                        { "nome": "Financeiro", "status": "ATIVO" }
                    ],
                    "cargos": [
                        { "nome": "Analista", "status": "ATIVO" }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Nova Empresa"))
                .andExpect(jsonPath("$.cnpj").value("12.345.678/0001-90"))
                .andExpect(jsonPath("$.departamentos[0].id").value(10))
                .andExpect(jsonPath("$.departamentos[0].nome").value("TI"))
                .andExpect(jsonPath("$.centros[0].id").value(20))
                .andExpect(jsonPath("$.cargos[0].id").value(30));

        verify(salvarEmpresaUseCase, times(1)).execute(any(Empresa.class));
    }

    @Test
    @DisplayName("POST /api/v1/empresas deve retornar 400 Bad Request quando campos obrigatórios não preenchidos")
    void deveRetornar400AoSalvarComPayloadInvalido() throws Exception {
        String jsonInvalido = """
                {
                    "nome": "",
                    "cnpj": "",
                    "status": null
                }
                """;

        mockMvc.perform(post("/api/v1/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(salvarEmpresaUseCase, never()).execute(any());
    }

    @Test
    @DisplayName("POST /api/v1/empresas deve retornar 200 OK quando empresa existente é editada com sucesso")
    void deveAtualizarEmpresaComSucesso() throws Exception {
        Empresa empresaSalva = Empresa.builder()
                .id(1L)
                .nome("Empresa Atualizada")
                .cnpj("12.345.678/0001-90")
                .celular("11988887777")
                .status(Status.ATIVO)
                .build();

        when(salvarEmpresaUseCase.execute(any(Empresa.class))).thenReturn(empresaSalva);

        String jsonRequest = """
                {
                    "id": 1,
                    "nome": "Empresa Atualizada",
                    "cnpj": "12.345.678/0001-90",
                    "celular": "11988887777",
                    "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Empresa Atualizada"))
                .andExpect(jsonPath("$.celular").value("11988887777"));

        verify(salvarEmpresaUseCase, times(1)).execute(any(Empresa.class));
    }

    @Test
    @DisplayName("POST /api/v1/empresas deve retornar 400 Bad Request quando BusinessException for lançada")
    void deveRetornar400QuandoBusinessExceptionAoSalvar() throws Exception {
        when(salvarEmpresaUseCase.execute(any(Empresa.class)))
                .thenThrow(new BusinessException("msg.add.telefone"));

        String jsonRequest = """
                {
                    "nome": "Empresa Sem Telefone",
                    "cnpj": "12.345.678/0001-90",
                    "status": "ATIVO"
                }
                """;

        mockMvc.perform(post("/api/v1/empresas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("msg.add.telefone"));

        verify(salvarEmpresaUseCase, times(1)).execute(any(Empresa.class));
    }
}
