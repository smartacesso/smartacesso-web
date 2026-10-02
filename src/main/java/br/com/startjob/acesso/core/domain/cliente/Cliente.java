package br.com.startjob.acesso.core.domain.cliente;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.plano.Plano;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Cliente extends BaseDomain {

    private Long id;

    private String nome;

    private String email;

    private String cnpj;

    private String telefone;

    private String celular;

    private String contato;

    private Status status;

    private String nomeUnidadeOrganizacional;

    private List<Plano> planos = new ArrayList<>();
    
}
