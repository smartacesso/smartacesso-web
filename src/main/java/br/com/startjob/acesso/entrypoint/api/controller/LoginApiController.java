package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.config.SmartAcessoProperties;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.login.LoginExtras;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.LoginBusinessException;
import br.com.startjob.acesso.core.usecase.login.AuditLogger;
import br.com.startjob.acesso.core.usecase.login.LoginUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.DesktopClienteResponse;
import br.com.startjob.acesso.entrypoint.api.dto.DesktopUserResponse;
import br.com.startjob.acesso.entrypoint.api.dto.ResponseServiceTO;
import br.com.startjob.acesso.entrypoint.api.security.legacy.LegacyDesktopTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/restful-services/login")
@Tag(name = "Login desktop", description = "Contrato legado usado pelo Swing e pelo Luxand app")
public class LoginApiController {

    private final LoginUseCase loginUseCase;
    private final LegacyDesktopTokenService tokenService;
    private final SmartAcessoProperties properties;
    private final AuditLogger auditLogger;

    static String deviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) {
            return "Desktop";
        }
        if (userAgent.contains("Apache-HttpClient/UNAVAILABLE")) {
            return "Android App";
        }
        if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ios")) {
            return "iPhone App";
        }
        return "Desktop";
    }

    static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @GetMapping(value = "/action", produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Health check do login desktop")
    public ResponseEntity<String> action() {
        return ResponseEntity.ok("working");
    }

    @GetMapping(value = "/do", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login desktop (senha em claro no query string — contrato Swing)")
    public ResponseEntity<ResponseServiceTO> login(
            @RequestParam("unidadeName") String unidade,
            @RequestParam("loginName") String login,
            @RequestParam("passwd") String passwd,
            HttpServletRequest request
    ) {
        String ip = clientIp(request);
        try {
            Usuario usuario = loginUseCase.authenticateDesktop(
                    unidade, login, passwd, false, LoginUseCase.ACCESS_API, deviceType(request));
            auditLogger.loginSuccess("DESKTOP", unidade, login, usuario.getId(), ip);
            return ResponseEntity.ok(ResponseServiceTO.ok(toResponse(usuario, true)));
        } catch (LoginBusinessException e) {
            auditLogger.loginFailure("DESKTOP", unidade, login, e.getMessageKey(), ip);
            return ResponseEntity.status(500).body(ResponseServiceTO.error("INTERNAL_SERVER_ERROR", e.getMessageKey()));
        }
    }

    @GetMapping(value = "/interno", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login interno (hash SHA-256 no query string — contrato legado)")
    public ResponseEntity<ResponseServiceTO> loginInterno(
            @RequestParam("unidadeName") String unidade,
            @RequestParam("loginName") String login,
            @RequestParam("passwd") String passwd,
            HttpServletRequest request
    ) {
        String ip = clientIp(request);
        try {
            Usuario usuario = loginUseCase.authenticateDesktop(
                    unidade, login, passwd, true, LoginUseCase.ACCESS_WEB, deviceType(request));
            auditLogger.loginSuccess("WEB_INTERNO", unidade, login, usuario.getId(), ip);
            return ResponseEntity.ok(ResponseServiceTO.ok(toResponse(usuario, false)));
        } catch (LoginBusinessException e) {
            auditLogger.loginFailure("WEB_INTERNO", unidade, login, e.getMessageKey(), ip);
            return ResponseEntity.status(500).body(ResponseServiceTO.error("INTERNAL_SERVER_ERROR", e.getMessageKey()));
        }
    }

    private DesktopUserResponse toResponse(Usuario usuario, boolean includePasswordHash) {
        LoginExtras extras = loginUseCase.loadExtras(usuario);
        String token = tokenService.issue(usuario.getId(), usuario.getSenha());

        DesktopUserResponse dto = new DesktopUserResponse();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setLogin(usuario.getLogin());
        dto.setStatus(usuario.getStatus() != null ? usuario.getStatus().name() : null);
        dto.setPerfil(usuario.getPerfil() != null ? usuario.getPerfil().name() : null);
        dto.setEmail(usuario.getEmail());
        dto.setAcessaWeb(usuario.getAcessaWeb());
        dto.setExpedidora(usuario.getExpedidora());
        dto.setCadastroSimples(usuario.getCadastroSimples());
        dto.setDataCriacao(usuario.getDataCriacao());
        dto.setToken(token);
        dto.setQtdePadraoDigitosCartao(extras.qtdePadraoDigitosCartao());
        dto.setChaveIntegracaoComtele(extras.chaveIntegracaoComtele());
        dto.setPermissoes(extras.permissoes());
        if (includePasswordHash && properties.security().includePasswordHashInDesktopLogin()) {
            dto.setSenha(usuario.getSenha());
        }

        Cliente cliente = usuario.getCliente();
        if (cliente != null) {
            DesktopClienteResponse clienteDto = new DesktopClienteResponse();
            clienteDto.setId(cliente.getId());
            clienteDto.setNome(cliente.getNome());
            clienteDto.setNomeUnidadeOrganizacional(cliente.getNomeUnidadeOrganizacional());
            clienteDto.setStatus(cliente.getStatus() != null ? cliente.getStatus().name() : null);
            clienteDto.setCnpj(cliente.getCnpj());
            clienteDto.setEmail(cliente.getEmail());
            dto.setCliente(clienteDto);
        }
        return dto;
    }
}
