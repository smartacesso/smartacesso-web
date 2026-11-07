package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.exception.BusinessException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Base64;

@Builder
public record SalvarCorrespondenciaRequest(
        Long id,
        @NotNull(message = "Destinatário é obrigatório")
        Long idDestinatario,
        @NotBlank(message = "Tipo de volume é obrigatório")
        @Size(max = 255, message = "Tipo de volume deve ter no máximo 255 caracteres")
        String tipo,
        @Size(max = 100, message = "Código de rastreio deve ter no máximo 100 caracteres")
        String codigoRastreio,
        @Size(max = 500, message = "Observação deve ter no máximo 500 caracteres")
        String observacao,
        String fotoBase64,
        @Size(max = 10, message = "Extensão da foto deve ter no máximo 10 caracteres")
        String extensaoFoto,
        Boolean removerFoto,
        @Size(max = 1, message = "Confirmação de retirada deve ter no máximo 1 caractere")
        String confirmaRetirada,
        LocalDateTime dataRecebimento,
        LocalDateTime dataRetirada,
        @Size(max = 150, message = "Nome de quem retirou deve ter no máximo 150 caracteres")
        String nomeQuemRetirou,
        @Size(max = 50, message = "Documento de quem retirou deve ter no máximo 50 caracteres")
        String documentoQuemRetirou
) {
    public Correspondencia toDomain() {
        byte[] fotoBytes = decodeFotoBase64(fotoBase64);
        String ext = extensaoFoto;
        if (fotoBytes != null && (ext == null || ext.isBlank())) {
            ext = "jpg";
        }

        return Correspondencia.builder()
                .id(id)
                .destinatario(idDestinatario != null ? Pedestre.builder().id(idDestinatario).build() : null)
                .tipo(tipo)
                .codigoRastreio(codigoRastreio)
                .observacao(observacao)
                .fotoVolume(fotoBytes)
                .extensaoFoto(ext)
                .confirmaRetirada(confirmaRetirada)
                .dataRecebimento(dataRecebimento)
                .dataRetirada(dataRetirada)
                .nomeQuemRetirou(nomeQuemRetirou)
                .documentoQuemRetirou(documentoQuemRetirou)
                .build();
    }

    private static byte[] decodeFotoBase64(String rawBase64) {
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
            throw new BusinessException("Formato de foto em Base64 inválido.");
        }
    }
}
