package br.com.startjob.acesso.core.domain.plano;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Plano extends BaseDomain {

    private Long id;

    private String nome;

    private Status status;

    private LocalDateTime inicio;

    private LocalDateTime fim;

    private String periodicidadeCobranca;

    private Long diaVencimento;

    private Double valor;

    private Cliente cliente;
}
