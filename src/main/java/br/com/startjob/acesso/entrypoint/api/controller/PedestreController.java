package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.usecase.pedestre.ConsultaPedestreUseCase;
import br.com.startjob.acesso.core.usecase.pedestre.SalvarPedestreUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.PedestreResponse;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarPedestreRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pedestres")
@Tag(name = "Pedestre Controller", description = "Endpoints para gerenciamento de pedestres e visitantes")
public class PedestreController {

    private final ConsultaPedestreUseCase consultaPedestreUseCase;
    private final SalvarPedestreUseCase salvarPedestreUseCase;

    @Operation(summary = "Busca um pedestre ou visitante pelo identificador para edição")
    @GetMapping("/{id}")
    public ResponseEntity<PedestreResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando pedestre/visitante - id: {}", id);

        var pedestre = consultaPedestreUseCase.buscarPorId(id);

        return ResponseEntity.ok(PedestreResponse.fromDomain(pedestre));
    }

    @Operation(summary = "Cria ou edita um pedestre ou visitante")
    @PostMapping
    public ResponseEntity<PedestreResponse> salvarPedestre(
            @Valid @RequestBody SalvarPedestreRequest request) {

        log.info("Salvando pedestre/visitante - id: {}, tipo: {}, nome: {}",
                request.id(), request.tipo(), request.nome());

        boolean removerFoto = Boolean.TRUE.equals(request.removerFoto());
        var salvo = salvarPedestreUseCase.execute(request.toDomain(), removerFoto);

        return ResponseEntity.ok(PedestreResponse.fromDomain(salvo));
    }
}
