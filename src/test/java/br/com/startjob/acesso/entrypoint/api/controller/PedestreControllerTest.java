package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Genero;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.usecase.pedestre.SalvarPedestreUseCase;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PedestreControllerTest {

    @Mock
    private SalvarPedestreUseCase salvarPedestreUseCase;

    @InjectMocks
    private PedestreController pedestreController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pedestreController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/pedestres deve retornar 200 OK ao salvar pedestre com sucesso")
    void deveSalvarPedestreComSucesso() throws Exception {
        Pedestre salvo = Pedestre.builder()
                .id(10L)
                .nome("João da Silva")
                .status(Status.ATIVO)
                .tipo(TipoPedestre.PEDESTRE)
                .cpf("52998224725")
                .genero(Genero.MASCULINO)
                .dataNascimento(LocalDate.of(1992, 4, 15))
                .empresa(Empresa.builder().id(100L).nome("Empresa ABC").build())
                .regras(List.of(
                        PedestreRegra.builder()
                                .id(200L)
                                .regra(Regra.builder()
                                        .id(50L)
                                        .nome("Regra Geral")
                                        .tipo(TipoRegra.ACESSO_UNICO)
                                        .build())
                                .qtdeDeCreditos(1L)
                                .qtdeTotalDeCreditos(1L)
                                .build()
                ))
                .build();

        when(salvarPedestreUseCase.execute(any(Pedestre.class), eq(false))).thenReturn(salvo);

        String payload = """
                {
                    "nome": "João da Silva",
                    "status": "ATIVO",
                    "tipo": "PEDESTRE",
                    "cpf": "529.982.247-25",
                    "genero": "MASCULINO",
                    "dataNascimento": "1992-04-15",
                    "idEmpresa": 100,
                    "fotoBase64": "data:image/jpeg;base64,AQID",
                    "regras": [
                        {
                            "idRegra": 50
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/v1/pedestres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.tipo").value("PEDESTRE"))
                .andExpect(jsonPath("$.cpf").value("52998224725"))
                .andExpect(jsonPath("$.genero").value("MASCULINO"))
                .andExpect(jsonPath("$.generoDescricao").value("Masculino"))
                .andExpect(jsonPath("$.idEmpresa").value(100))
                .andExpect(jsonPath("$.nomeEmpresa").value("Empresa ABC"))
                .andExpect(jsonPath("$.regras[0].id").value(200))
                .andExpect(jsonPath("$.regras[0].idRegra").value(50))
                .andExpect(jsonPath("$.regras[0].nomeRegra").value("Regra Geral"));

        verify(salvarPedestreUseCase).execute(any(Pedestre.class), eq(false));
    }

    @Test
    @DisplayName("POST /api/v1/pedestres deve retornar 400 Bad Request quando nome estiver em branco")
    void deveRetornar400QuandoNomeEmBranco() throws Exception {
        String payloadInvalido = """
                {
                    "nome": "",
                    "tipo": "PEDESTRE"
                }
                """;

        mockMvc.perform(post("/api/v1/pedestres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest());

        verify(salvarPedestreUseCase, never()).execute(any(), anyBoolean());
    }

    @Test
    @DisplayName("POST /api/v1/pedestres deve retornar 400 Bad Request quando BusinessException for lançada")
    void deveRetornar400QuandoBusinessException() throws Exception {
        when(salvarPedestreUseCase.execute(any(Pedestre.class), eq(false)))
                .thenThrow(new BusinessException("CPF já cadastrado para outro pedestre."));

        String payload = """
                {
                    "nome": "João da Silva",
                    "tipo": "PEDESTRE",
                    "cpf": "52998224725"
                }
                """;

        mockMvc.perform(post("/api/v1/pedestres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF já cadastrado para outro pedestre."));
    }
}
