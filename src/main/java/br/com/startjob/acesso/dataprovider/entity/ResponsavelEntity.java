package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.Status;
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
import jakarta.persistence.TableGenerator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString(exclude = {"password", "cliente"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TB_RESPONSIBLE")
public class ResponsavelEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "responsible_gen")
    @TableGenerator(
            name = "responsible_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "TB_RESPONSIBLE",
            allocationSize = 50
    )
    @Column(name = "ID_RESPONSIBLE", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 100)
    private String nome;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "CPF", length = 100)
    private String cpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @Column(name = "RG", length = 100)
    private String rg;

    @Column(name = "CELULAR", length = 100)
    private String celular;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column(name = "LOGIN", length = 100)
    private String login;

    @Column(name = "PASSWORD", length = 100)
    private String password;

    @Column(name = "TOKEN", length = 100)
    private String token;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "DEVICE_KEY", length = 255)
    private String deviceKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;
}
