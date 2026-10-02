package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.login.AppLoginResult;
import br.com.startjob.acesso.entrypoint.api.dto.AppHealthResponse;
import br.com.startjob.acesso.entrypoint.api.dto.AppLoginRequest;
import br.com.startjob.acesso.entrypoint.api.dto.AppLoginResponse;
import br.com.startjob.acesso.entrypoint.api.dto.AppUsuarioDto;
import br.com.startjob.acesso.core.usecase.login.AppLoginUseCase;
import br.com.startjob.acesso.entrypoint.api.security.jwt.JwtService;
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

    private final AppLoginUseCase appLoginUseCase;
    private final JwtService jwtService;

    public AppAuthController(AppLoginUseCase appLoginUseCase, JwtService jwtService) {
        this.appLoginUseCase = appLoginUseCase;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login do app (JWT Bearer)")
    public ResponseEntity<?> login(@RequestBody(required = false) AppLoginRequest request, HttpServletRequest http) {
        if (request == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Parâmetros inválidos", "code", "INVALID_PARAMS"));
        }

        AppLoginResult result = appLoginUseCase.login(
                request.cliente(), request.login(), request.senha(), http.getRemoteAddr());
        if (!result.success()) {
            return ResponseEntity.status(result.httpStatus()).body(
                    Map.of(
                            "error", result.message(),
                            "code", result.code()));
        }

        AppLoginResponse body = new AppLoginResponse(
                result.token(),
                "Bearer",
                new AppUsuarioDto(result.userId(), result.nome(), result.cliente(), result.perfil()));
        return ResponseEntity.ok(body);
    }

    @GetMapping("/health")
    @Operation(summary = "Status JWT/Firebase sem expor caminhos de arquivo")
    public AppHealthResponse health() {
        boolean jwtConfigured = jwtService.isConfigured();
        return AppHealthResponse.builder()
                .jwtConfigured(jwtConfigured)
                .firebasePathConfigured(false)
                .firebaseFileExists(false)
                .firebaseReady(false)
                .status(jwtConfigured ? "ok" : "degraded")
                .build();
    }
}
