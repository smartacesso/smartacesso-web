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
public class PedestreFiltro {

    private String matricula;
    private String codigoCartaoAcesso;
    private String nome;
    private String contato;
    private String cpf;
    private String rg;
    private TipoPedestre tipo;
    private Long idEmpresa;
    private Long idDepartamento;
    private Long idCentroCusto;
    private Long idCargo;
    private String uuidLocal;
    private Status status;
    private Boolean possuiFoto;

}
