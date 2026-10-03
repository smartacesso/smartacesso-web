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

import java.time.LocalDateTime;

@Getter
@Setter
@TableGenerator(name = "acessoSistemaIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_ACESSO_SISTEMA",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_ACESSO_SISTEMA")
public class AcessoSistemaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "acessoSistemaIdGenerator")
    @Column(name = "ID_ACESSO_SISTEMA", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private UsuarioEntity usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE")
    private PedestreEntity pedestre;

    @Column(name = "DATA")
    private LocalDateTime data;

    @Column(name = "DISPOSITICO", length = 255)
    private String dispositivo;

    @Column(name = "TIPO", length = 100)
    private String tipo;
}
