package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.enumeration.PerfilAcessoApp;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Pedestre extends BaseDomain {

    private Long id;

    private String nome;

    private Status status;

    private String login;

    private String senha;

    private PerfilAcessoApp perfilApp;

    private Cliente cliente;
}
