package br.com.startjob.acesso.api.app;

import br.com.startjob.acesso.domain.entity.ClienteEntity;
import br.com.startjob.acesso.domain.entity.PedestreEntity;
import br.com.startjob.acesso.domain.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.domain.enumeration.Status;
import br.com.startjob.acesso.domain.repository.ClienteRepository;
import br.com.startjob.acesso.domain.repository.PedestreRepository;
import br.com.startjob.acesso.security.password.PasswordHasher;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AppAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PedestreRepository pedestreRepository;
    @Autowired
    private PasswordHasher passwordHasher;

    @BeforeEach
    void seed() {
        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Cliente App");
        cliente.setNomeUnidadeOrganizacional("desenvolvimento");
        cliente.setStatus(Status.ATIVO);
        cliente = clienteRepository.save(cliente);

        PedestreEntity pedestre = new PedestreEntity();
        pedestre.setNome("Maria");
        pedestre.setLogin("pedestre");
        pedestre.setSenha(passwordHasher.sha256Hex("123456"));
        pedestre.setStatus(Status.ATIVO);
        pedestre.setPerfilApp(PerfilAcessoApp.GERENCIAL);
        pedestre.setCliente(cliente);
        pedestreRepository.save(pedestre);
    }

    @Test
    void loginAppRetornaJwt() throws Exception {
        mockMvc.perform(post("/restful-services/app/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AppLoginRequest("desenvolvimento", "pedestre", "123456"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.nome").value("Maria"))
                .andExpect(jsonPath("$.usuario.perfil").value("GERENCIAL"));
    }

    @Test
    void loginAppSenhaInvalidaNaoVazaDetalheInterno() throws Exception {
        mockMvc.perform(post("/restful-services/app/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new AppLoginRequest("desenvolvimento", "pedestre", "errada"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_PASSWORD"));
    }

    @Test
    void healthNaoExpoeCaminhoDeArquivo() throws Exception {
        mockMvc.perform(get("/restful-services/app/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtConfigured").value(true))
                .andExpect(jsonPath("$.status").value("ok"));
    }
}
