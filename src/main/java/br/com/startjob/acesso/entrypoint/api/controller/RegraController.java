package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.usecase.regra.ConsultaRegraUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import br.com.startjob.acesso.entrypoint.api.dto.RegraResponse;
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
@RequestMapping("/api/v1/regras")
@Tag(name = "Regra Controller", description = "Endpoints para gerenciamento de regras de acesso")
public class RegraController {

    private final ConsultaRegraUseCase consultaRegraUseCase;

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
}
