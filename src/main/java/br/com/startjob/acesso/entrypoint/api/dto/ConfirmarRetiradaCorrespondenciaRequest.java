package br.com.startjob.acesso.entrypoint.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ConfirmarRetiradaCorrespondenciaRequest(
        @JsonAlias({"nomeRetirada", "nomeQuemRetirou"})
        @NotBlank(message = "Informe o nome de quem está retirando.")
        @Size(max = 150, message = "Nome de quem retirou deve ter no máximo 150 caracteres")
        String nomeQuemRetirou,

        @JsonAlias({"documentoRetirada", "documentoQuemRetirou"})
        @NotBlank(message = "Informe o documento de quem está retirando.")
        @Size(max = 50, message = "Documento de quem retirou deve ter no máximo 50 caracteres")
        String documentoQuemRetirou
) {
}
