package br.com.startjob.acesso.core.domain.empresa;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@ToString(exclude = {"departamentos", "centros", "cargos"})
@EqualsAndHashCode(callSuper = true, exclude = {"departamentos", "centros", "cargos"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Empresa extends BaseDomain {

    private Long id;

    private String nome;

    private String cnpj;

    private String email;

    private String telefone;

    private String celular;

    private Status status;

    private String codigoExterno;

    private String codEmpresaSenior;

    private Boolean primeiroImportacaoFuncionarioSeniorSucesso;

    private Boolean autoAtendimentoLiberado;

    private Cliente cliente;

    @Builder.Default
    private List<Departamento> departamentos = new ArrayList<>();

    @Builder.Default
    private List<CentroCusto> centros = new ArrayList<>();

    @Builder.Default
    private List<Cargo> cargos = new ArrayList<>();
}
