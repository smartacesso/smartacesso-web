package br.com.startjob.acesso.api.app;

import br.com.startjob.acesso.application.identity.AppLoginService;
import br.com.startjob.acesso.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/restful-services/app")
@Tag(name = "App mobile", description = "API JWT do aplicativo")
public class AppAuthController {

    private final AppLoginService appLoginService;
    private final JwtService jwtService;

    public AppAuthController(AppLoginService appLoginService, JwtService jwtService) {
        this.appLoginService = appLoginService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login do app (JWT Bearer)")
    public ResponseEntity<?> login(@RequestBody(required = false) AppLoginRequest request, HttpServletRequest http) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Parâmetros inválidos", "code", "INVALID_PARAMS"));
        }
        AppLoginService.AppLoginResult result = appLoginService.login(
                request.cliente(), request.login(), request.senha(), http.getRemoteAddr());
        if (!result.success()) {
            return ResponseEntity.status(result.httpStatus()).body(
                    Map.of(
                            "error", result.message(),
                            "code", result.code()
                    ));
        }
        AppLoginResponse body = new AppLoginResponse(
                result.token(),
                "Bearer",
                new AppUsuarioDto(result.userId(), result.nome(), result.cliente(), result.perfil())
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/health")
    @Operation(summary = "Status JWT/Firebase sem expor caminhos de arquivo")
    public AppHealthResponse health() {
        AppHealthResponse health = new AppHealthResponse();
        health.setJwtConfigured(jwtService.isConfigured());
        health.setFirebasePathConfigured(false);
        health.setFirebaseFileExists(false);
        health.setFirebaseReady(false);
        health.setStatus(health.isJwtConfigured() ? "ok" : "degraded");
        return health;
    }
}
