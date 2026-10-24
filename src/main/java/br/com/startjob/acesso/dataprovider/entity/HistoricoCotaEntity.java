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

@ToString(exclude = {"pedestre", "cliente"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TB_COTAS")
public class HistoricoCotaEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "cota_gen")
    @TableGenerator(
            name = "cota_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "TB_COTAS",
            allocationSize = 50
    )
    @Column(name = "ID_COTA", nullable = false)
    private Long id;

    @Column(name = "MES")
    private Integer mes;

    @Column(name = "ANO")
    private Integer ano;

    @Column(name = "COTA_MENSAL")
    private Long cotaMensal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PEDESTRE_ID", nullable = false)
    private PedestreEntity pedestre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;
}
