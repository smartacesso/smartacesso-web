package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.parametro.AtualizarParametroInput;
import br.com.startjob.acesso.core.usecase.parametro.ConsultaParametrosUseCase;
import br.com.startjob.acesso.core.usecase.parametro.SalvarParametrosUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarParametroRequest;
import br.com.startjob.acesso.entrypoint.api.dto.SecaoParametroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/parametros")
@Tag(name = "Parametro Controller", description = "Endpoints para gerenciamento das configurações e parâmetros gerais do sistema")
public class ParametroController {

    private final ConsultaParametrosUseCase consultaParametrosUseCase;
    private final SalvarParametrosUseCase salvarParametrosUseCase;

    @Operation(summary = "Carrega os parâmetros gerais do cliente do usuário logado agrupados por seção")
    @GetMapping
    public ResponseEntity<List<SecaoParametroResponse>> carregarParametros() {
        log.info("Carregando parâmetros gerais do sistema");

        List<SecaoParametroResponse> response = consultaParametrosUseCase.execute().stream()
                .map(SecaoParametroResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Salva os parâmetros gerais do cliente do usuário logado")
    @PutMapping
    public ResponseEntity<List<SecaoParametroResponse>> salvarParametros(
            @Valid @RequestBody List<@Valid SalvarParametroRequest> request) {

        log.info("Salvando parâmetros gerais do sistema - total de itens: {}", request != null ? request.size() : 0);

        List<AtualizarParametroInput> domainList = request == null
                ? Collections.emptyList()
                : request.stream()
                        .map(SalvarParametroRequest::toDomain)
                        .toList();

        List<SecaoParametroResponse> response = salvarParametrosUseCase.execute(domainList).stream()
                .map(SecaoParametroResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(response);
    }
}
