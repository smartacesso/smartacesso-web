package br.com.startjob.acesso.core.usecase.login;

import br.com.startjob.acesso.core.domain.login.AppLoginResult;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.entrypoint.api.security.jwt.JwtService;
import br.com.startjob.acesso.entrypoint.api.security.password.PasswordHasher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RequiredArgsConstructor
@Component
public class AppLoginUseCase {

    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final AuditLogger auditLogger;
    private final PedestreGateway pedestreGateway;

    @Transactional(readOnly = true)
    public AppLoginResult login(String cliente, String login, String senha, String clientIp) {
        if (isBlank(cliente) || isBlank(login) || isBlank(senha)) {
            return AppLoginResult.error(400, "Parâmetros inválidos", "INVALID_PARAMS");
        }
        if (!jwtService.isConfigured()) {
            return AppLoginResult.error(500, "Servidor sem jwt.secret configurado", "JWT_NOT_CONFIGURED");
        }

        String unidade = cliente.trim().toLowerCase();
        String loginNorm = login.trim().toLowerCase();

        Pedestre usuario = pedestreGateway.findByLoginOtimizado(loginNorm, unidade).orElse(null);
        if (usuario == null) {
            auditLogger.loginFailure("APP", unidade, loginNorm, "INVALID_CREDENTIALS", clientIp);
            return AppLoginResult.error(401, "Usuário ou cliente inválido", "INVALID_CREDENTIALS");
        }
        if (!passwordHasher.matches(senha, usuario.getSenha())) {
            auditLogger.loginFailure("APP", unidade, loginNorm, "INVALID_PASSWORD", clientIp);
            return AppLoginResult.error(401, "Senha inválida", "INVALID_PASSWORD");
        }
        if (usuario.getPerfilApp() == null) {
            return AppLoginResult.error(400, "Sem perfil de usuario", "NO_APP_PROFILE");
        }

        String token = jwtService.generate(usuario.getId(), cliente.trim(), usuario.getPerfilApp().name());
        auditLogger.loginSuccess("APP", unidade, loginNorm, usuario.getId(), clientIp);

        String nome = usuario.getNome() != null && !usuario.getNome().trim().isEmpty()
                ? usuario.getNome().trim()
                : usuario.getLogin();
        return AppLoginResult.ok(token, usuario.getId(), nome, cliente.trim(), usuario.getPerfilApp().name());
    }

}
