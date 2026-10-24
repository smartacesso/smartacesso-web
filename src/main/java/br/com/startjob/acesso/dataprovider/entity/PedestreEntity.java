package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.Genero;
import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoQRCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@TableGenerator(name = "pedestreIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_PEDESTRE",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_PEDESTRE")
public class PedestreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "pedestreIdGenerator")
    @Column(name = "ID_PEDESTRE", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 100)
    private Status status;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "CPF", length = 20)
    private String cpf;

    @Column(name = "RG", length = 20)
    private String rg;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "CELULAR", length = 20)
    private String celular;

    @Column(name = "MATRICULA", length = 200)
    private String matricula;

    @Column(name = "CARTAO_ACESSO", length = 50)
    private String codigoCartaoAcesso;

    @Column(name = "OBSERVACOES", length = 300)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_QRCODE", length = 100)
    private TipoQRCode tipoQRCode;

    @Column(name = "QR_CODE_PARA_ACESSO", length = 100)
    private String qrCodeParaAcesso;

    @Column(name = "SEMPRE_LIBERADO")
    private Boolean sempreLiberado;

    @Column(name = "ACESSO_LIVRE")
    private Boolean acessoLivre;

    @Column(name = "HABILITAR_TECLADO")
    private Boolean habilitarTeclado;

    @Column(name = "CADASTRO_FACIAL_OBRIGATORIO")
    private Boolean cadastroFacialObrigatorio;

    @Lob
    @Column(name = "FOTO")
    private byte[] foto;

    @Column(name = "DATA_ALTERACAO_FOTO")
    private LocalDateTime dataAlteracaoFoto;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PEDESTRE", length = 15)
    private TipoPedestre tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENERO", length = 15)
    private Genero genero;

    @Column(name = "ENVIAR_SMS_AO_PASSAR_NA_CATRACA")
    private Boolean enviaSmsAoPassarNaCatraca;

    @Column(name = "UUID_LOCAL", length = 255)
    private String uuidLocal;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_ENDERECO")
    private EnderecoEntity endereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPRESA")
    private EmpresaEntity empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DEPARTAMENTO")
    private DepartamentoEntity departamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CENTRO_CUSTO")
    private CentroCustoEntity centroCusto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CARGO")
    private CargoEntity cargo;

    @Column(name = "EMPRESA_VISITADA", length = 200)
    private String empresaVisitada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_EMPRESA_VISITADA")
    private EmpresaEntity empresaVisitadaRef;

    @Column(name = "LOGIN", length = 100)
    private String login;

    @JsonIgnore
    @Column(name = "SENHA", length = 255)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL_APP")
    private PerfilAcessoApp perfilApp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO")
    private UsuarioEntity usuario;

    @Column(name = "JUSTIFICATIVA_LIBERADO", length = 255)
    private String justificativa;

    @Column(name = "DATA_INICIO_PERIODO_AGENDAMENTO")
    private LocalDateTime dataInicioPeriodoAgendamento;

    @Column(name = "DATA_FIM_PERIODO_AGENDAMENTO")
    private LocalDateTime dataFimPeriodoAgendamento;

    @Column(name = "AGENDAMENTO_LIBERADO")
    private Boolean agendamentoLiberado;

    @BatchSize(size = 50)
    @SQLRestriction("removido = 'F' or removido is null")
    @OneToMany(mappedBy = "pedestre", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private List<PedestreRegraEntity> regras = new ArrayList<>();

    public PedestreEntity(Long id, String login, String senha, PerfilAcessoApp perfilApp) {
        this.id = id;
        this.login = login;
        this.senha = senha;
        this.perfilApp = perfilApp;
    }
}
