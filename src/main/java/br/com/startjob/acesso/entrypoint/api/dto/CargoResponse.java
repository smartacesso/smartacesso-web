package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

@Builder
public record CargoResponse(
        Long id,
        String nome,
        Status status
) {
    public static CargoResponse fromDomain(Cargo domain) {
        if (domain == null) {
            return null;
        }
        return CargoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .build();
    }
}
