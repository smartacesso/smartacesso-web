package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.usecase.usuario.ConsultaUsuarioUseCase;
import br.com.startjob.acesso.core.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.startjob.acesso.core.usecase.usuario.SalvarUsuarioUseCase;
import br.com.startjob.acesso.entrypoint.api.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private ConsultaUsuarioUseCase consultaUsuarioUseCase;

    @Mock
    private SalvarUsuarioUseCase salvarUsuarioUseCase;

    @Mock
    private ExcluirUsuarioUseCase excluirUsuarioUseCase;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{id} deve retornar 204 No Content quando sucesso")
    void deveExcluirUsuarioComSucesso() throws Exception {
        doNothing().when(excluirUsuarioUseCase).execute(5L);

        mockMvc.perform(delete("/api/v1/usuarios/{id}", 5L))
                .andExpect(status().isNoContent());

        verify(excluirUsuarioUseCase, times(1)).execute(5L);
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{id} deve retornar 400 Bad Request quando sem permissao")
    void deveRetornar400QuandoSemPermissao() throws Exception {
        doThrow(new BusinessException("Você não possui permissão para excluir usuários."))
                .when(excluirUsuarioUseCase).execute(5L);

        mockMvc.perform(delete("/api/v1/usuarios/{id}", 5L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Você não possui permissão para excluir usuários."));

        verify(excluirUsuarioUseCase, times(1)).execute(5L);
    }

    @Test
    @DisplayName("DELETE /api/v1/usuarios/{id} deve retornar 400 Bad Request quando usuario nao encontrado")
    void deveRetornar400QuandoUsuarioNaoEncontrado() throws Exception {
        doThrow(new BusinessException("Usuário não encontrado."))
                .when(excluirUsuarioUseCase).execute(99L);

        mockMvc.perform(delete("/api/v1/usuarios/{id}", 99L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));

        verify(excluirUsuarioUseCase, times(1)).execute(99L);
    }
}
