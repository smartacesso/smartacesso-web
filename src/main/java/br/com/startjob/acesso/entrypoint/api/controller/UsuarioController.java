package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.usecase.usuario.AlterarLoginSenhaUsuarioUseCase;
import br.com.startjob.acesso.core.usecase.usuario.ConsultaUsuarioUseCase;
import br.com.startjob.acesso.core.usecase.usuario.ExcluirUsuarioUseCase;
import br.com.startjob.acesso.core.usecase.usuario.SalvarUsuarioUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.AlterarLoginSenhaRequest;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import br.com.startjob.acesso.entrypoint.api.dto.UsuarioResponse;
import br.com.startjob.acesso.entrypoint.api.dto.WebSalvarUsuarioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuario Controller", description = "Endpoints para gerenciamento de usuarios")
public class UsuarioController {

    private final ConsultaUsuarioUseCase consultaUsuarioUseCase;
    private final SalvarUsuarioUseCase salvarUsuarioUseCase;
    private final ExcluirUsuarioUseCase excluirUsuarioUseCase;
    private final AlterarLoginSenhaUsuarioUseCase alterarLoginSenhaUsuarioUseCase;

    @Operation(summary = "Lista usuários paginados com filtro opcional por nome e cpf")
    @GetMapping
    public ResponseEntity<PageResponse<UsuarioResponse>> buscarUsuarios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            Pageable pageable) {

        log.info("Buscando usuários paginados. Filtros - nome: {}, cpf: {}", nome, cpf);

        PageResponse<UsuarioResponse> page = PageResponse.from(
                consultaUsuarioUseCase.execute(nome, cpf, pageable)
                        .map(UsuarioResponse::fromDomainSemCliente));

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Busca um usuário pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando usuário - id: {}", id);

        var usuario = consultaUsuarioUseCase.buscarPorId(id);

        return ResponseEntity.ok(UsuarioResponse.fromDomain(usuario));
    }

    @Operation(summary = "Cria ou edita um usuário")
    @PostMapping
    public ResponseEntity<UsuarioResponse> salvarUsuario(
            @Valid @RequestBody WebSalvarUsuarioRequest request) {

        log.info("Salvando usuário - id: {}, login: {}", request.getId(), request.getLogin());

        var usuarioSalvo = salvarUsuarioUseCase.execute(request.toDomain(), request.getConfirmarSenha());

        return ResponseEntity.ok(UsuarioResponse.fromDomain(usuarioSalvo));
    }

    @Operation(summary = "Exclui um usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirUsuario(@PathVariable Long id) {
        log.info("Excluindo usuário - id: {}", id);

        excluirUsuarioUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Altera o login e a senha de um usuário")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{id}/login-senha")
    public ResponseEntity<UsuarioResponse> alterarLoginSenha(
            @PathVariable Long id,
            @Valid @RequestBody AlterarLoginSenhaRequest request) {

        log.info("Alterando login e senha do usuário - id: {}, login: {}", id, request.login());

        Usuario usuarioAtualizado = alterarLoginSenhaUsuarioUseCase.execute(
                id,
                request.login(),
                request.senha(),
                request.confirmacaoSenha()
        );

        return ResponseEntity.ok(UsuarioResponse.fromDomain(usuarioAtualizado));
    }
}
