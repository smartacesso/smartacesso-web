package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.endereco.Endereco;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record SalvarEnderecoRequest(
        Long id,
        @Size(max = 20, message = "CEP deve ter no máximo 20 caracteres")
        String cep,
        @Size(max = 255, message = "Logradouro deve ter no máximo 255 caracteres")
        String logradouro,
        @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
        String numero,
        @Size(max = 255, message = "Complemento deve ter no máximo 255 caracteres")
        String complemento,
        @Size(max = 100, message = "Bairro deve ter no máximo 100 caracteres")
        String bairro,
        @Size(max = 255, message = "Cidade deve ter no máximo 255 caracteres")
        String cidade,
        @Size(max = 100, message = "Estado deve ter no máximo 100 caracteres")
        String estado
) {
    public Endereco toDomain() {
        return Endereco.builder()
                .id(id)
                .cep(cep)
                .logradouro(logradouro)
                .numero(numero)
                .complemento(complemento)
                .bairro(bairro)
                .cidade(cidade)
                .estado(estado)
                .build();
    }
}
