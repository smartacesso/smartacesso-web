package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.usecase.permissao.ConsultaMatrizPermissaoWebUseCase;
import br.com.startjob.acesso.core.usecase.permissao.RestaurarMatrizPermissaoWebUseCase;
import br.com.startjob.acesso.core.usecase.permissao.SalvarMatrizPermissaoWebUseCase;
import br.com.startjob.acesso.entrypoint.api.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WebPermissaoControllerTest {

    @Mock
    private ConsultaMatrizPermissaoWebUseCase consultaMatrizPermissaoWebUseCase;

    @Mock
    private SalvarMatrizPermissaoWebUseCase salvarMatrizPermissaoWebUseCase;

    @Mock
    private RestaurarMatrizPermissaoWebUseCase restaurarMatrizPermissaoWebUseCase;

    @InjectMocks
    private WebPermissaoController webPermissaoController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webPermissaoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/permissoes-web deve retornar 200 OK com a matriz de permissões")
    void deveCarregarMatrizComSucesso() throws Exception {
        WebPermissaoLinha linha = WebPermissaoLinha.builder()
                .codigo(WebPermissao.PEDESTRE_VER.getCodigo())
                .descricao(WebPermissao.PEDESTRE_VER.getDescricao())
                .administrador(true)
                .gerente(true)
                .operador(true)
                .porteiro(false)
                .cuidador(true)
                .responsavel(false)
                .build();

        when(consultaMatrizPermissaoWebUseCase.execute()).thenReturn(List.of(linha));

        mockMvc.perform(get("/api/v1/permissoes-web"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("WEB_PEDESTRE_VER"))
                .andExpect(jsonPath("$[0].descricao").value("Ver cadastro de pedestres"))
                .andExpect(jsonPath("$[0].administrador").value(true))
                .andExpect(jsonPath("$[0].gerente").value(true))
                .andExpect(jsonPath("$[0].operador").value(true))
                .andExpect(jsonPath("$[0].porteiro").value(false))
                .andExpect(jsonPath("$[0].cuidador").value(true))
                .andExpect(jsonPath("$[0].responsavel").value(false));

        verify(consultaMatrizPermissaoWebUseCase).execute();
    }

    @Test
    @DisplayName("PUT /api/v1/permissoes-web deve atualizar matriz e retornar 200 OK")
    void deveSalvarMatrizComSucesso() throws Exception {
        WebPermissaoLinha linhaAtualizada = WebPermissaoLinha.builder()
                .codigo(WebPermissao.PEDESTRE_VER.getCodigo())
                .descricao(WebPermissao.PEDESTRE_VER.getDescricao())
                .administrador(true)
                .gerente(true)
                .operador(false)
                .porteiro(true)
                .cuidador(true)
                .responsavel(false)
                .build();

        when(salvarMatrizPermissaoWebUseCase.execute(anyList())).thenReturn(List.of(linhaAtualizada));

        String jsonRequest = """
                [
                    {
                        "codigo": "WEB_PEDESTRE_VER",
                        "administrador": true,
                        "gerente": true,
                        "operador": false,
                        "porteiro": true,
                        "cuidador": true,
                        "responsavel": false
                    }
                ]
                """;

        mockMvc.perform(put("/api/v1/permissoes-web")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("WEB_PEDESTRE_VER"))
                .andExpect(jsonPath("$[0].operador").value(false))
                .andExpect(jsonPath("$[0].porteiro").value(true));

        verify(salvarMatrizPermissaoWebUseCase).execute(anyList());
    }

    @Test
    @DisplayName("PUT /api/v1/permissoes-web deve retornar 400 Bad Request quando BusinessException ocorrer")
    void deveRetornar400QuandoSemPermissaoNoPut() throws Exception {
        when(salvarMatrizPermissaoWebUseCase.execute(anyList()))
                .thenThrow(new BusinessException("Você não possui permissão para editar a matriz de permissões."));

        String jsonRequest = """
                [
                    {
                        "codigo": "WEB_PEDESTRE_VER",
                        "administrador": true,
                        "gerente": true,
                        "operador": true,
                        "porteiro": false,
                        "cuidador": true,
                        "responsavel": false
                    }
                ]
                """;

        mockMvc.perform(put("/api/v1/permissoes-web")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Você não possui permissão para editar a matriz de permissões."));
    }

    @Test
    @DisplayName("POST /api/v1/permissoes-web/restaurar-padrao deve restaurar padrão do cliente e retornar 200 OK com permissões atualizadas")
    void deveRestaurarPadraoComSucesso() throws Exception {
        WebPermissaoLinha linhaPadrao = WebPermissaoLinha.builder()
                .codigo(WebPermissao.PEDESTRE_VER.getCodigo())
                .descricao(WebPermissao.PEDESTRE_VER.getDescricao())
                .administrador(true)
                .gerente(true)
                .operador(true)
                .porteiro(false)
                .cuidador(true)
                .responsavel(false)
                .build();

        when(restaurarMatrizPermissaoWebUseCase.execute()).thenReturn(List.of(linhaPadrao));

        mockMvc.perform(post("/api/v1/permissoes-web/restaurar-padrao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("WEB_PEDESTRE_VER"))
                .andExpect(jsonPath("$[0].descricao").value("Ver cadastro de pedestres"))
                .andExpect(jsonPath("$[0].administrador").value(true))
                .andExpect(jsonPath("$[0].gerente").value(true))
                .andExpect(jsonPath("$[0].operador").value(true))
                .andExpect(jsonPath("$[0].porteiro").value(false))
                .andExpect(jsonPath("$[0].cuidador").value(true))
                .andExpect(jsonPath("$[0].responsavel").value(false));

        verify(restaurarMatrizPermissaoWebUseCase).execute();
    }

    @Test
    @DisplayName("POST /api/v1/permissoes-web/restaurar-padrao deve retornar 400 Bad Request quando usuário não tiver permissão")
    void deveRetornar400QuandoSemPermissaoAoRestaurarPadrao() throws Exception {
        when(restaurarMatrizPermissaoWebUseCase.execute())
                .thenThrow(new BusinessException("Você não possui permissão para restaurar a matriz de permissões."));

        mockMvc.perform(post("/api/v1/permissoes-web/restaurar-padrao"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Você não possui permissão para restaurar a matriz de permissões."));

        verify(restaurarMatrizPermissaoWebUseCase).execute();
    }
}
