package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Builder
public record PedestreRegraResponse(
        Long id,
        Long idRegra,
        String nomeRegra,
        TipoRegra tipoRegra,
        String tipoRegraDescricao,
        LocalDate validade,
        Long qtdeDeCreditos,
        Long qtdeTotalDeCreditos,
        Long diasValidadeCredito,
        Boolean bloqueado,
        LocalDate dataInicioPeriodo,
        LocalDate dataFimPeriodo,
        LocalDate dataInicioEscala3_3,
        List<HorarioResponse> horarios
) {
    public static PedestreRegraResponse fromDomain(PedestreRegra domain) {
        if (domain == null) {
            return null;
        }

        List<HorarioResponse> horarios = domain.getHorarios() == null
                ? Collections.emptyList()
                : domain.getHorarios().stream()
                        .filter(h -> h.getRemovido() == null || !h.getRemovido())
                        .map(HorarioResponse::fromDomain)
                        .toList();

        if (horarios.isEmpty() && domain.getRegra() != null && domain.getRegra().getHorarios() != null) {
            horarios = domain.getRegra().getHorarios().stream()
                    .filter(h -> h.getRemovido() == null || !h.getRemovido())
                    .map(HorarioResponse::fromDomain)
                    .toList();
        }

        TipoRegra tipo = domain.getRegra() != null ? domain.getRegra().getTipo() : null;

        return PedestreRegraResponse.builder()
                .id(domain.getId())
                .idRegra(domain.getRegra() != null ? domain.getRegra().getId() : null)
                .nomeRegra(domain.getRegra() != null ? domain.getRegra().getNome() : null)
                .tipoRegra(tipo)
                .tipoRegraDescricao(tipo != null ? tipo.getDescricao() : null)
                .validade(domain.getValidade())
                .qtdeDeCreditos(domain.getQtdeDeCreditos())
                .qtdeTotalDeCreditos(domain.getQtdeTotalDeCreditos())
                .diasValidadeCredito(domain.getDiasValidadeCredito())
                .bloqueado(domain.getBloqueado())
                .dataInicioPeriodo(domain.getDataInicioPeriodo())
                .dataFimPeriodo(domain.getDataFimPeriodo())
                .dataInicioEscala3_3(domain.getDataInicioEscala3_3())
                .horarios(horarios)
                .build();
    }
}
