package br.com.startjob.acesso.entrypoint.api.dto;

import br.com.startjob.acesso.core.domain.pedestre.PedestreResumo;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import lombok.Builder;

@Builder
public record PedestreResumoResponse(
        Long id,
        String matricula,
        String codigoCartaoAcesso,
        String nome,
        String telefone,
        String celular,
        String cpf,
        String rg,
        TipoPedestre tipo,
        Status status,
        String nomeEmpresa,
        String nomeDepartamento,
        String nomeCentroCusto,
        String nomeCargo,
        String nomeRegraAtiva,
        Boolean possuiFoto
) {
    public static PedestreResumoResponse fromDomain(PedestreResumo domain) {
        if (domain == null) {
            return null;
        }
        return PedestreResumoResponse.builder()
                .id(domain.getId())
                .matricula(domain.getMatricula())
                .codigoCartaoAcesso(domain.getCodigoCartaoAcesso())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .celular(domain.getCelular())
                .cpf(domain.getCpf())
                .rg(domain.getRg())
                .tipo(domain.getTipo())
                .status(domain.getStatus())
                .nomeEmpresa(domain.getNomeEmpresa())
                .nomeDepartamento(domain.getNomeDepartamento())
                .nomeCentroCusto(domain.getNomeCentroCusto())
                .nomeCargo(domain.getNomeCargo())
                .nomeRegraAtiva(domain.getNomeRegraAtiva())
                .possuiFoto(domain.getPossuiFoto())
                .build();
    }
}
