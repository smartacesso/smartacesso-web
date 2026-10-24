package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.MensagemEquipamento;
import br.com.startjob.acesso.core.enumeration.Status;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SalvarMensagemEquipamentoRequest(
        Long id,
        @Size(max = 255, message = "Nome da mensagem deve ter no máximo 255 caracteres")
        String nome,
        Status status,
        @Size(max = 255, message = "Mensagem deve ter no máximo 255 caracteres")
        String mensagem,
        Long quantidade,
        LocalDate validade,
        Boolean removido
) {
    public MensagemEquipamento toDomain() {
        return MensagemEquipamento.builder()
                .id(id)
                .nome(nome)
                .status(status)
                .mensagem(mensagem)
                .quantidade(quantidade)
                .validade(validade)
                .removido(removido)
                .build();
    }
}
