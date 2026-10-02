package br.com.startjob.acesso.core.domain.permissao;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class WebPerfilPermissao extends BaseDomain {

    private Long id;

    private PerfilAcesso perfil;

    private String codigoPermissao;

    private Boolean habilitado;

    private Cliente cliente;
}
