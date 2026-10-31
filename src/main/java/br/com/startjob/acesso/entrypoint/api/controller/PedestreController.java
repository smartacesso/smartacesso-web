package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.pedestre.PedestreFiltro;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.usecase.pedestre.ConsultaPedestreUseCase;
import br.com.startjob.acesso.core.usecase.pedestre.ExcluirPedestreUseCase;
import br.com.startjob.acesso.core.usecase.pedestre.SalvarPedestreUseCase;
import br.com.startjob.acesso.entrypoint.api.dto.PageResponse;
import br.com.startjob.acesso.entrypoint.api.dto.PedestreResponse;
import br.com.startjob.acesso.entrypoint.api.dto.PedestreResumoResponse;
import br.com.startjob.acesso.entrypoint.api.dto.SalvarPedestreRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/pedestres")
@Tag(name = "Pedestre Controller", description = "Endpoints para gerenciamento de pedestres e visitantes")
public class PedestreController {

    private final ConsultaPedestreUseCase consultaPedestreUseCase;
    private final SalvarPedestreUseCase salvarPedestreUseCase;
    private final ExcluirPedestreUseCase excluirPedestreUseCase;

    @Operation(summary = "Lista pedestres e visitantes de forma paginada e otimizada com múltiplos filtros")
    @GetMapping
    public ResponseEntity<PageResponse<PedestreResumoResponse>> buscarPedestres(
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String codigoCartaoAcesso,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String contato,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String rg,
            @RequestParam(required = false) TipoPedestre tipo,
            @RequestParam(required = false) Long idEmpresa,
            @RequestParam(required = false) Long idDepartamento,
            @RequestParam(required = false) Long idCentroCusto,
            @RequestParam(required = false) Long idCargo,
            @RequestParam(required = false) String uuidLocal,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Boolean possuiFoto,
            Pageable pageable) {

        PedestreFiltro filtro = PedestreFiltro.builder()
                .matricula(matricula)
                .codigoCartaoAcesso(codigoCartaoAcesso)
                .nome(nome)
                .contato(contato)
                .cpf(cpf)
                .rg(rg)
                .tipo(tipo)
                .idEmpresa(idEmpresa)
                .idDepartamento(idDepartamento)
                .idCentroCusto(idCentroCusto)
                .idCargo(idCargo)
                .uuidLocal(uuidLocal)
                .status(status)
                .possuiFoto(possuiFoto)
                .build();

        log.info("Buscando pedestres paginados. Filtros - nome: {}, matricula: {}, cpf: {}, tipo: {}, idEmpresa: {}, status: {}",
                nome, matricula, cpf, tipo, idEmpresa, status);

        PageResponse<PedestreResumoResponse> page = PageResponse.from(
                consultaPedestreUseCase.execute(filtro, pageable)
                        .map(PedestreResumoResponse::fromDomain));

        return ResponseEntity.ok(page);
    }

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

    @Operation(summary = "Exclui um pedestre ou visitante")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirPedestre(@PathVariable Long id) {
        log.info("Excluindo pedestre/visitante - id: {}", id);

        excluirPedestreUseCase.execute(id);

        return ResponseEntity.noContent().build();
    }
}
