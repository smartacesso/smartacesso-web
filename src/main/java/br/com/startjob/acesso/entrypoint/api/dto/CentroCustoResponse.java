package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

@Builder
public record CentroCustoResponse(
        Long id,
        String nome,
        Status status
) {
    public static CentroCustoResponse fromDomain(CentroCusto domain) {
        if (domain == null) {
            return null;
        }
        return CentroCustoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .build();
    }
}
