package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoEscala;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Builder
public record SalvarRegraRequest(
        Long id,
        @NotBlank(message = "Nome da regra é obrigatório")
        String nome,
        @NotNull(message = "Status é obrigatório")
        Status status,
        LocalDate validade,
        Long idEmpresa,
        @NotNull(message = "Tipo de regra é obrigatório")
        TipoRegra tipo,
        @NotNull(message = "Tipo de pedestre é obrigatório")
        TipoPedestre tipoPedestre,
        String descricao,
        LocalDate dataInicioPeriodo,
        LocalDate dataFimPeriodo,
        TipoEscala tipoEscala,
        Boolean permiteBloqueio,
        LocalDateTime horarioInicioTurno,
        Long qtdeDeCreditos,
        Long diasValidadeCredito,
        Integer idTemplate,
        Integer idPlano,
        Integer idEscala,
        List<@Valid SalvarHorarioRequest> horarios
) {
    public SalvarRegraRequest withId(Long novoId) {
        return new SalvarRegraRequest(
                novoId,
                this.nome,
                this.status,
                this.validade,
                this.idEmpresa,
                this.tipo,
                this.tipoPedestre,
                this.descricao,
                this.dataInicioPeriodo,
                this.dataFimPeriodo,
                this.tipoEscala,
                this.permiteBloqueio,
                this.horarioInicioTurno,
                this.qtdeDeCreditos,
                this.diasValidadeCredito,
                this.idTemplate,
                this.idPlano,
                this.idEscala,
                this.horarios
        );
    }

    public Regra toDomain() {
        return Regra.builder()
                .id(id)
                .nome(nome)
                .status(status)
                .validade(validade)
                .empresa(idEmpresa != null ? Empresa.builder().id(idEmpresa).build() : null)
                .tipo(tipo)
                .tipoPedestre(tipoPedestre)
                .descricao(descricao)
                .dataInicioPeriodo(dataInicioPeriodo)
                .dataFimPeriodo(dataFimPeriodo)
                .tipoEscala(tipoEscala)
                .permiteBloqueio(permiteBloqueio != null && permiteBloqueio)
                .horarioInicioTurno(horarioInicioTurno)
                .qtdeDeCreditos(qtdeDeCreditos)
                .diasValidadeCredito(diasValidadeCredito)
                .idTemplate(idTemplate)
                .idPlano(idPlano)
                .idEscala(idEscala)
                .horarios(horarios == null ? Collections.emptyList() : horarios.stream().map(SalvarHorarioRequest::toDomain).toList())
                .build();
    }
}
