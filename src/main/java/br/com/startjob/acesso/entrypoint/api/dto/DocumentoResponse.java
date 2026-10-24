package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.Documento;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Base64;

@Builder
public record DocumentoResponse(
        Long id,
        String nome,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String arquivoBase64,
        LocalDate validade
) {
    public static DocumentoResponse fromDomain(Documento domain) {
        if (domain == null) {
            return null;
        }
        String arquivoBase64 = (domain.getArquivo() != null && domain.getArquivo().length > 0)
                ? Base64.getEncoder().encodeToString(domain.getArquivo())
                : null;
        return DocumentoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .arquivoBase64(arquivoBase64)
                .validade(domain.getValidade())
                .build();
    }
}
