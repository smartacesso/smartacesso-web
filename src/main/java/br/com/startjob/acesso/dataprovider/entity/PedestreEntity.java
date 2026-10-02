package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "TB_PEDESTRE")
public class PedestreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID_PEDESTRE", nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL_APP")
    private PerfilAcessoApp perfilApp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

    public PedestreEntity(Long id, String login, String senha, PerfilAcessoApp perfilApp) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfilApp = perfilApp;
    }
}
