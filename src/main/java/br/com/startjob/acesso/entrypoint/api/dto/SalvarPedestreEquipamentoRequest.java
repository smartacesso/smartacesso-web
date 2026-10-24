package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import br.com.startjob.acesso.core.domain.pedestre.PedestreEquipamento;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SalvarPedestreEquipamentoRequest(
        Long id,
        @NotNull(message = "Equipamento é obrigatório")
        Long idEquipamento,
        LocalDate validade,
        Boolean removido
) {
    public PedestreEquipamento toDomain() {
        return PedestreEquipamento.builder()
                .id(id)
                .equipamento(idEquipamento != null ? Equipamento.builder().id(idEquipamento).build() : null)
                .validade(validade)
                .removido(removido)
                .build();
    }
}
