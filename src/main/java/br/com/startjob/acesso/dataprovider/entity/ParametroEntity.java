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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableGenerator(name = "parametroIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_PARAMETRO",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_PARAMETRO")
public class ParametroEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "parametroIdGenerator")
    @Column(name = "ID_PARAMETRO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Column(name = "VALOR", length = 255)
    private String valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;
}
