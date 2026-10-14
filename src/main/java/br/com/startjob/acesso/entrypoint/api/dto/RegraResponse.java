package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoEscala;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Builder
public record RegraResponse(
        Long id,
        String nome,
        Status status,
        LocalDate validade,
        Long idEmpresa,
        String nomeEmpresa,
        TipoRegra tipo,
        String tipoDescricao,
        TipoPedestre tipoPedestre,
        String descricao,
        LocalDate dataInicioPeriodo,
        LocalDate dataFimPeriodo,
        TipoEscala tipoEscala,
        String tipoEscalaDescricao,
        Boolean permiteBloqueio,
        LocalDateTime horarioInicioTurno,
        Long qtdeDeCreditos,
        Long diasValidadeCredito,
        Integer idTemplate,
        Integer idPlano,
        Integer idEscala,
        List<HorarioResponse> horarios
) {
    public static RegraResponse fromDomain(Regra domain) {
        if (domain == null) {
            return null;
        }

        List<HorarioResponse> horarios = domain.getHorarios() == null
                ? Collections.emptyList()
                : domain.getHorarios().stream()
                        .filter(h -> h.getRemovido() == null || !h.getRemovido())
                        .map(HorarioResponse::fromDomain)
                        .toList();

        return RegraResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .validade(domain.getValidade())
                .idEmpresa(domain.getEmpresa() != null ? domain.getEmpresa().getId() : null)
                .nomeEmpresa(domain.getEmpresa() != null ? domain.getEmpresa().getNome() : null)
                .tipo(domain.getTipo())
                .tipoDescricao(domain.getTipo() != null ? domain.getTipo().getDescricao() : null)
                .tipoPedestre(domain.getTipoPedestre())
                .descricao(domain.getDescricao())
                .dataInicioPeriodo(domain.getDataInicioPeriodo())
                .dataFimPeriodo(domain.getDataFimPeriodo())
                .tipoEscala(domain.getTipoEscala())
                .tipoEscalaDescricao(domain.getTipoEscala() != null ? domain.getTipoEscala().getDescricao() : null)
                .permiteBloqueio(domain.getPermiteBloqueio())
                .horarioInicioTurno(domain.getHorarioInicioTurno())
                .qtdeDeCreditos(domain.getQtdeDeCreditos())
                .diasValidadeCredito(domain.getDiasValidadeCredito())
                .idTemplate(domain.getIdTemplate())
                .idPlano(domain.getIdPlano())
                .idEscala(domain.getIdEscala())
                .horarios(horarios)
                .build();
    }
}
