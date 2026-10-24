package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import br.com.startjob.acesso.core.domain.pedestre.Biometria;
import br.com.startjob.acesso.core.domain.pedestre.Documento;
import br.com.startjob.acesso.core.domain.pedestre.HistoricoCota;
import br.com.startjob.acesso.core.domain.pedestre.MensagemEquipamento;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreEquipamento;
import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Dedo;
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
    @DisplayName("POST /api/v1/pedestres deve retornar 200 OK ao salvar pedestre com todas as abas")
    void deveSalvarPedestreComSucesso() throws Exception {
        Pedestre salvo = Pedestre.builder()
                .id(10L)
                .nome("João da Silva")
                .status(Status.ATIVO)
                .tipo(TipoPedestre.PEDESTRE)
                .cpf("52998224725")
                .qtdAcessoAntesSinc(3)
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
                .equipamentos(List.of(
                        PedestreEquipamento.builder()
                                .id(300L)
                                .equipamento(Equipamento.builder().id(30L).nome("Catraca 1").modelo("Henry").build())
                                .validade(LocalDate.of(2027, 12, 31))
                                .build()
                ))
                .documentos(List.of(
                        Documento.builder()
                                .id(400L)
                                .nome("RG Frente")
                                .validade(LocalDate.of(2030, 1, 1))
                                .build()
                ))
                .biometrias(List.of(
                        Biometria.builder()
                                .id(500L)
                                .dedo(Dedo.RIGHT_INDEX)
                                .build()
                ))
                .mensagensPersonalizadas(List.of(
                        MensagemEquipamento.builder()
                                .id(600L)
                                .nome("Aviso")
                                .status(Status.ATIVO)
                                .mensagem("Bem-vindo")
                                .quantidade(5L)
                                .build()
                ))
                .cotas(List.of(
                        HistoricoCota.builder()
                                .id(700L)
                                .mes(9)
                                .ano(2026)
                                .cotaMensal(20L)
                                .build()
                ))
                .responsaveis(List.of(
                        Pedestre.builder().id(80L).nome("Responsável 1").build()
                ))
                .build();

        when(salvarPedestreUseCase.execute(any(Pedestre.class), eq(false))).thenReturn(salvo);

        String payload = """
                {
                    "nome": "João da Silva",
                    "status": "ATIVO",
                    "tipo": "PEDESTRE",
                    "cpf": "529.982.247-25",
                    "qtdAcessoAntesSinc": 3,
                    "genero": "MASCULINO",
                    "dataNascimento": "1992-04-15",
                    "idEmpresa": 100,
                    "fotoBase64": "data:image/jpeg;base64,AQID",
                    "regras": [
                        {
                            "idRegra": 50
                        }
                    ],
                    "equipamentos": [
                        {
                            "idEquipamento": 30,
                            "validade": "2027-12-31"
                        }
                    ],
                    "documentos": [
                        {
                            "nome": "RG Frente",
                            "arquivoBase64": "AQID",
                            "validade": "2030-01-01"
                        }
                    ],
                    "biometrias": [
                        {
                            "dedo": "RIGHT_INDEX",
                            "templateBase64": "AQID"
                        }
                    ],
                    "mensagensPersonalizadas": [
                        {
                            "nome": "Aviso",
                            "mensagem": "Bem-vindo",
                            "quantidade": 5
                        }
                    ],
                    "cotas": [
                        {
                            "mes": 9,
                            "ano": 2026,
                            "cotaMensal": 20
                        }
                    ],
                    "idsResponsaveis": [80]
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
                .andExpect(jsonPath("$.qtdAcessoAntesSinc").value(3))
                .andExpect(jsonPath("$.genero").value("MASCULINO"))
                .andExpect(jsonPath("$.generoDescricao").value("Masculino"))
                .andExpect(jsonPath("$.idEmpresa").value(100))
                .andExpect(jsonPath("$.nomeEmpresa").value("Empresa ABC"))
                .andExpect(jsonPath("$.regras[0].id").value(200))
                .andExpect(jsonPath("$.regras[0].idRegra").value(50))
                .andExpect(jsonPath("$.regras[0].nomeRegra").value("Regra Geral"))
                .andExpect(jsonPath("$.equipamentos[0].id").value(300))
                .andExpect(jsonPath("$.equipamentos[0].nomeEquipamento").value("Catraca 1"))
                .andExpect(jsonPath("$.documentos[0].id").value(400))
                .andExpect(jsonPath("$.documentos[0].nome").value("RG Frente"))
                .andExpect(jsonPath("$.biometrias[0].id").value(500))
                .andExpect(jsonPath("$.biometrias[0].dedoDescricao").value("Indicador direito"))
                .andExpect(jsonPath("$.mensagensPersonalizadas[0].id").value(600))
                .andExpect(jsonPath("$.cotas[0].id").value(700))
                .andExpect(jsonPath("$.cotas[0].mesExtenso").value("Outubro"))
                .andExpect(jsonPath("$.responsaveis[0].id").value(80))
                .andExpect(jsonPath("$.responsaveis[0].nome").value("Responsável 1"));

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
