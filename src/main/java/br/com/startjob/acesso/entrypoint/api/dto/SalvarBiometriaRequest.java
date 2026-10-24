package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.Biometria;
import br.com.startjob.acesso.core.enumeration.Dedo;
import br.com.startjob.acesso.core.exception.BusinessException;
import lombok.Builder;

import java.util.Base64;

@Builder
public record SalvarBiometriaRequest(
        Long id,
        Dedo dedo,
        String templateBase64,
        String sampleBase64,
        Boolean removido
) {
    public Biometria toDomain() {
        return Biometria.builder()
                .id(id)
                .dedo(dedo)
                .template(decodeBase64(templateBase64))
                .sample(decodeBase64(sampleBase64))
                .removido(removido)
                .build();
    }

    private static byte[] decodeBase64(String rawBase64) {
        if (rawBase64 == null || rawBase64.isBlank()) {
            return null;
        }
        String payload = rawBase64.trim();
        int commaIdx = payload.indexOf(',');
        if (commaIdx >= 0) {
            payload = payload.substring(commaIdx + 1).trim();
        }
        if (payload.isEmpty()) {
            return null;
        }
        try {
            return Base64.getDecoder().decode(payload);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Formato de biometria em Base64 inválido.");
        }
    }
}
