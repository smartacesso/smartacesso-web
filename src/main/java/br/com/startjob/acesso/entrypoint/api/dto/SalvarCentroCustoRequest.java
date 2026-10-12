package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SalvarCentroCustoRequest(
        Long id,
        @NotBlank(message = "Nome do centro de custo é obrigatório")
        String nome,
        @NotNull(message = "Status do centro de custo é obrigatório")
        Status status,
        Boolean removido
) {
    public CentroCusto toDomain() {
        return CentroCusto.builder()
                .id(id)
                .nome(nome)
                .status(status)
                .removido(removido != null ? removido : false)
                .build();
    }
}
