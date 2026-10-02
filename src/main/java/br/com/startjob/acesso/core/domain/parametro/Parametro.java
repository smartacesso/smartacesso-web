package br.com.startjob.acesso.core.domain.parametro;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Parametro extends BaseDomain {

    private Long id;

    private String nome;

    private String valor;

    private Cliente cliente;
}
