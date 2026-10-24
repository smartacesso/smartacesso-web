package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.domain.regra.Regra;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Builder
public record SalvarPedestreRegraRequest(
        Long id,
        @NotNull(message = "O identificador da regra é obrigatório")
        Long idRegra,
        LocalDate validade,
        Long qtdeDeCreditos,
        Long qtdeTotalDeCreditos,
        Long diasValidadeCredito,
        Boolean bloqueado,
        LocalDate dataInicioPeriodo,
        LocalDate dataFimPeriodo,
        LocalDate dataInicioEscala3_3,
        Boolean removido,
        List<@Valid SalvarHorarioRequest> horarios
) {
    public PedestreRegra toDomain() {
        return PedestreRegra.builder()
                .id(id)
                .regra(idRegra != null ? Regra.builder().id(idRegra).build() : null)
                .validade(validade)
                .qtdeDeCreditos(qtdeDeCreditos)
                .qtdeTotalDeCreditos(qtdeTotalDeCreditos)
                .diasValidadeCredito(diasValidadeCredito)
                .bloqueado(bloqueado)
                .dataInicioPeriodo(dataInicioPeriodo)
                .dataFimPeriodo(dataFimPeriodo)
                .dataInicioEscala3_3(dataInicioEscala3_3)
                .removido(removido != null && removido)
                .horarios(horarios == null
                        ? Collections.emptyList()
                        : horarios.stream().map(SalvarHorarioRequest::toDomain).toList())
                .build();
    }
}
