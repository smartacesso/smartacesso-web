package br.com.startjob.acesso.core.usecase.login;

import br.com.startjob.acesso.core.domain.acessosistema.AcessoSistema;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.exception.LoginBusinessException;
import br.com.startjob.acesso.core.exception.LoginErrorCodes;
import br.com.startjob.acesso.core.gateway.AcessoSistemaGateway;
import br.com.startjob.acesso.core.gateway.ClienteGateway;
import br.com.startjob.acesso.core.gateway.PlanoGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
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
public class WebAppLoginUseCase {

    private final ClienteGateway clienteGateway;
    private final UsuarioGateway usuarioGateway;
    private final PlanoGateway planoGateway;
    private final AcessoSistemaGateway acessoSistemaGateway;
    private final PasswordHasher passwordHasher;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public WebAppLoginResult login(String unidade, String login, String senha, String deviceType) {
        if (clienteGateway.countByUnidadeAtiva(unidade) <= 0) {
            throw new LoginBusinessException(LoginErrorCodes.INVALID_CREDENTIALS);
        }

        List<Usuario> users = usuarioGateway.findByLoginAndUnidade(login, unidade);
        if (users == null || users.size() != 1) {
            throw new LoginBusinessException(LoginErrorCodes.INVALID_CREDENTIALS);
        }

        Usuario user = users.getFirst();
        if (!passwordHasher.matchesPreHashedOrRaw(senha, user.getSenha(), false)) {
            throw new LoginBusinessException(LoginErrorCodes.INVALID_PASSWORD);
        }

        validateAccount(user);
        registerAccess(user, deviceType);

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(user);

        return new WebAppLoginResult(user, permissoes);
    }

    private void validateAccount(Usuario user) {
        if (Status.INATIVO.equals(user.getStatus())) {
            throw new LoginBusinessException(LoginErrorCodes.INACTIVE_USER);
        }
        if (Boolean.FALSE.equals(user.getAcessaWeb())) {
            throw new LoginBusinessException(LoginErrorCodes.WEB_ACCESS_DENIED);
        }
        if (user.getCliente() != null) {
            var planos = planoGateway.findAtivos(user.getCliente().getId(), LocalDateTime.now(), PageRequest.of(0, 1));
            if (planos == null || planos.isEmpty()) {
                throw new LoginBusinessException(LoginErrorCodes.NO_ACTIVE_PLAN);
            }
        }
    }

    private void registerAccess(Usuario user, String deviceType) {
        AcessoSistema acesso = AcessoSistema.builder()
                .usuario(user)
                .data(LocalDateTime.now())
                .tipo("WEB_SPA")
                .dispositivo(deviceType)
                .build();

        acessoSistemaGateway.save(acesso);
    }

    public record WebAppLoginResult(Usuario usuario, Set<String> permissoes) {
    }
}
