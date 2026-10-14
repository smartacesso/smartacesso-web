package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoEscala;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@TableGenerator(name = "regraIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_REGRA",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_REGRA")
public class RegraEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "regraIdGenerator")
    @Column(name = "ID_REGRA", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @Column(name = "VALIDADE")
    private LocalDate validade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPRESA")
    private EmpresaEntity empresa;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", length = 100)
    private TipoRegra tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PEDESTRE", length = 100)
    private TipoPedestre tipoPedestre;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    @Column(name = "DATA_INICIO_PERIODO")
    private LocalDate dataInicioPeriodo;

    @Column(name = "DATA_FIM_PERIODO")
    private LocalDate dataFimPeriodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ESCALA", length = 100)
    private TipoEscala tipoEscala;

    @Column(name = "PERMITE_BLOQUEIO")
    private Boolean permiteBloqueio;

    @Column(name = "HORARIO_INICIO_TURNO")
    private LocalDateTime horarioInicioTurno;

    @Column(name = "QTDE_CREDITOS")
    private Long qtdeDeCreditos;

    @Column(name = "DIAS_VALIDADE_CREDITO")
    private Long diasValidadeCredito;

    @Column(name = "ID_TEMPLATE")
    private Integer idTemplate;

    @Column(name = "ID_PLANO")
    private Integer idPlano;

    @Column(name = "ID_ESCALA")
    private Integer idEscala;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

    @OneToMany(mappedBy = "regra", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<HorarioEntity> horarios = new ArrayList<>();
}
