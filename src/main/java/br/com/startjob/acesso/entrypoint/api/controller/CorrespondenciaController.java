package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.correspondencia.CorrespondenciaFiltro;
import br.com.startjob.acesso.core.usecase.correspondencia.ConfirmarRetiradaCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.ConsultaCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.ExcluirCorrespondenciaUseCase;
import br.com.startjob.acesso.core.usecase.correspondencia.SalvarCorrespondenciaUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.ConfirmarRetiradaCorrespondenciaRequest;
import br.com.startjob.acesso.entrypoint.api.dto.CorrespondenciaResponse;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarCorrespondenciaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/correspondencias")
@Tag(name = "Correspondência Controller", description = "Endpoints para gerenciamento de correspondências")
public class CorrespondenciaController {

    private final ConsultaCorrespondenciaUseCase consultaCorrespondenciaUseCase;
    private final SalvarCorrespondenciaUseCase salvarCorrespondenciaUseCase;
    private final ExcluirCorrespondenciaUseCase excluirCorrespondenciaUseCase;
    private final ConfirmarRetiradaCorrespondenciaUseCase confirmarRetiradaCorrespondenciaUseCase;

    @Operation(summary = "Lista correspondências paginadas com filtros opcionais")
    @GetMapping
    public ResponseEntity<PageResponse<CorrespondenciaResponse>> buscarCorrespondencias(
            @RequestParam(required = false) String nomeDestinatario,
            @RequestParam(required = false) String codigoRastreio,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String jaRetirado,
            @RequestParam(required = false) String confirmaRetirada,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataRecebimento,
            Pageable pageable) {

        String filtroRetirado = (jaRetirado != null && !jaRetirado.isBlank()) ? jaRetirado : confirmaRetirada;

        CorrespondenciaFiltro filtro = CorrespondenciaFiltro.builder()
                .nomeDestinatario(nomeDestinatario)
                .codigoRastreio(codigoRastreio)
                .tipo(tipo)
                .confirmaRetirada(filtroRetirado)
                .dataRecebimento(dataRecebimento)
                .build();

        log.info("Buscando correspondências paginadas. Filtros - nomeDestinatario: {}, codigoRastreio: {}, tipo: {}, confirmaRetirada: {}, dataRecebimento: {}",
                nomeDestinatario, codigoRastreio, tipo, filtroRetirado, dataRecebimento);

        PageResponse<CorrespondenciaResponse> page = PageResponse.from(
                consultaCorrespondenciaUseCase.execute(filtro, pageable)
                        .map(CorrespondenciaResponse::fromDomain));

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Busca uma correspondência pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<CorrespondenciaResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando correspondência - id: {}", id);

        var correspondencia = consultaCorrespondenciaUseCase.buscarPorId(id);

        return ResponseEntity.ok(CorrespondenciaResponse.fromDomain(correspondencia));
    }

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
