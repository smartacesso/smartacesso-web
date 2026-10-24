package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.HistoricoCota;
import lombok.Builder;

@Builder
public record SalvarHistoricoCotaRequest(
        Long id,
        Integer mes,
        Integer ano,
        Long cotaMensal,
        Boolean removido
) {
    public HistoricoCota toDomain() {
        return HistoricoCota.builder()
                .id(id)
                .mes(mes)
                .ano(ano)
                .cotaMensal(cotaMensal)
                .removido(removido)
                .build();
    }
}
