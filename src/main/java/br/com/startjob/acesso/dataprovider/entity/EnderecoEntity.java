package br.com.startjob.acesso.dataprovider.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableGenerator(name = "enderecoIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_ENDERECO",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_ENDERECO")
public class EnderecoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "enderecoIdGenerator")
    @Column(name = "ID_ENDERECO", nullable = false)
    private Long id;

    @Column(name = "CEP", length = 20)
    private String cep;

    @Column(name = "LOGRADOURO", length = 255)
    private String logradouro;

    @Column(name = "NUMERO", length = 10)
    private String numero;

    @Column(name = "COMPLEMENTO", length = 255)
    private String complemento;

    @Column(name = "BAIRRO", length = 100)
    private String bairro;

    @Column(name = "CIDADE", length = 255)
    private String cidade;

    @Column(name = "ESTADO", length = 100)
    private String estado;
}
