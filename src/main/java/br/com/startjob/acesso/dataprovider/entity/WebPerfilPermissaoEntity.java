package br.com.startjob.acesso.dataprovider.entity;

import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
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
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableGenerator(name = "webPerfilPermissaoIdGenerator", table = "TB_ID_GENERATOR",
        pkColumnName = "GEN_NAME", valueColumnName = "GEN_VALUE", pkColumnValue = "TB_WEB_PERFIL_PERMISSAO",
        allocationSize = 50, initialValue = 0)
@Entity
@Table(name = "TB_WEB_PERFIL_PERMISSAO", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ID_CLIENTE", "PERFIL", "CODIGO_PERMISSAO"})
})
public class WebPerfilPermissaoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "webPerfilPermissaoIdGenerator")
    @Column(name = "ID_WEB_PERFIL_PERMISSAO", nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "PERFIL", nullable = false, length = 20)
    private PerfilAcesso perfil;

    @Column(name = "CODIGO_PERMISSAO", nullable = false, length = 80)
    private String codigoPermissao;

    @Column(name = "HABILITADO", nullable = false)
    private Boolean habilitado = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLIENTE")
    private ClienteEntity cliente;
}
