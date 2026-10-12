package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@TableGenerator(name = "empresaIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_EMPRESA",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_EMPRESA")
public class EmpresaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "empresaIdGenerator")
    @Column(name = "ID_EMPRESA", nullable = false)
    private Long id;

    @Column(name = "NOME", length = 255)
    private String nome;

    @Column(name = "CNPJ", length = 50)
    private String cnpj;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "CELULAR", length = 20)
    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 10)
    private Status status;

    @Column(name = "CODIGO_EXTERNO", length = 255)
    private String codigoExterno;

    @Column(name = "COD_EMPRESA_SENIOR", length = 40)
    private String codEmpresaSenior;

    @Column(name = "PRIM_IMPORT_FUNC_SENIOR_SUCESSO")
    private Boolean primeiroImportacaoFuncionarioSeniorSucesso;

    @Column(name = "AUTO_ATENDIMENTO")
    private Boolean autoAtendimentoLiberado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;

    @BatchSize(size = 50)
    @SQLRestriction("removido = 'F' or removido is null")
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DepartamentoEntity> departamentos = new ArrayList<>();

    @BatchSize(size = 50)
    @SQLRestriction("removido = 'F' or removido is null")
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CentroCustoEntity> centros = new ArrayList<>();

    @BatchSize(size = 50)
    @SQLRestriction("removido = 'F' or removido is null")
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CargoEntity> cargos = new ArrayList<>();
}
