package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Base64;

@Builder
public record CorrespondenciaResponse(
        Long id,
        Long idDestinatario,
        String nomeDestinatario,
        String cpfDestinatario,
        Long idUsuarioRecebeu,
        String nomeUsuarioRecebeu,
        String tipo,
        String codigoRastreio,
        Long idArquivoFoto,
        LocalDateTime dataRecebimento,
        String confirmaRetirada,
        LocalDateTime dataRetirada,
        String observacao,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        String fotoBase64,
        String extensaoFoto,
        Boolean possuiFoto,
        String nomeQuemRetirou,
        String documentoQuemRetirou
) {
    public static CorrespondenciaResponse fromDomain(Correspondencia domain) {
        if (domain == null) {
            return null;
        }

        boolean possuiFoto = domain.getFotoVolume() != null && domain.getFotoVolume().length > 0;
        String fotoBase64 = possuiFoto
                ? Base64.getEncoder().encodeToString(domain.getFotoVolume())
                : null;

        return CorrespondenciaResponse.builder()
                .id(domain.getId())
                .idDestinatario(domain.getDestinatario() != null ? domain.getDestinatario().getId() : null)
                .nomeDestinatario(domain.getDestinatario() != null ? domain.getDestinatario().getNome() : null)
                .cpfDestinatario(domain.getDestinatario() != null ? domain.getDestinatario().getCpf() : null)
                .idUsuarioRecebeu(domain.getUsuarioRecebeu() != null ? domain.getUsuarioRecebeu().getId() : null)
                .nomeUsuarioRecebeu(domain.getUsuarioRecebeu() != null ? domain.getUsuarioRecebeu().getNome() : null)
                .tipo(domain.getTipo())
                .codigoRastreio(domain.getCodigoRastreio())
                .idArquivoFoto(domain.getIdArquivoFoto())
                .dataRecebimento(domain.getDataRecebimento())
                .confirmaRetirada(domain.getConfirmaRetirada())
                .dataRetirada(domain.getDataRetirada())
                .observacao(domain.getObservacao())
                .fotoBase64(fotoBase64)
                .extensaoFoto(domain.getExtensaoFoto())
                .possuiFoto(possuiFoto)
                .nomeQuemRetirou(domain.getNomeQuemRetirou())
                .documentoQuemRetirou(domain.getDocumentoQuemRetirou())
                .build();
    }
}
