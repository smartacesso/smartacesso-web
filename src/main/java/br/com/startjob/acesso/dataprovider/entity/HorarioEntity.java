package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableGenerator(name = "horarioIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_HORARIO",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_HORARIO")
public class HorarioEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "horarioIdGenerator")
    @Column(name = "ID_HORARIO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGRA")
    private RegraEntity regra;

    @Column(name = "ID_PEDESTRE_REGRA")
    private Long idPedestreRegra;

    @Column(name = "DIAS_SEMANA", length = 100)
    private String diasSemana;

    @Column(name = "HORARIO_INI")
    private LocalDateTime horarioInicio;

    @Column(name = "HORARIO_FIM")
    private LocalDateTime horarioFim;

    @Column(name = "QTDE_CREDITOS")
    private Long qtdeDeCreditos;

    @Column(name = "ID_HORARIO_SENIOR")
    private Integer idHorarioSenior;
}
