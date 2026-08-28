package br.com.startjob.acesso.application.identity;

import br.com.startjob.acesso.common.exception.LoginBusinessException;
import br.com.startjob.acesso.domain.entity.AcessoSistemaEntity;
import br.com.startjob.acesso.domain.entity.UsuarioEntity;
import br.com.startjob.acesso.domain.enumeration.Status;
import br.com.startjob.acesso.domain.repository.AcessoSistemaRepository;
import br.com.startjob.acesso.domain.repository.ClienteRepository;
import br.com.startjob.acesso.domain.repository.ParametroRepository;
import br.com.startjob.acesso.domain.repository.PlanoRepository;
import br.com.startjob.acesso.domain.repository.UsuarioRepository;
import br.com.startjob.acesso.security.password.PasswordHasher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class LoginService {

    public static final String ACCESS_WEB = "WEB";
    public static final String ACCESS_API = "API";

    static final String PARAM_DIGITOS_CARTAO = "Escolher quantidade de digítos do cartão (até 10)";
    static final String PARAM_COMTELE = "Chave de integração Comtele";

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanoRepository planoRepository;
    private final AcessoSistemaRepository acessoSistemaRepository;
    private final ParametroRepository parametroRepository;
    private final PasswordHasher passwordHasher;
    private final WebPermissaoService webPermissaoService;

    public LoginService(
            ClienteRepository clienteRepository,
            UsuarioRepository usuarioRepository,
            PlanoRepository planoRepository,
            AcessoSistemaRepository acessoSistemaRepository,
            ParametroRepository parametroRepository,
            PasswordHasher passwordHasher,
            WebPermissaoService webPermissaoService
    ) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.planoRepository = planoRepository;
        this.acessoSistemaRepository = acessoSistemaRepository;
        this.parametroRepository = parametroRepository;
        this.passwordHasher = passwordHasher;
        this.webPermissaoService = webPermissaoService;
    }

    @Transactional
    public UsuarioEntity authenticateDesktop(String unidade, String login, String presentedPassword,
                                             boolean presentedIsSha256, String accessType, String deviceType) {
        if (clienteRepository.countByUnidadeAtiva(unidade) <= 0) {
            throw new LoginBusinessException("msgs.account.unidade.nao.encontrada");
        }

        List<UsuarioEntity> users = usuarioRepository.findByLoginAndUnidade(login, unidade);
        if (users == null || users.size() != 1) {
            throw new LoginBusinessException("msgs.account.usuario.nao.encontrado");
        }

        UsuarioEntity user = users.get(0);
        if (!passwordHasher.matchesPreHashedOrRaw(presentedPassword, user.getSenha(), presentedIsSha256)) {
            throw new LoginBusinessException("msgs.account.usuario.senha.invalida");
        }

        validateAccount(user, accessType);
        registerAccess(user, accessType, deviceType);
        return user;
    }

    @Transactional(readOnly = true)
    public LoginExtras loadExtras(UsuarioEntity user) {
        Long clienteId = user.getCliente() != null ? user.getCliente().getId() : null;
        Integer digitos = null;
        String comtele = null;
        if (clienteId != null) {
            digitos = parametroRepository.findByClienteAndNome(clienteId, PARAM_DIGITOS_CARTAO)
                    .map(p -> parseInt(p.getValor()))
                    .orElse(null);
            comtele = parametroRepository.findByClienteAndNome(clienteId, PARAM_COMTELE)
                    .map(p -> p.getValor())
                    .orElse(null);
        }
        Set<String> permissoes = webPermissaoService.resolverPermissoesWeb(user);
        return new LoginExtras(digitos, comtele, permissoes);
    }

    private void validateAccount(UsuarioEntity user, String accessType) {
        if (Status.INATIVO.equals(user.getStatus())) {
            throw new LoginBusinessException("msgs.account.usuario.nao.encontrado");
        }
        if (ACCESS_WEB.equals(accessType) && Boolean.FALSE.equals(user.getAcessaWeb())) {
            throw new LoginBusinessException("msgs.account.usuario.nao.permitido.web");
        }
        if (user.getCliente() != null) {
            var planos = planoRepository.findAtivos(user.getCliente().getId(), new Date(), PageRequest.of(0, 1));
            if (planos == null || planos.isEmpty()) {
                throw new LoginBusinessException("msgs.account.usuario.nenhum.plano.ativo");
            }
        }
    }

    private void registerAccess(UsuarioEntity user, String accessType, String deviceType) {
        AcessoSistemaEntity acesso = new AcessoSistemaEntity();
        acesso.setUsuario(user);
        acesso.setData(new Date());
        acesso.setTipo(accessType);
        acesso.setDispositivo(deviceType);
        acessoSistemaRepository.save(acesso);
    }

    private static Integer parseInt(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public record LoginExtras(Integer qtdePadraoDigitosCartao, String chaveIntegracaoComtele, Set<String> permissoes) {
    }
}
