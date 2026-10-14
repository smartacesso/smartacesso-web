package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record HorarioResponse(
        Long id,
        String nome,
        Status status,
        Long idPedestreRegra,
        String diasSemana,
        LocalDateTime horarioInicio,
        LocalDateTime horarioFim,
        Long qtdeDeCreditos,
        Integer idHorarioSenior
) {
    public static HorarioResponse fromDomain(Horario domain) {
        if (domain == null) {
            return null;
        }
        return HorarioResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .idPedestreRegra(domain.getIdPedestreRegra())
                .diasSemana(domain.getDiasSemana())
                .horarioInicio(domain.getHorarioInicio())
                .horarioFim(domain.getHorarioFim())
                .qtdeDeCreditos(domain.getQtdeDeCreditos())
                .idHorarioSenior(domain.getIdHorarioSenior())
                .build();
    }
}
