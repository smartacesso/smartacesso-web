package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.MensagemEquipamento;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MensagemEquipamentoResponse(
        Long id,
        String nome,
        Status status,
        String mensagem,
        Long quantidade,
        LocalDate validade
) {
    public static MensagemEquipamentoResponse fromDomain(MensagemEquipamento domain) {
        if (domain == null) {
            return null;
        }
        return MensagemEquipamentoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .status(domain.getStatus())
                .mensagem(domain.getMensagem())
                .quantidade(domain.getQuantidade())
                .validade(domain.getValidade())
                .build();
    }
}
