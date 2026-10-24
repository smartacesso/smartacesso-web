package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@ToString(exclude = "pedestre")
@EqualsAndHashCode(callSuper = true, exclude = "pedestre")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class PedestreRegra extends BaseDomain {

    private Long id;
    private Regra regra;
    private Pedestre pedestre;
    private LocalDate validade;
    private Long qtdeDeCreditos;
    private Long qtdeTotalDeCreditos;
    private Long diasValidadeCredito;
    private Boolean bloqueado;
    private LocalDate dataInicioPeriodo;
    private LocalDate dataFimPeriodo;
    private LocalDate dataInicioEscala3_3;
    private List<Horario> horarios;
}
