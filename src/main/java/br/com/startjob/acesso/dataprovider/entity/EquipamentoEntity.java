package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@ToString(exclude = {"cliente", "usuario"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TB_EQUIPAMENTO")
public class EquipamentoEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "equipamento_gen")
    @TableGenerator(
            name = "equipamento_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "TB_EQUIPAMENTO",
            allocationSize = 50
    )
    @Column(name = "ID_EQUIPAMENTO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Column(name = "IP", length = 255)
    private String ip;

    @Column(name = "IDENTIFICADOR", length = 255)
    private String identificador;

    @Column(name = "MARCA", length = 255)
    private String marca;

    @Column(name = "MODELO", length = 255)
    private String modelo;

    @Column(name = "LOCAL", length = 255)
    private String local;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private UsuarioEntity usuario;
}
