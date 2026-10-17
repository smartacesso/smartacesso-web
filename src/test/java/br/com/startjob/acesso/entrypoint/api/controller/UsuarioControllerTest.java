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

    @Test
    @DisplayName("GET /api/v1/usuarios/{id} deve retornar 200 OK com todas as informações do usuário e cliente sem planos e sem senha")
    void deveBuscarUsuarioPorIdComSucesso() throws Exception {
        var cliente = br.com.startjob.acesso.core.domain.cliente.Cliente.builder()
                .id(10L)
                .nome("Cliente Alpha")
                .cnpj("12.345.678/0001-90")
                .email("contato@alpha.com")
                .telefone("3432320000")
                .celular("34999990000")
                .contato("Diretor")
                .status(br.com.startjob.acesso.core.enumeration.Status.ATIVO)
                .nomeUnidadeOrganizacional("Unidade Central")
                .build();

        var usuario = br.com.startjob.acesso.core.domain.usuario.Usuario.builder()
                .id(1L)
                .nome("João da Silva")
                .login("joao.silva")
                .senha("SenhaSuperSecreta123!")
                .email("joao@example.com")
                .cpf("123.456.789-00")
                .rg("MG-12.345.678")
                .telefone("3432323232")
                .celular("34999999999")
                .dataNascimento(java.time.LocalDateTime.of(1990, 5, 15, 10, 30))
                .status(br.com.startjob.acesso.core.enumeration.Status.ATIVO)
                .perfil(br.com.startjob.acesso.core.enumeration.PerfilAcesso.ADMINISTRADOR)
                .acessaWeb(true)
                .cadastroSimples(false)
                .usuarioMaster(false)
                .expedidora(true)
                .cliente(cliente)
                .build();

        when(consultaUsuarioUseCase.buscarPorId(1L)).thenReturn(usuario);

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.login").value("joao.silva"))
                .andExpect(jsonPath("$.email").value("joao@example.com"))
                .andExpect(jsonPath("$.cpf").value("123.456.789-00"))
                .andExpect(jsonPath("$.rg").value("MG-12.345.678"))
                .andExpect(jsonPath("$.telefone").value("3432323232"))
                .andExpect(jsonPath("$.celular").value("34999999999"))
                .andExpect(jsonPath("$.dataNascimento").value("1990-05-15T10:30:00"))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.perfil").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.acessaWeb").value(true))
                .andExpect(jsonPath("$.cadastroSimples").value(false))
                .andExpect(jsonPath("$.usuarioMaster").value(false))
                .andExpect(jsonPath("$.expedidora").value(true))
                .andExpect(jsonPath("$.cliente.id").value(10))
                .andExpect(jsonPath("$.cliente.nome").value("Cliente Alpha"))
                .andExpect(jsonPath("$.cliente.cnpj").value("12.345.678/0001-90"))
                .andExpect(jsonPath("$.cliente.status").value("ATIVO"))
                .andExpect(jsonPath("$.cliente.planos").doesNotExist())
                .andExpect(jsonPath("$.senha").doesNotExist());

        verify(consultaUsuarioUseCase, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{id} deve retornar 400 Bad Request quando usuario nao encontrado")
    void deveRetornar400QuandoUsuarioNaoEncontradoEmBuscarPorId() throws Exception {
        when(consultaUsuarioUseCase.buscarPorId(99L))
                .thenThrow(new BusinessException("Usuário não encontrado."));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/usuarios/{id}", 99L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado."));

        verify(consultaUsuarioUseCase, times(1)).buscarPorId(99L);
    }

    @Test
    @DisplayName("GET /api/v1/usuarios/{id} deve retornar 400 Bad Request quando usuario pertencer a outro cliente")
    void deveRetornar400QuandoUsuarioPertencerAOutroCliente() throws Exception {
        when(consultaUsuarioUseCase.buscarPorId(100L))
                .thenThrow(new BusinessException("O usuário não pertence ao seu cliente."));

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/api/v1/usuarios/{id}", 100L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("BUSINESS_ERROR"))
                .andExpect(jsonPath("$.message").value("O usuário não pertence ao seu cliente."));

        verify(consultaUsuarioUseCase, times(1)).buscarPorId(100L);
    }
}
