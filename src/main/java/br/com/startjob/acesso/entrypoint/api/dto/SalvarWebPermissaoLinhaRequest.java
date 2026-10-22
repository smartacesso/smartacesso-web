package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SalvarWebPermissaoLinhaRequest(
        @NotBlank(message = "Código da permissão é obrigatório")
        String codigo,
        boolean administrador,
        boolean gerente,
        boolean operador,
        boolean porteiro,
        boolean cuidador,
        boolean responsavel
) {
    public WebPermissaoLinha toDomain() {
        return WebPermissaoLinha.builder()
                .codigo(codigo)
                .administrador(administrador)
                .gerente(gerente)
                .operador(operador)
                .porteiro(porteiro)
                .cuidador(cuidador)
                .responsavel(responsavel)
                .build();
    }
}
