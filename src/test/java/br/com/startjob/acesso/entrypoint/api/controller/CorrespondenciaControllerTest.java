package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.usecase.correspondencia.ConfirmarRetiradaCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.ExcluirCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.SalvarCorrespondenciaUseCase;
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

import java.time.LocalDateTime;
import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CorrespondenciaControllerTest {

    @Mock
    private SalvarCorrespondenciaUseCase salvarCorrespondenciaUseCase;

    @Mock
    private ExcluirCorrespondenciaUseCase excluirCorrespondenciaUseCase;

    @Mock
    private ConfirmarRetiradaCorrespondenciaUseCase confirmarRetiradaCorrespondenciaUseCase;

    @InjectMocks
    private CorrespondenciaController correspondenciaController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(correspondenciaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/correspondencias deve retornar 200 OK ao criar correspondência com sucesso")
    void deveSalvarNovaCorrespondenciaComSucesso() throws Exception {
        byte[] fotoBytes = new byte[]{10, 20, 30};
        String fotoBase64 = Base64.getEncoder().encodeToString(fotoBytes);

        Correspondencia salva = Correspondencia.builder()
                .id(1L)
                .destinatario(Pedestre.builder().id(50L).nome("Morador Maria").cpf("12345678900").build())
                .usuarioRecebeu(Usuario.builder().id(10L).nome("Porteiro João").build())
                .tipo("PACOTE")
                .codigoRastreio("BR123456789")
                .observacao("Caixa amassada")
                .confirmaRetirada("N")
                .dataRecebimento(LocalDateTime.of(2026, 11, 7, 10, 0))
                .fotoVolume(fotoBytes)
                .extensaoFoto("jpg")
                .build();

        when(salvarCorrespondenciaUseCase.execute(any(Correspondencia.class), eq(false))).thenReturn(salva);

        String jsonRequest = """
                {
                    "idDestinatario": 50,
                    "tipo": "PACOTE",
                    "codigoRastreio": "BR123456789",
                    "observacao": "Caixa amassada",
                    "fotoBase64": "%s"
                }
                """.formatted(fotoBase64);

        mockMvc.perform(post("/api/v1/correspondencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.idDestinatario").value(50))
                .andExpect(jsonPath("$.nomeDestinatario").value("Morador Maria"))
                .andExpect(jsonPath("$.cpfDestinatario").value("12345678900"))
                .andExpect(jsonPath("$.idUsuarioRecebeu").value(10))
                .andExpect(jsonPath("$.nomeUsuarioRecebeu").value("Porteiro João"))
                .andExpect(jsonPath("$.tipo").value("PACOTE"))
                .andExpect(jsonPath("$.codigoRastreio").value("BR123456789"))
                .andExpect(jsonPath("$.observacao").value("Caixa amassada"))
                .andExpect(jsonPath("$.confirmaRetirada").value("N"))
                .andExpect(jsonPath("$.possuiFoto").value(true))
                .andExpect(jsonPath("$.fotoBase64").value(fotoBase64))
                .andExpect(jsonPath("$.extensaoFoto").value("jpg"));

        verify(salvarCorrespondenciaUseCase, times(1)).execute(any(Correspondencia.class), eq(false));
    }

    @Test
    @DisplayName("POST /api/v1/correspondencias deve retornar 400 Bad Request quando campos obrigatórios ausentes")
    void deveRetornar400QuandoCamposObrigatoriosAusentes() throws Exception {
        String jsonInvalido = """
                {
                    "idDestinatario": null,
                    "tipo": ""
                }
                """;

        mockMvc.perform(post("/api/v1/correspondencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(salvarCorrespondenciaUseCase, never()).execute(any(), anyBoolean());
    }

    @Test
    @DisplayName("POST /api/v1/correspondencias deve retornar 400 Bad Request quando BusinessException for lançada")
    void deveRetornar400QuandoBusinessException() throws Exception {
        when(salvarCorrespondenciaUseCase.execute(any(Correspondencia.class), eq(false)))
                .thenThrow(new BusinessException("Você não possui permissão para salvar correspondências."));

        String jsonRequest = """
                {
                    "idDestinatario": 50,
                    "tipo": "CARTA"
                }
                """;

        mockMvc.perform(post("/api/v1/correspondencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Você não possui permissão para salvar correspondências."));

        verify(salvarCorrespondenciaUseCase, times(1)).execute(any(Correspondencia.class), eq(false));
    }

    @Test
    @DisplayName("POST /api/v1/correspondencias/{id}/confirmar-retirada deve retornar 200 OK quando retirada confirmada com sucesso")
    void deveConfirmarRetiradaComSucesso() throws Exception {
        Correspondencia atualizada = Correspondencia.builder()
                .id(10L)
                .destinatario(Pedestre.builder().id(50L).nome("Morador Maria").build())
                .tipo("PACOTE")
                .confirmaRetirada("S")
                .dataRetirada(LocalDateTime.of(2026, 11, 7, 11, 0))
                .nomeQuemRetirou("João da Silva")
                .documentoQuemRetirou("12345678900")
                .build();

        when(confirmarRetiradaCorrespondenciaUseCase.execute(10L, "João da Silva", "12345678900"))
                .thenReturn(atualizada);

        String jsonRequest = """
                {
                    "nomeQuemRetirou": "João da Silva",
                    "documentoQuemRetirou": "12345678900"
                }
                """;

        mockMvc.perform(post("/api/v1/correspondencias/{id}/confirmar-retirada", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.confirmaRetirada").value("S"))
                .andExpect(jsonPath("$.nomeQuemRetirou").value("João da Silva"))
                .andExpect(jsonPath("$.documentoQuemRetirou").value("12345678900"));

        verify(confirmarRetiradaCorrespondenciaUseCase, times(1))
                .execute(10L, "João da Silva", "12345678900");
    }

    @Test
    @DisplayName("POST /api/v1/correspondencias/{id}/confirmar-retirada deve retornar 400 Bad Request quando nome ou documento vazios")
    void deveRetornar400AoConfirmarRetiradaComCamposVazios() throws Exception {
        String jsonInvalido = """
                {
                    "nomeQuemRetirou": "",
                    "documentoQuemRetirou": ""
                }
                """;

        mockMvc.perform(post("/api/v1/correspondencias/{id}/confirmar-retirada", 10L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));

        verify(confirmarRetiradaCorrespondenciaUseCase, never()).execute(any(), any(), any());
    }

    @Test
    @DisplayName("DELETE /api/v1/correspondencias/{id} deve retornar 204 No Content quando sucesso")
    void deveExcluirCorrespondenciaComSucesso() throws Exception {
        doNothing().when(excluirCorrespondenciaUseCase).execute(10L);

        mockMvc.perform(delete("/api/v1/correspondencias/{id}", 10L))
                .andExpect(status().isNoContent());

        verify(excluirCorrespondenciaUseCase, times(1)).execute(10L);
    }

    @Test
    @DisplayName("DELETE /api/v1/correspondencias/{id} deve retornar 400 Bad Request quando sem permissão")
    void deveRetornar400QuandoSemPermissaoAoExcluir() throws Exception {
        doThrow(new BusinessException("Você não possui permissão para excluir correspondências."))
                .when(excluirCorrespondenciaUseCase).execute(10L);

        mockMvc.perform(delete("/api/v1/correspondencias/{id}", 10L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Você não possui permissão para excluir correspondências."));

        verify(excluirCorrespondenciaUseCase, times(1)).execute(10L);
    }

    @Test
    @DisplayName("DELETE /api/v1/correspondencias/{id} deve retornar 400 Bad Request quando correspondência não encontrada")
    void deveRetornar400QuandoCorrespondenciaNaoEncontradaAoExcluir() throws Exception {
        doThrow(new BusinessException("Correspondência não encontrada."))
                .when(excluirCorrespondenciaUseCase).execute(99L);

        mockMvc.perform(delete("/api/v1/correspondencias/{id}", 99L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Correspondência não encontrada."));

        verify(excluirCorrespondenciaUseCase, times(1)).execute(99L);
    }
}
