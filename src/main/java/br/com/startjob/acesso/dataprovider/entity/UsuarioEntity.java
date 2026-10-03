package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableGenerator(name = "usuarioIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_USUARIO",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_USUARIO")
public class UsuarioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "usuarioIdGenerator")
    @Column(name = "ID_USUARIO", nullable = false)
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @Column(name = "LOGIN", length = 100)
    private String login;

    @JsonIgnore
    @Column(name = "SENHA")
    private String senha;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "CPF", length = 20)
    private String cpf;

    @Column(name = "RG", length = 20)
    private String rg;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "CELULAR", length = 20)
    private String celular;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDateTime dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", length = 20)
    private PerfilAcesso perfil;

    @Column(name = "TOKEN", length = 100)
    private String token;

    @Column(name = "ACESSA_WEB")
    private Boolean acessaWeb = true;

    @Column(name = "CADASTRO_SIMPLES")
    private Boolean cadastroSimples = true;

    @Column(name = "USUARIO_MASTER")
    private Boolean usuarioMaster = false;

    @Column(name = "HABILITA_EXPEDIDORA")
    private Boolean expedidora = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

}
