package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SalvarCargoRequest(
        Long id,
        @NotBlank(message = "Nome do cargo é obrigatório")
        String nome,
        @NotNull(message = "Status do cargo é obrigatório")
        Status status,
        Boolean removido
) {
    public Cargo toDomain() {
        return Cargo.builder()
                .id(id)
                .nome(nome)
                .status(status)
                .removido(removido != null ? removido : false)
                .build();
    }
}
