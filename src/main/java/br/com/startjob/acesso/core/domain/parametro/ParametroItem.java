package br.com.startjob.acesso.core.domain.parametro;

import br.com.startjob.acesso.core.enumeration.SecaoParametro;
import br.com.startjob.acesso.core.enumeration.TipoDadoParametro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ParametroItem {

    private String codigo;
    private String nome;
    private String rotulo;
    private SecaoParametro secao;
    private TipoDadoParametro tipo;
    private Object valor;
    private Object valorPadrao;
    private Integer min;
    private Integer max;
    private List<OpcaoParametro> opcoes;
}
