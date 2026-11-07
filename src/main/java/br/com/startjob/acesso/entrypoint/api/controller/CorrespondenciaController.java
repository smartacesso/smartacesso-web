package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.usecase.correspondencia.ConfirmarRetiradaCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.ExcluirCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.SalvarCorrespondenciaUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.ConfirmarRetiradaCorrespondenciaRequest;
import br.com.startjob.acesso.entrypoint.api.dto.CorrespondenciaResponse;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarCorrespondenciaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/correspondencias")
@Tag(name = "Correspondência Controller", description = "Endpoints para gerenciamento de correspondências")
public class CorrespondenciaController {

    private final SalvarCorrespondenciaUseCase salvarCorrespondenciaUseCase;
    private final ExcluirCorrespondenciaUseCase excluirCorrespondenciaUseCase;
    private final ConfirmarRetiradaCorrespondenciaUseCase confirmarRetiradaCorrespondenciaUseCase;

    @Operation(summary = "Cria ou edita uma correspondência")
    @PostMapping
    public ResponseEntity<CorrespondenciaResponse> salvarCorrespondencia(
            @Valid @RequestBody SalvarCorrespondenciaRequest request) {

        log.info("Salvando correspondência - id: {}, idDestinatario: {}, tipo: {}",
                request.id(), request.idDestinatario(), request.tipo());

        boolean removerFoto = Boolean.TRUE.equals(request.removerFoto());
        var salva = salvarCorrespondenciaUseCase.execute(request.toDomain(), removerFoto);

        return ResponseEntity.ok(CorrespondenciaResponse.fromDomain(salva));
    }

    @Operation(summary = "Confirma a retirada de uma correspondência informando nome e documento de quem retirou")
    @PostMapping("/{id}/confirmar-retirada")
    public ResponseEntity<CorrespondenciaResponse> confirmarRetirada(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmarRetiradaCorrespondenciaRequest request) {

        log.info("Confirmando retirada de correspondência - id: {}, nomeQuemRetirou: {}",
                id, request.nomeQuemRetirou());

        var correspondencia = confirmarRetiradaCorrespondenciaUseCase.execute(
                id,
                request.nomeQuemRetirou(),
                request.documentoQuemRetirou()
        );

        return ResponseEntity.ok(CorrespondenciaResponse.fromDomain(correspondencia));
    }

    @Operation(summary = "Exclui uma correspondência")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirCorrespondencia(@PathVariable Long id) {
        log.info("Excluindo correspondência - id: {}", id);

        excluirCorrespondenciaUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}
