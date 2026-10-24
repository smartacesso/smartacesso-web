package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.parametro.AtualizarParametroInput;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record SalvarParametroRequest(
        @NotBlank(message = "Código do parâmetro é obrigatório")
        String codigo,
        @NotNull(message = "Valor do parâmetro é obrigatório")
        Object valor
) {
    public AtualizarParametroInput toDomain() {
        return AtualizarParametroInput.builder()
                .codigo(codigo)
                .valor(valor)
                .build();
    }
}
