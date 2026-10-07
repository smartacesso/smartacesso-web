package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.usecase.cliente.BuscarClientesUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.ClienteResponse;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/clientes")
@Tag(name = "Cliente Controller", description = "Endpoints para gerenciamento de clientes")
public class ClienteController {

    private final BuscarClientesUseCase buscarClientesUseCase;

    @Operation(summary = "Lista clientes paginados com filtro opcional por nome")
    @GetMapping
    public ResponseEntity<PageResponse<ClienteResponse>> buscarClientes(
            @RequestParam(required = false) String nome,
            Pageable pageable) {

        PageResponse<ClienteResponse> page = PageResponse.from(buscarClientesUseCase.execute(nome, pageable)
                .map(ClienteResponse::fromDomain));

        return ResponseEntity.ok(page);
    }
}
