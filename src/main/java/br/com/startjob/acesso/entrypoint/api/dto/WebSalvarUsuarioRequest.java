package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WebSalvarUsuarioRequest {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Status é obrigatório")
    private Status status;

    @NotBlank(message = "Login é obrigatório")
    private String login;

    private String senha;
    private String confirmarSenha;

    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    private String rg;
    private String telefone;
    private String celular;
    private LocalDateTime dataNascimento;

    @NotNull(message = "Perfil de acesso é obrigatório")
    private PerfilAcesso perfil;

    private Boolean acessaWeb;
    private Boolean cadastroSimples;

    @Valid
    private SalvarEnderecoRequest endereco;

    public Usuario toDomain() {
        return Usuario.builder()
                .id(this.id)
                .nome(this.nome)
                .status(this.status)
                .login(this.login)
                .senha(this.senha)
                .email(this.email)
                .cpf(this.cpf)
                .rg(this.rg)
                .telefone(this.telefone)
                .celular(this.celular)
                .dataNascimento(this.dataNascimento)
                .perfil(this.perfil)
                .acessaWeb(this.acessaWeb != null ? this.acessaWeb : true)
                .cadastroSimples(this.cadastroSimples != null ? this.cadastroSimples : true)
                .endereco(this.endereco != null ? this.endereco.toDomain() : null)
                .build();
    }
}
