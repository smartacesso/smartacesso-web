package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.parametro.OpcaoParametro;
import br.com.startjob.acesso.core.domain.parametro.ParametroItem;
import br.com.startjob.acesso.core.domain.parametro.SecaoParametroGrupo;
import br.com.startjob.acesso.core.enumeration.SecaoParametro;
import br.com.startjob.acesso.core.enumeration.TipoDadoParametro;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.usecase.parametro.ConsultaParametrosUseCase;
import br.com.startjob.acesso.core.usecase.parametro.SalvarParametrosUseCase;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ParametroControllerTest {

    @Mock
    private ConsultaParametrosUseCase consultaParametrosUseCase;

    @Mock
    private SalvarParametrosUseCase salvarParametrosUseCase;

    @InjectMocks
    private ParametroController parametroController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(parametroController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/parametros deve retornar 200 OK com seções e parâmetros tipados")
    void deveCarregarParametrosComSucesso() throws Exception {
        ParametroItem itemBool = ParametroItem.builder()
                .codigo("GERAR_MATRICULA_SEQUENCIAL")
                .nome("Gerar matricula sequencial")
                .rotulo("Gerar matricula sequencial")
                .secao(SecaoParametro.CADASTRO)
                .tipo(TipoDadoParametro.BOOLEAN)
                .valor(true)
                .valorPadrao(false)
                .build();

        ParametroItem itemLista = ParametroItem.builder()
                .codigo("CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE")
                .nome("Campos obrigatórios para cadastro de pedestres")
                .rotulo("Campos obrigatórios para cadastro de pedestres")
                .secao(SecaoParametro.CADASTRO)
                .tipo(TipoDadoParametro.LISTA_STRING)
                .valor(List.of("cpf", "foto"))
                .valorPadrao(List.of())
                .opcoes(List.of(new OpcaoParametro("cpf", "CPF"), new OpcaoParametro("foto", "Foto")))
                .build();

        SecaoParametroGrupo secao = SecaoParametroGrupo.builder()
                .secao(SecaoParametro.CADASTRO)
                .titulo("Cadastro")
                .parametros(List.of(itemBool, itemLista))
                .build();

        when(consultaParametrosUseCase.execute()).thenReturn(List.of(secao));

        mockMvc.perform(get("/api/v1/parametros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].secao").value("CADASTRO"))
                .andExpect(jsonPath("$[0].titulo").value("Cadastro"))
                .andExpect(jsonPath("$[0].parametros[0].codigo").value("GERAR_MATRICULA_SEQUENCIAL"))
                .andExpect(jsonPath("$[0].parametros[0].tipo").value("BOOLEAN"))
                .andExpect(jsonPath("$[0].parametros[0].valor").value(true))
                .andExpect(jsonPath("$[0].parametros[1].codigo").value("CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE"))
                .andExpect(jsonPath("$[0].parametros[1].tipo").value("LISTA_STRING"))
                .andExpect(jsonPath("$[0].parametros[1].valor[0]").value("cpf"))
                .andExpect(jsonPath("$[0].parametros[1].valor[1]").value("foto"));

        verify(consultaParametrosUseCase).execute();
    }

    @Test
    @DisplayName("PUT /api/v1/parametros deve salvar parâmetros de tipos variados e retornar 200 OK")
    void deveSalvarParametrosComSucesso() throws Exception {
        ParametroItem itemAtualizado = ParametroItem.builder()
                .codigo("ESCOLHER_QTDE_DIGITOS_CARTAO")
                .nome("Escolher quantidade de digítos do cartão (até 10)")
                .rotulo("Escolher quantidade de dígitos do cartão (1 a 16)")
                .secao(SecaoParametro.CARTAO)
                .tipo(TipoDadoParametro.INTEGER)
                .valor(12)
                .valorPadrao(10)
                .min(1)
                .max(16)
                .build();

        SecaoParametroGrupo secao = SecaoParametroGrupo.builder()
                .secao(SecaoParametro.CARTAO)
                .titulo("Cartão e matrícula")
                .parametros(List.of(itemAtualizado))
                .build();

        when(salvarParametrosUseCase.execute(anyList())).thenReturn(List.of(secao));

        String jsonRequest = """
                [
                    {
                        "codigo": "GERAR_MATRICULA_SEQUENCIAL",
                        "valor": true
                    },
                    {
                        "codigo": "ESCOLHER_QTDE_DIGITOS_CARTAO",
                        "valor": 12
                    },
                    {
                        "codigo": "CAMPOS_OBRIGATORIOS_CADASTRO_PEDESTRE",
                        "valor": ["cpf", "email", "foto"]
                    }
                ]
                """;

        mockMvc.perform(put("/api/v1/parametros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].secao").value("CARTAO"))
                .andExpect(jsonPath("$[0].parametros[0].codigo").value("ESCOLHER_QTDE_DIGITOS_CARTAO"))
                .andExpect(jsonPath("$[0].parametros[0].valor").value(12));

        verify(salvarParametrosUseCase).execute(anyList());
    }

    @Test
    @DisplayName("PUT /api/v1/parametros deve retornar 400 Bad Request quando BusinessException ocorrer")
    void deveRetornar400QuandoErroDeNegocio() throws Exception {
        when(salvarParametrosUseCase.execute(anyList()))
                .thenThrow(new BusinessException("O valor do parâmetro 'ESCOLHER_QTDE_DIGITOS_CARTAO' deve ser no máximo 16."));

        String jsonRequest = """
                [
                    {
                        "codigo": "ESCOLHER_QTDE_DIGITOS_CARTAO",
                        "valor": 99
                    }
                ]
                """;

        mockMvc.perform(put("/api/v1/parametros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O valor do parâmetro 'ESCOLHER_QTDE_DIGITOS_CARTAO' deve ser no máximo 16."));
    }
}
