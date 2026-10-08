package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;

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

        return new UsuarioResponse(
                domain.getId(),
                domain.getNome(),
                domain.getLogin(),
                domain.getEmail(),
                domain.getCpf(),
                domain.getRg(),
                domain.getTelefone(),
                domain.getCelular(),
                domain.getStatus(),
                domain.getPerfil(),
                domain.getAcessaWeb(),
                domain.getCadastroSimples(),
                domain.getUsuarioMaster(),
                domain.getExpedidora()
        );
    }
}
