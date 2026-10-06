package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.LoginBusinessException;
import br.com.startjob.acesso.core.usecase.login.AuditLogger;
import br.com.startjob.acesso.core.usecase.login.WebAppLoginUseCase;
import br.com.startjob.acesso.core.usecase.login.WebAppLoginUseCase.WebAppLoginResult;
import br.com.startjob.acesso.entrypoint.api.dto.WebLoginRequest;
import br.com.startjob.acesso.entrypoint.api.dto.WebLoginResponse;
import br.com.startjob.acesso.entrypoint.api.dto.WebUsuarioDto;
import br.com.startjob.acesso.entrypoint.api.security.jwt.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/login")
@Tag(name = "Web App Login", description = "Contrato usado pelo Web App para login")
public class WebAppLoginController {

    private final WebAppLoginUseCase webAppLoginUseCase;
    private final JwtService jwtService;
    private final AuditLogger auditLogger;

    static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PostMapping
    @Operation(summary = "Login da aplicação web (SPA)")
    public ResponseEntity<WebLoginResponse> login(
            @Valid @RequestBody WebLoginRequest request,
            HttpServletRequest httpServletRequest) {
        String ip = clientIp(httpServletRequest);
        String deviceType = "WEB SPA";

        WebAppLoginResult result;
        try {
            result = webAppLoginUseCase.login(
                    request.unidadeOrganizacional(),
                    request.usuario(),
                    request.senha(),
                    deviceType
            );
            auditLogger.loginSuccess("WEB_SPA", request.unidadeOrganizacional(), request.usuario(), result.usuario().getId(), ip);
        } catch (LoginBusinessException e) {
            auditLogger.loginFailure("WEB_SPA", request.unidadeOrganizacional(), request.usuario(), e.getMessageKey(), ip);
            throw e;
        }

        Usuario usuario = result.usuario();
        String clienteNome = usuario.getCliente() != null ? usuario.getCliente().getNome() : null;
        String perfil = usuario.getPerfil() != null ? usuario.getPerfil().name() : null;

        String token = jwtService.generate(usuario.getId(), clienteNome, perfil);

        WebUsuarioDto usuarioDto = new WebUsuarioDto(
                usuario.getId(),
                usuario.getNome(),
                perfil,
                result.permissoes()
        );

        WebLoginResponse response = new WebLoginResponse(token, "Bearer", usuarioDto);
        return ResponseEntity.ok(response);
    }
}
