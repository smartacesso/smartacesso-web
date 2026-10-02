package br.com.startjob.acesso.core.domain.usuario;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Usuario extends BaseDomain {

    private Long id;

    private String nome;

    private Status status;

    private String login;

    private String senha;

    private String email;

    private String cpf;

    private String rg;

    private String telefone;

    private String celular;

    private LocalDateTime dataNascimento;

    private PerfilAcesso perfil;

    private String token;

    private Boolean acessaWeb = true;

    private Boolean cadastroSimples = true;

    private Boolean usuarioMaster = false;

    private Boolean expedidora = false;

    private Cliente cliente;

    private String chaveIntegracaoComtele;

    private Integer qtdePadraoDigitosCartao;

    private Set<String> permissoes;

    public Boolean getAcessaWeb() {
        return acessaWeb == null ? Boolean.TRUE : acessaWeb;
    }
}
