package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.equipamento.Equipamento;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class PedestreEquipamento extends BaseDomain {

    private Long id;
    private Equipamento equipamento;
    private LocalDate validade;
}
