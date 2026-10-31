package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedestreResumo {

    private Long id;
    private String matricula;
    private String codigoCartaoAcesso;
    private String nome;
    private String telefone;
    private String celular;
    private String cpf;
    private String rg;
    private TipoPedestre tipo;
    private Status status;
    private String nomeEmpresa;
    private String nomeDepartamento;
    private String nomeCentroCusto;
    private String nomeCargo;
    private String nomeRegraAtiva;
    private Boolean possuiFoto;

    public PedestreResumo(
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
            Boolean possuiFoto
    ) {
        this.id = id;
        this.matricula = matricula;
        this.codigoCartaoAcesso = codigoCartaoAcesso;
        this.nome = nome;
        this.telefone = telefone;
        this.celular = celular;
        this.cpf = cpf;
        this.rg = rg;
        this.tipo = tipo;
        this.status = status;
        this.nomeEmpresa = nomeEmpresa;
        this.nomeDepartamento = nomeDepartamento;
        this.nomeCentroCusto = nomeCentroCusto;
        this.nomeCargo = nomeCargo;
        this.possuiFoto = possuiFoto;
    }
}
