package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class MensagemEquipamento extends BaseDomain {

    private Long id;
    private String nome;
    private Status status;
    private String mensagem;
    private Long quantidade;
    private LocalDate validade;
}
