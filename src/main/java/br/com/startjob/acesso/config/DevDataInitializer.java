package br.com.startjob.acesso.config;

import br.com.startjob.acesso.domain.entity.ClienteEntity;
import br.com.startjob.acesso.domain.entity.PedestreEntity;
import br.com.startjob.acesso.domain.entity.PlanoEntity;
import br.com.startjob.acesso.domain.entity.UsuarioEntity;
import br.com.startjob.acesso.domain.enumeration.PerfilAcesso;
import br.com.startjob.acesso.domain.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.domain.enumeration.Status;
import br.com.startjob.acesso.domain.repository.ClienteRepository;
import br.com.startjob.acesso.domain.repository.PedestreRepository;
import br.com.startjob.acesso.domain.repository.PlanoRepository;
import br.com.startjob.acesso.domain.repository.UsuarioRepository;
import br.com.startjob.acesso.security.password.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@Profile("dev")
public class DevDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private final SmartAcessoProperties properties;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedestreRepository pedestreRepository;
    private final PlanoRepository planoRepository;
    private final PasswordHasher passwordHasher;

    public DevDataInitializer(
            SmartAcessoProperties properties,
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            PedestreRepository pedestreRepository,
            PlanoRepository planoRepository,
            PasswordHasher passwordHasher
    ) {
        this.properties = properties;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedestreRepository = pedestreRepository;
        this.planoRepository = planoRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (properties.bootstrap() == null || !properties.bootstrap().enabled()) {
            return;
        }
        String unidade = properties.bootstrap().unidade();
        if (clienteRepository.findByUnidadeAtiva(unidade).isPresent()) {
            return;
        }

        ClienteEntity cliente = new ClienteEntity();
        cliente.setNome("Ambiente de desenvolvimento");
        cliente.setNomeUnidadeOrganizacional(unidade);
        cliente.setStatus(Status.ATIVO);
        cliente = clienteRepository.save(cliente);

        PlanoEntity plano = new PlanoEntity();
        plano.setNome("Plano Dev");
        plano.setStatus(Status.ATIVO);
        plano.setInicio(new Date());
        plano.setFim(Date.from(Instant.now().plus(365, ChronoUnit.DAYS)));
        plano.setCliente(cliente);
        planoRepository.save(plano);

        UsuarioEntity admin = new UsuarioEntity();
        admin.setNome(properties.bootstrap().adminNome());
        admin.setLogin(properties.bootstrap().adminLogin());
        admin.setSenha(passwordHasher.sha256Hex(properties.bootstrap().adminPassword()));
        admin.setStatus(Status.ATIVO);
        admin.setPerfil(PerfilAcesso.ADMINISTRADOR);
        admin.setAcessaWeb(true);
        admin.setCliente(cliente);
        usuarioRepository.save(admin);

        PedestreEntity pedestre = new PedestreEntity();
        pedestre.setNome(properties.bootstrap().appNome());
        pedestre.setLogin(properties.bootstrap().appLogin());
        pedestre.setSenha(passwordHasher.sha256Hex(properties.bootstrap().appPassword()));
        pedestre.setStatus(Status.ATIVO);
        pedestre.setPerfilApp(PerfilAcessoApp.GERENCIAL);
        pedestre.setCliente(cliente);
        pedestreRepository.save(pedestre);

        log.info("Bootstrap dev: unidade='{}' admin='{}' app='{}'", unidade,
                properties.bootstrap().adminLogin(), properties.bootstrap().appLogin());
    }
}
