package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.usecase.usuario.ConsultaUsuarioUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import br.com.startjob.acesso.entrypoint.api.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuario Controller", description = "Endpoints para gerenciamento de usuarios")
public class UsuarioController {

    private final ConsultaUsuarioUseCase consultaUsuarioUseCase;

    @Operation(summary = "Lista usuários paginados com filtro opcional por nome e cpf")
    @GetMapping
    public ResponseEntity<PageResponse<UsuarioResponse>> buscarUsuarios(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            Pageable pageable) {

        log.info("Buscando usuários paginados. Filtros - nome: {}, cpf: {}", nome, cpf);

        PageResponse<UsuarioResponse> page = PageResponse.from(
                consultaUsuarioUseCase.execute(nome, cpf, pageable)
                        .map(UsuarioResponse::fromDomain));

        return ResponseEntity.ok(page);
    }
}
