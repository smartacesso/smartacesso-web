package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(exclude = "cliente")
@EqualsAndHashCode(callSuper = true, exclude = "cliente")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class HistoricoCota extends BaseDomain {

    private static final String[] MESES_EXTENSO = {
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    };

    private Long id;
    private Integer mes;
    private Integer ano;
    private Long cotaMensal;
    private Cliente cliente;

    public String getMesExtenso() {
        if (mes == null || mes < 0 || mes > 11) {
            return "Mês inválido";
        }
        return MESES_EXTENSO[mes];
    }
}
