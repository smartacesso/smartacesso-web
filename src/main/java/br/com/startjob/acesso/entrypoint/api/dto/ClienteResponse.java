package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.cliente.Cliente;
import lombok.Builder;

@Builder
public record ClienteResponse(
        Long id,
        String nome,
        String email,
        String cnpj,
        String telefone,
        String celular,
        String contato,
        String status,
        String nomeUnidadeOrganizacional
) {
    public static ClienteResponse fromDomain(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .email(cliente.getEmail())
                .cnpj(cliente.getCnpj())
                .telefone(cliente.getTelefone())
                .celular(cliente.getCelular())
                .contato(cliente.getContato())
                .status(cliente.getStatus() != null ? cliente.getStatus().name() : null)
                .nomeUnidadeOrganizacional(cliente.getNomeUnidadeOrganizacional())
                .build();
    }
}
