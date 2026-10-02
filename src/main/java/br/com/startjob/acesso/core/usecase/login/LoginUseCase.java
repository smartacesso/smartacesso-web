package br.com.startjob.acesso.core.usecase.login;

import br.com.startjob.acesso.core.domain.acessosistema.AcessoSistema;
import br.com.startjob.acesso.core.domain.login.LoginExtras;
import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.exception.LoginBusinessException;
import br.com.startjob.acesso.core.gateway.*;
import br.com.startjob.acesso.entrypoint.api.security.password.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class LoginUseCase {

    public static final String ACCESS_WEB = "WEB";
    public static final String ACCESS_API = "API";

    static final String PARAM_DIGITOS_CARTAO = "Escolher quantidade de digítos do cartão (até 10)";
    static final String PARAM_COMTELE = "Chave de integração Comtele";

    private final ClienteGateway clienteGateway;
    private final UsuarioGateway usuarioGateway;
    private final PlanoGateway planoGateway;
    private final AcessoSistemaGateway acessoSistemaGateway;
    private final ParametroGateway parametroGateway;
    private final PasswordHasher passwordHasher;
    private final WebPermissaoUseCase webPermissaoUseCase;

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

    @Transactional
    public Usuario authenticateDesktop(String unidade, String login, String presentedPassword,
                                       boolean presentedIsSha256, String accessType, String deviceType) {
        if (clienteGateway.countByUnidadeAtiva(unidade) <= 0) {
            throw new LoginBusinessException("msgs.account.unidade.nao.encontrada");
        }

        List<Usuario> users = usuarioGateway.findByLoginAndUnidade(login, unidade);
        if (users == null || users.size() != 1) {
            throw new LoginBusinessException("msgs.account.usuario.nao.encontrado");
        }

        Usuario user = users.getFirst();
        if (!passwordHasher.matchesPreHashedOrRaw(presentedPassword, user.getSenha(), presentedIsSha256)) {
            throw new LoginBusinessException("msgs.account.usuario.senha.invalida");
        }

        validateAccount(user, accessType);
        registerAccess(user, accessType, deviceType);
        return user;
    }

    @Transactional(readOnly = true)
    public LoginExtras loadExtras(Usuario user) {
        Long clienteId = user.getCliente() != null ? user.getCliente().getId() : null;
        Integer digitos = null;
        String comtele = null;
        if (clienteId != null) {
            digitos = parametroGateway.findByClienteAndNome(clienteId, PARAM_DIGITOS_CARTAO)
                    .map(p -> parseInt(p.getValor()))
                    .orElse(null);
            comtele = parametroGateway.findByClienteAndNome(clienteId, PARAM_COMTELE)
                    .map(Parametro::getValor)
                    .orElse(null);
        }
        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(user);
        return new LoginExtras(digitos, comtele, permissoes);
    }

    private void validateAccount(Usuario user, String accessType) {
        if (Status.INATIVO.equals(user.getStatus())) {
            throw new LoginBusinessException("msgs.account.usuario.nao.encontrado");
        }
        if (ACCESS_WEB.equals(accessType) && Boolean.FALSE.equals(user.getAcessaWeb())) {
            throw new LoginBusinessException("msgs.account.usuario.nao.permitido.web");
        }
        if (user.getCliente() != null) {
            var planos = planoGateway.findAtivos(user.getCliente().getId(), LocalDateTime.now(), PageRequest.of(0, 1));
            if (planos == null || planos.isEmpty()) {
                throw new LoginBusinessException("msgs.account.usuario.nenhum.plano.ativo");
            }
        }
    }

    private void registerAccess(Usuario user, String accessType, String deviceType) {
        AcessoSistema acesso = AcessoSistema.builder()
                .usuario(user)
                .data(LocalDateTime.now())
                .tipo(accessType)
                .dispositivo(deviceType)
                .build();

        acessoSistemaGateway.save(acesso);
    }

}
