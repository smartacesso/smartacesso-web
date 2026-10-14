package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SalvarHorarioRequest(
        Long id,
        @NotBlank(message = "Nome do horário é obrigatório")
        String nome,
        @NotNull(message = "Status do horário é obrigatório")
        Status status,
        @NotBlank(message = "Dias da semana é obrigatório")
        String diasSemana,
        @NotNull(message = "Horário de início é obrigatório")
        LocalDateTime horarioInicio,
        @NotNull(message = "Horário de término é obrigatório")
        LocalDateTime horarioFim,
        Long qtdeDeCreditos,
        Long idPedestreRegra,
        Integer idHorarioSenior
) {
    public Horario toDomain() {
        return Horario.builder()
                .id(id)
                .nome(nome)
                .status(status)
                .diasSemana(diasSemana)
                .horarioInicio(horarioInicio)
                .horarioFim(horarioFim)
                .qtdeDeCreditos(qtdeDeCreditos)
                .idPedestreRegra(idPedestreRegra)
                .idHorarioSenior(idHorarioSenior)
                .build();
    }
}
