package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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

@ToString(exclude = {"pedestre", "arquivo"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TB_DOCUMENTO")
public class DocumentoEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "documento_gen")
    @TableGenerator(
            name = "documento_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "TB_DOCUMENTO",
            allocationSize = 50
    )
    @Column(name = "ID_DOCUMENTO", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Lob
    @Column(name = "ARQUIVO")
    private byte[] arquivo;

    @Column(name = "VALIDADE")
    private LocalDate validade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE")
    private PedestreEntity pedestre;
}
