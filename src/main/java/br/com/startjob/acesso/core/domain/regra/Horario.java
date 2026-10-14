package br.com.startjob.acesso.core.domain.regra;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@ToString(exclude = "regra")
@EqualsAndHashCode(callSuper = true, exclude = "regra")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Horario extends BaseDomain {

    private Long id;
    private String nome;
    private Status status;
    private Regra regra;
    private Long idPedestreRegra;
    private String diasSemana;
    private LocalDateTime horarioInicio;
    private LocalDateTime horarioFim;
    private Long qtdeDeCreditos;
    private Integer idHorarioSenior;
}
