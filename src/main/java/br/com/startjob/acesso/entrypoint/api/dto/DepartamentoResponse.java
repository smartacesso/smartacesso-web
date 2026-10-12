package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

@Builder
public record DepartamentoResponse(
        Long id,
        String nome,
        Status status
) {
    public static DepartamentoResponse fromDomain(Departamento domain) {
        if (domain == null) {
            return null;
        }
        return DepartamentoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .build();
    }
}
