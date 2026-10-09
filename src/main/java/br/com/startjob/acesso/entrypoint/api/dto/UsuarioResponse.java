package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

@Builder
public record UsuarioResponse(
        Long id,
        String nome,
        String login,
        String email,
        String cpf,
        String rg,
        String telefone,
        String celular,
        Status status,
        PerfilAcesso perfil,
        Boolean acessaWeb,
        Boolean cadastroSimples,
        Boolean usuarioMaster,
        Boolean expedidora
) {
    public static UsuarioResponse fromDomain(Usuario domain) {
        if (domain == null) {
            return null;
        }

        return UsuarioResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .login(domain.getLogin())
                .email(domain.getEmail())
                .cpf(domain.getCpf())
                .rg(domain.getRg())
                .telefone(domain.getTelefone())
                .celular(domain.getCelular())
                .status(domain.getStatus())
                .perfil(domain.getPerfil())
                .acessaWeb(domain.getAcessaWeb())
                .cadastroSimples(domain.getCadastroSimples())
                .usuarioMaster(domain.getUsuarioMaster())
                .expedidora(domain.getExpedidora())
                .build();
    }
}
