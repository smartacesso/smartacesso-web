package br.com.startjob.acesso.api.legacy;

import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.PlanoEntity;
import br.com.startjob.acesso.dataprovider.entity.UsuarioEntity;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.PlanoRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import br.com.startjob.acesso.entrypoint.api.security.password.PasswordHasher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoginApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PlanoRepository planoRepository;
    @Autowired
    private PasswordHasher passwordHasher;

    @BeforeEach
    void seed() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Academia Teste");
        cliente.setNomeUnidadeOrganizacional("desenvolvimento");
        cliente.setStatus(Status.ATIVO);
        cliente = clienteRepository.save(cliente);

        PlanoEntity plano = new PlanoEntity();
        plano.setNome("Plano");
        plano.setStatus(Status.ATIVO);
        plano.setInicio(LocalDateTime.now());
        plano.setFim(LocalDateTime.now().plusDays(30));
        plano.setCliente(cliente);
        planoRepository.save(plano);

        UsuarioEntity admin = new UsuarioEntity();
        admin.setNome("Admin");
        admin.setLogin("admin");
        admin.setSenha(passwordHasher.sha256Hex("123456"));
        admin.setStatus(Status.ATIVO);
        admin.setPerfil(PerfilAcesso.ADMINISTRADOR);
        admin.setAcessaWeb(true);
        admin.setCliente(cliente);
        usuarioRepository.save(admin);
    }

    @Test
    void healthLoginAction() throws Exception {
        mockMvc.perform(get("/restful-services/login/action"))
                .andExpect(status().isOk())
                .andExpect(content().string("working"));
    }

    @Test
    void loginDoSucessoPreservaContratoSwing() throws Exception {
        mockMvc.perform(get("/restful-services/login/do")
                        .param("unidadeName", "desenvolvimento")
                        .param("loginName", "admin")
                        .param("passwd", "123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.object.login").value("admin"))
                .andExpect(jsonPath("$.object.status").value("ATIVO"))
                .andExpect(jsonPath("$.object.perfil").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.object.senha").exists())
                .andExpect(jsonPath("$.object.token").exists())
                .andExpect(jsonPath("$.object.cliente.id").exists())
                .andExpect(jsonPath("$.object.permissoes").isArray())
                .andExpect(jsonPath("$.object.permissoes[0]").exists());
    }

    @Test
    void loginDoSenhaInvalidaRetorna500ComChaveLegada() throws Exception {
        mockMvc.perform(get("/restful-services/login/do")
                        .param("unidadeName", "desenvolvimento")
                        .param("loginName", "admin")
                        .param("passwd", "errada"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("msgs.account.usuario.senha.invalida"));
    }

    @Test
    void loginDoUnidadeInexistente() throws Exception {
        mockMvc.perform(get("/restful-services/login/do")
                        .param("unidadeName", "naoexiste")
                        .param("loginName", "admin")
                        .param("passwd", "123456"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("msgs.account.unidade.nao.encontrada"));
    }

    @Test
    void loginInternoNaoDevolveHashDaSenha() throws Exception {
        String hash = passwordHasher.sha256Hex("123456");
        mockMvc.perform(get("/restful-services/login/interno")
                        .param("unidadeName", "desenvolvimento")
                        .param("loginName", "admin")
                        .param("passwd", hash))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.object.login").value("admin"))
                .andExpect(jsonPath("$.object.senha").value(org.hamcrest.Matchers.nullValue()));
    }

    @Test
    void accessActionWorking() throws Exception {
        mockMvc.perform(get("/restful-services/access/action"))
                .andExpect(status().isOk())
                .andExpect(content().string("working"));
    }
}
