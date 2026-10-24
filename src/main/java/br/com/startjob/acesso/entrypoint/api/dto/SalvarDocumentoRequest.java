package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.Documento;
import br.com.startjob.acesso.core.exception.BusinessException;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Base64;

@Builder
public record SalvarDocumentoRequest(
        Long id,
        @Size(max = 255, message = "Nome do documento deve ter no máximo 255 caracteres")
        String nome,
        String arquivoBase64,
        LocalDate validade,
        Boolean removido
) {
    public Documento toDomain() {
        return Documento.builder()
                .id(id)
                .nome(nome)
                .arquivo(decodeBase64(arquivoBase64))
                .validade(validade)
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
            throw new BusinessException("Formato de arquivo do documento em Base64 inválido.");
        }
    }
}
