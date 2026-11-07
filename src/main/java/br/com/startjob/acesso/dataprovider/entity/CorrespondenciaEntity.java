package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.Basic;
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

import java.time.LocalDateTime;

@ToString(exclude = {"destinatario", "usuarioRecebeu", "cliente", "fotoVolume"})
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "TB_CORRESPONDENCIA")
public class CorrespondenciaEntity extends BaseEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "correspondencia_gen")
    @TableGenerator(
            name = "correspondencia_gen",
            table = "TB_ID_GENERATOR",
            pkColumnName = "GEN_NAME",
            valueColumnName = "GEN_VALUE",
            pkColumnValue = "TB_CORRESPONDENCIA",
            allocationSize = 50
    )
    @Column(name = "ID_CORRESPONDENCIA", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PEDESTRE", nullable = false)
    private PedestreEntity destinatario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_RECEBEU")
    private UsuarioEntity usuarioRecebeu;

    @Column(name = "TIPO_VOLUME", length = 255)
    private String tipo;

    @Column(name = "CODIGO_RASTREIO", length = 100)
    private String codigoRastreio;

    @Column(name = "ID_ARQUIVO_FOTO")
    private Long idArquivoFoto;

    @Column(name = "DATA_RECEBIMENTO")
    private LocalDateTime dataRecebimento;

    @Column(name = "CONFIRMA_RETIRADA", length = 1)
    private String confirmaRetirada = "N";

    @Column(name = "DATA_RETIRADA")
    private LocalDateTime dataRetirada;

    @Column(name = "OBSERVACAO", length = 500)
    private String observacao;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "FOTO_VOLUME")
    private byte[] fotoVolume;

    @Column(name = "EXTENSAO_FOTO", length = 10)
    private String extensaoFoto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

    @Column(name = "NOME_QUEM_RETIROU", length = 150)
    private String nomeQuemRetirou;

    @Column(name = "DOC_QUEM_RETIROU", length = 50)
    private String documentoQuemRetirou;
}
