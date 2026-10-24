package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.Biometria;
import br.com.startjob.acesso.core.enumeration.Dedo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.Base64;

@Builder
public record BiometriaResponse(
        Long id,
        Dedo dedo,
        String dedoDescricao,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String sampleBase64
) {
    public static BiometriaResponse fromDomain(Biometria domain) {
        if (domain == null) {
            return null;
        }
        String sampleBase64 = (domain.getSample() != null && domain.getSample().length > 0)
                ? Base64.getEncoder().encodeToString(domain.getSample())
                : null;
        return BiometriaResponse.builder()
                .id(domain.getId())
                .dedo(domain.getDedo())
                .dedoDescricao(domain.getDedo() != null ? domain.getDedo().getDescricao() : null)
                .sampleBase64(sampleBase64)
                .build();
    }
}
