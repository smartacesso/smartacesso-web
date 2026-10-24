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

@ToString(exclude = "pedestre")
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TB_MENSAGEM_EQUIPAMENTO")
public class MensagemEquipamentoEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "msg_equip_gen")
    @TableGenerator(
            name = "msg_equip_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "TB_MENSAGEM_EQUIPAMENTO",
            allocationSize = 50
    )
    @Column(name = "ID_MENSAGEM_EQUIPAMENTO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @Column(name = "MENSAGEM", length = 255)
    private String mensagem;

    @Column(name = "QUANTIDADE")
    private Long quantidade;

    @Column(name = "VALIDADE")
    private LocalDate validade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE")
    private PedestreEntity pedestre;
}
