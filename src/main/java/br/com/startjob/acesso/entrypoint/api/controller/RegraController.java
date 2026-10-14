package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.usecase.regra.ConsultaRegraUseCase;
import br.com.startjob.acesso.core.usecase.regra.ExcluirRegraUseCase;
import br.com.startjob.acesso.core.usecase.regra.SalvarRegraUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import br.com.startjob.acesso.entrypoint.api.dto.RegraResponse;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarRegraRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/regras")
@Tag(name = "Regra Controller", description = "Endpoints para gerenciamento de regras de acesso")
public class RegraController {

    private final ConsultaRegraUseCase consultaRegraUseCase;
    private final SalvarRegraUseCase salvarRegraUseCase;
    private final ExcluirRegraUseCase excluirRegraUseCase;

    @Operation(summary = "Lista regras de acesso paginadas com filtros por nome, empresa vinculada, tipo pedestre e tipo de regra")
    @GetMapping
    public ResponseEntity<PageResponse<RegraResponse>> buscarRegras(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String empresa,
            @RequestParam(required = false) Long idEmpresa,
            @RequestParam(required = false) String nomeEmpresa,
            @RequestParam(required = false) TipoPedestre tipoPedestre,
            @RequestParam(required = false) TipoRegra tipo,
            @RequestParam(required = false) TipoRegra tipoRegra,
            Pageable pageable) {

        TipoRegra tipoFinal = tipo != null ? tipo : tipoRegra;
        String nomeEmpresaFinal = nomeEmpresa != null ? nomeEmpresa : empresa;

        log.info("Buscando regras paginadas. Filtros - nome: {}, idEmpresa: {}, empresa: {}, tipoPedestre: {}, tipo: {}",
                nome, idEmpresa, nomeEmpresaFinal, tipoPedestre, tipoFinal);

        PageResponse<RegraResponse> page = PageResponse.from(
                consultaRegraUseCase.execute(nome, idEmpresa, nomeEmpresaFinal, tipoPedestre, tipoFinal, pageable)
                        .map(RegraResponse::fromDomain));

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Busca uma regra de acesso pelo identificador")
    @GetMapping("/{id}")
    public ResponseEntity<RegraResponse> buscarPorId(@PathVariable Long id) {
        log.info("Buscando regra de acesso - id: {}", id);

        var regra = consultaRegraUseCase.buscarPorId(id);

        return ResponseEntity.ok(RegraResponse.fromDomain(regra));
    }

    @Operation(summary = "Cria ou edita uma regra de acesso")
    @PostMapping
    public ResponseEntity<RegraResponse> salvarRegra(
            @Valid @RequestBody SalvarRegraRequest request) {

        log.info("Salvando regra de acesso - id: {}, nome: {}", request.id(), request.nome());

        var regraSalva = salvarRegraUseCase.execute(request.toDomain());

        return ResponseEntity.ok(RegraResponse.fromDomain(regraSalva));
    }

    @Operation(summary = "Exclui uma regra de acesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirRegra(@PathVariable Long id) {
        log.info("Excluindo regra de acesso - id: {}", id);

        excluirRegraUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}
