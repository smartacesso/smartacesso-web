package br.com.startjob.acesso.core.domain.empresa;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString(exclude = "empresa")
@EqualsAndHashCode(callSuper = true, exclude = "empresa")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Departamento extends BaseDomain {

    private Long id;

    private String nome;

    private Status status;

    private Empresa empresa;
}
