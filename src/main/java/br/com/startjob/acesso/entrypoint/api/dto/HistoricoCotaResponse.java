package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.HistoricoCota;
import lombok.Builder;

@Builder
public record HistoricoCotaResponse(
        Long id,
        Integer mes,
        String mesExtenso,
        Integer ano,
        Long cotaMensal
) {
    public static HistoricoCotaResponse fromDomain(HistoricoCota domain) {
        if (domain == null) {
            return null;
        }
        return HistoricoCotaResponse.builder()
                .id(domain.getId())
                .mes(domain.getMes())
                .mesExtenso(domain.getMesExtenso())
                .ano(domain.getAno())
                .cotaMensal(domain.getCotaMensal())
                .build();
    }
}
