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

import java.time.LocalDate;

@ToString(exclude = {"pedestre", "equipamento"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "RE_PEDESTRE_EQUIPAMENTO")
public class PedestreEquipamentoEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pedestre_equip_gen")
    @TableGenerator(
            name = "pedestre_equip_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "RE_PEDESTRE_EQUIPAMENTO",
            allocationSize = 50
    )
    @Column(name = "ID_PEDESTRE_EQUIPAMENTO", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EQUIPAMENTO")
    private EquipamentoEntity equipamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE")
    private PedestreEntity pedestre;

    @Column(name = "VALIDADE")
    private LocalDate validade;
}
