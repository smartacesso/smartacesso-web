package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SalvarDepartamentoRequest(
        Long id,
        @NotBlank(message = "Nome do departamento é obrigatório")
        String nome,
        @NotNull(message = "Status do departamento é obrigatório")
        Status status,
        Boolean removido
) {
    public Departamento toDomain() {
        return Departamento.builder()
                .id(id)
                .nome(nome)
                .status(status)
                .removido(removido != null ? removido : false)
                .build();
    }
}
