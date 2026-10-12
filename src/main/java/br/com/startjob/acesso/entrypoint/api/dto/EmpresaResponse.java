package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

import java.util.Collections;
import java.util.List;

@Builder
public record EmpresaResponse(
        Long id,
        String nome,
        String cnpj,
        String email,
        String telefone,
        String celular,
        Status status,
        String codigoExterno,
        String codEmpresaSenior,
        Boolean autoAtendimentoLiberado,
        List<DepartamentoResponse> departamentos,
        List<CentroCustoResponse> centros,
        List<CargoResponse> cargos
) {
    public static EmpresaResponse fromDomain(Empresa domain) {
        if (domain == null) {
            return null;
        }

        List<DepartamentoResponse> departamentos = domain.getDepartamentos() == null
                ? Collections.emptyList()
                : domain.getDepartamentos().stream()
                        .filter(d -> d.getRemovido() == null || !d.getRemovido())
                        .map(DepartamentoResponse::fromDomain)
                        .toList();

        List<CentroCustoResponse> centros = domain.getCentros() == null
                ? Collections.emptyList()
                : domain.getCentros().stream()
                        .filter(c -> c.getRemovido() == null || !c.getRemovido())
                        .map(CentroCustoResponse::fromDomain)
                        .toList();

        List<CargoResponse> cargos = domain.getCargos() == null
                ? Collections.emptyList()
                : domain.getCargos().stream()
                        .filter(c -> c.getRemovido() == null || !c.getRemovido())
                        .map(CargoResponse::fromDomain)
                        .toList();

        return EmpresaResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .cnpj(domain.getCnpj())
                .email(domain.getEmail())
                .telefone(domain.getTelefone())
                .celular(domain.getCelular())
                .status(domain.getStatus())
                .codigoExterno(domain.getCodigoExterno())
                .codEmpresaSenior(domain.getCodEmpresaSenior())
                .autoAtendimentoLiberado(domain.getAutoAtendimentoLiberado())
                .departamentos(departamentos)
                .centros(centros)
                .cargos(cargos)
                .build();
    }
}
