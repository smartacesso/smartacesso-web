package br.com.startjob.acesso.core.domain.regra;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoEscala;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(exclude = "horarios")
@EqualsAndHashCode(callSuper = true, exclude = "horarios")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Regra extends BaseDomain {

    private Long id;
    private String nome;
    private Status status;
    private LocalDate validade;
    private Empresa empresa;
    private TipoRegra tipo;
    private TipoPedestre tipoPedestre;
    private String descricao;
    private LocalDate dataInicioPeriodo;
    private LocalDate dataFimPeriodo;
    private TipoEscala tipoEscala;
    private Boolean permiteBloqueio;
    private LocalDateTime horarioInicioTurno;
    private Long qtdeDeCreditos;
    private Long diasValidadeCredito;
    private Integer idTemplate;
    private Integer idPlano;
    private Integer idEscala;
    private Cliente cliente;

    @Builder.Default
    private List<Horario> horarios = new ArrayList<>();
}
