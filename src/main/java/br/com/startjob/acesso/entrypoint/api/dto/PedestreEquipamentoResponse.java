package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.PedestreEquipamento;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PedestreEquipamentoResponse(
        Long id,
        Long idEquipamento,
        String nomeEquipamento,
        String modeloEquipamento,
        LocalDate validade
) {
    public static PedestreEquipamentoResponse fromDomain(PedestreEquipamento domain) {
        if (domain == null) {
            return null;
        }
        return PedestreEquipamentoResponse.builder()
                .id(domain.getId())
                .idEquipamento(domain.getEquipamento() != null ? domain.getEquipamento().getId() : null)
                .nomeEquipamento(domain.getEquipamento() != null ? domain.getEquipamento().getNome() : null)
                .modeloEquipamento(domain.getEquipamento() != null ? domain.getEquipamento().getModelo() : null)
                .validade(domain.getValidade())
                .build();
    }
}
