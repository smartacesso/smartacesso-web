package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String login,
        String email,
        String cpf,
        String rg,
        String telefone,
        String celular,
        LocalDateTime dataNascimento,
        Status status,
        PerfilAcesso perfil,
        Boolean acessaWeb,
        Boolean cadastroSimples,
        Boolean usuarioMaster,
        Boolean expedidora,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        EnderecoResponse endereco,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        ClienteResponse cliente
) {
    public static UsuarioResponse fromDomain(Usuario domain) {
        return fromDomain(domain, true, true);
    }

    public static UsuarioResponse fromDomainSemCliente(Usuario domain) {
        return fromDomain(domain, false, false);
    }

    public static UsuarioResponse fromDomain(Usuario domain, boolean incluirCliente) {
        return fromDomain(domain, incluirCliente, true);
    }

    private static UsuarioResponse fromDomain(
            Usuario domain,
            boolean incluirCliente,
            boolean incluirEndereco
    ) {
        if (domain == null) {
            return null;
        }

        return new UsuarioResponse(
                domain.getId(),
                domain.getNome(),
                domain.getLogin(),
                domain.getEmail(),
                domain.getCpf(),
                domain.getRg(),
                domain.getTelefone(),
                domain.getCelular(),
                domain.getDataNascimento(),
                domain.getStatus(),
                domain.getPerfil(),
                domain.getAcessaWeb(),
                domain.getCadastroSimples(),
                domain.getUsuarioMaster(),
                domain.getExpedidora(),
                incluirEndereco ? EnderecoResponse.fromDomain(domain.getEndereco()) : null,
                incluirCliente && domain.getCliente() != null
                        ? ClienteResponse.fromDomain(domain.getCliente())
                        : null
        );
    }
}
