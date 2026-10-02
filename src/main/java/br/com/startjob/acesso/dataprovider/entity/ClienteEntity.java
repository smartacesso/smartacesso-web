package br.com.startjob.acesso.dataprovider.entity;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "TB_CLIENTE")
public class ClienteEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CLIENTE", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "CNPJ", length = 50)
    private String cnpj;

    @Column(name = "TELEFONE", length = 30)
    private String telefone;

    @Column(name = "CELULAR", length = 30)
    private String celular;

    @Column(name = "CONTATO", length = 255)
    private String contato;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 10)
    private Status status;

    @Column(name = "UNIDADE_ORGANIZACIONAL", length = 60)
    private String nomeUnidadeOrganizacional;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<PlanoEntity> planos = new ArrayList<>();
}
