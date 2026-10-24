package br.com.startjob.acesso.core.domain.parametro;

import br.com.startjob.acesso.core.enumeration.SecaoParametro;
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
public class SecaoParametroGrupo {

    private SecaoParametro secao;
    private String titulo;
    private List<ParametroItem> parametros;
}
