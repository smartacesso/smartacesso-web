package br.com.startjob.acesso.core.domain.endereco;

import br.com.startjob.acesso.core.domain.BaseDomain;
import lombok.*;
import lombok.experimental.SuperBuilder;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Endereco extends BaseDomain {

    private Long id;

    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    private String estado;
}
