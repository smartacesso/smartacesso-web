package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.Responsavel;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ResponsavelResumoResponse(
        Long id,
        String nome,
        String cpf,
        String email,
        LocalDate dataNascimento
) {
    public static ResponsavelResumoResponse fromPedestre(Pedestre domain) {
        if (domain == null) {
            return null;
        }
        return ResponsavelResumoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .cpf(domain.getCpf())
                .email(domain.getEmail())
                .dataNascimento(domain.getDataNascimento())
                .build();
    }

    public static ResponsavelResumoResponse fromResponsavel(Responsavel domain) {
        if (domain == null) {
            return null;
        }
        return ResponsavelResumoResponse.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .cpf(domain.getCpf())
                .email(domain.getEmail())
                .dataNascimento(domain.getDataNascimento())
                .build();
    }
}
