package br.com.startjob.acesso.domain.entity;

import br.com.startjob.acesso.domain.enumeration.PerfilAcesso;
import br.com.startjob.acesso.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "TB_USUARIO")
public class UsuarioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @Column(name = "LOGIN", length = 100)
    private String login;

    @JsonIgnore
    @Column(name = "SENHA", length = 255)
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATA_NASCIMENTO")
    private Date dataNascimento;

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

    @Transient
    private String chaveIntegracaoComtele;

    @Transient
    private Integer qtdePadraoDigitosCartao;

    @Transient
    private Set<String> permissoes;

    public Boolean getAcessaWeb() {
        return acessaWeb == null ? Boolean.TRUE : acessaWeb;
    }
}
