package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import br.com.startjob.acesso.core.usecase.permissao.ConsultaMatrizPermissaoWebUseCase;
import br.com.startjob.acesso.core.usecase.permissao.RestaurarMatrizPermissaoWebUseCase;
import br.com.startjob.acesso.core.usecase.permissao.SalvarMatrizPermissaoWebUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarWebPermissaoLinhaRequest;
import br.com.startjob.acesso.entrypoint.api.dto.WebPermissaoLinhaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/permissoes-web")
@Tag(name = "Web Permissao Controller", description = "Endpoints para gerenciamento da matriz de permissões web")
public class WebPermissaoController {

    private final ConsultaMatrizPermissaoWebUseCase consultaMatrizPermissaoWebUseCase;
    private final SalvarMatrizPermissaoWebUseCase salvarMatrizPermissaoWebUseCase;
    private final RestaurarMatrizPermissaoWebUseCase restaurarMatrizPermissaoWebUseCase;

    @Operation(summary = "Carrega a matriz de permissões web do cliente do usuário logado")
    @GetMapping
    public ResponseEntity<List<WebPermissaoLinhaResponse>> carregarMatriz() {
        log.info("Carregando matriz de permissões web");

        List<WebPermissaoLinhaResponse> response = consultaMatrizPermissaoWebUseCase.execute().stream()
                .map(WebPermissaoLinhaResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza a matriz de permissões web do cliente do usuário logado")
    @PutMapping
    public ResponseEntity<List<WebPermissaoLinhaResponse>> salvarMatriz(
            @Valid @RequestBody List<@Valid SalvarWebPermissaoLinhaRequest> request) {

        log.info("Salvando matriz de permissões web - total de linhas: {}", request != null ? request.size() : 0);

        List<WebPermissaoLinha> domainList = request == null
                ? Collections.emptyList()
                : request.stream()
                        .map(SalvarWebPermissaoLinhaRequest::toDomain)
                        .toList();

        List<WebPermissaoLinhaResponse> response = salvarMatrizPermissaoWebUseCase.execute(domainList).stream()
                .map(WebPermissaoLinhaResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Restaura a matriz de permissões web do cliente do usuário logado para o padrão do sistema")
    @PostMapping("/restaurar-padrao")
    public ResponseEntity<List<WebPermissaoLinhaResponse>> restaurarPadrao() {
        log.info("Restaurando matriz de permissões web para o padrão");

        List<WebPermissaoLinhaResponse> response = restaurarMatrizPermissaoWebUseCase.execute().stream()
                .map(WebPermissaoLinhaResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }
}
