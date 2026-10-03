package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableGenerator(name = "acessoIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_ACESSO",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_ACESSO")
public class AcessoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acessoIdGenerator")
    @Column(name = "ID_ACESSO", nullable = false, length = 4)
    private Long id;

    @ManyToOne(cascade = {}, fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PEDESTRE", nullable = true)
    private PedestreEntity pedestre;

    @Column(name = "DATA", nullable = true, length = 11)
    private LocalDateTime data;

    @Column(name = "SENTIDO", nullable = true, length = 100)
    private String sentido;

    @Column(name = "EQUIPAMENTO", nullable = true, length = 100)
    private String equipamento;

    @Column(name = "TIPO", nullable = true, length = 100)
    private String tipo;

    @Column(name = "LOCAL", nullable = true, length = 100)
    private String local;

    @Column(name = "RAZAO", nullable = true, length = 100)
    private String razao;

    @Column(name = "CARTAO_ACESSO_RECEBIDO", nullable = true, length = 100)
    private String cartaoAcessoRecebido;

    @Column(name = "IS_SINCRONIZADO", nullable = true)
    private Boolean isSincronizado;
    
}
