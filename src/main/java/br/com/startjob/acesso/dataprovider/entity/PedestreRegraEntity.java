package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@TableGenerator(name = "pedestreRegraIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "RE_PEDESTRE_REGRA",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "RE_PEDESTRE_REGRA")
public class PedestreRegraEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pedestreRegraIdGenerator")
    @Column(name = "ID_PEDESTRE_REGRA", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGRA")
    private RegraEntity regra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE")
    private PedestreEntity pedestre;

    @Column(name = "VALIDADE")
    private LocalDate validade;

    @Column(name = "QTDE_CREDITOS")
    private Long qtdeDeCreditos;

    @Column(name = "QTDE_TOTAL_CREDITOS")
    private Long qtdeTotalDeCreditos;

    @Column(name = "DIAS_VALIDADE_CREDITO")
    private Long diasValidadeCredito;

    @Column(name = "BLOQUEADO")
    private Boolean bloqueado;

    @Column(name = "DATA_INICIO_PERIODO")
    private LocalDate dataInicioPeriodo;

    @Column(name = "DATA_FIM_PERIODO")
    private LocalDate dataFimPeriodo;

    @Column(name = "DATA_INICIO_ESCALA_3_3")
    private LocalDate dataInicioEscala3_3;

    @BatchSize(size = 50)
    @SQLRestriction("removido = 'F' or removido is null")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE_REGRA")
    private List<HorarioEntity> horarios = new ArrayList<>();
}
