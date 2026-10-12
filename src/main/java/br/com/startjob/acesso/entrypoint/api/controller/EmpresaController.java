package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.usecase.empresa.ConsultaEmpresaUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.EmpresaResponse;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
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
@RequestMapping("/api/v1/empresas")
@Tag(name = "Empresa Controller", description = "Endpoints para gerenciamento de empresas")
public class EmpresaController {

    private final ConsultaEmpresaUseCase consultaEmpresaUseCase;

    @Operation(summary = "Lista empresas paginadas com departamentos, centro de custo e cargo, com filtro opcional por nome e cnpj")
    @GetMapping
    public ResponseEntity<PageResponse<EmpresaResponse>> buscarEmpresas(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cnpj,
            Pageable pageable) {

        log.info("Buscando empresas paginadas. Filtros - nome: {}, cnpj: {}", nome, cnpj);

        PageResponse<EmpresaResponse> page = PageResponse.from(
                consultaEmpresaUseCase.execute(nome, cnpj, pageable)
                        .map(EmpresaResponse::fromDomain));

        return ResponseEntity.ok(page);
    }
}
