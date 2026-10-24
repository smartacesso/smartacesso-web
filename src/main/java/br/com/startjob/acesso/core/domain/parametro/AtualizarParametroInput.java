package br.com.startjob.acesso.core.domain.parametro;

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
@Getter
@Setter
public class AtualizarParametroInput {

    private String codigo;
    private Object valor;
}
