package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.endereco.Endereco;
import lombok.Builder;

@Builder
public record EnderecoResponse(
        Long id,
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String estado
) {
    public static EnderecoResponse fromDomain(Endereco domain) {
        if (domain == null) {
            return null;
        }

        return EnderecoResponse.builder()
                .id(domain.getId())
                .cep(domain.getCep())
                .logradouro(domain.getLogradouro())
                .numero(domain.getNumero())
                .complemento(domain.getComplemento())
                .bairro(domain.getBairro())
                .cidade(domain.getCidade())
                .estado(domain.getEstado())
                .build();
    }
}
