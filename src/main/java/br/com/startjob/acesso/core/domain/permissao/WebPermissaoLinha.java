package br.com.startjob.acesso.core.domain.permissao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class WebPermissaoLinha {

    private String codigo;
    private String descricao;
    private boolean administrador;
    private boolean gerente;
    private boolean operador;
    private boolean porteiro;
    private boolean cuidador;
    private boolean responsavel;
}
