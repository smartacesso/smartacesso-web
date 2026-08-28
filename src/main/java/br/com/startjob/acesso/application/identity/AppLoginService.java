package br.com.startjob.acesso.application.identity;

import br.com.startjob.acesso.common.audit.AuditLogger;
import br.com.startjob.acesso.domain.entity.PedestreEntity;
import br.com.startjob.acesso.domain.repository.PedestreRepository;
import br.com.startjob.acesso.security.jwt.JwtService;
import br.com.startjob.acesso.security.password.PasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppLoginService {

    private final PedestreRepository pedestreRepository;
    private final PasswordHasher passwordHasher;
    private final JwtService jwtService;
    private final AuditLogger auditLogger;

    public AppLoginService(
            PedestreRepository pedestreRepository,
            PasswordHasher passwordHasher,
            JwtService jwtService,
            AuditLogger auditLogger
    ) {
        this.pedestreRepository = pedestreRepository;
        this.passwordHasher = passwordHasher;
        this.jwtService = jwtService;
        this.auditLogger = auditLogger;
    }

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

        PedestreEntity usuario = pedestreRepository.findByLoginOtimizado(loginNorm, unidade).orElse(null);
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

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record AppLoginResult(
            boolean success,
            int httpStatus,
            String message,
            String code,
            String token,
            Long userId,
            String nome,
            String cliente,
            String perfil
    ) {
        static AppLoginResult ok(String token, Long userId, String nome, String cliente, String perfil) {
            return new AppLoginResult(true, 200, null, null, token, userId, nome, cliente, perfil);
        }

        static AppLoginResult error(int status, String message, String code) {
            return new AppLoginResult(false, status, message, code, null, null, null, null, null);
        }
    }
}
