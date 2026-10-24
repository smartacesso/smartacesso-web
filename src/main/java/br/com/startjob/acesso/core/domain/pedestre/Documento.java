package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@ToString(exclude = "arquivo")
@EqualsAndHashCode(callSuper = true, exclude = "arquivo")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Documento extends BaseDomain {

    private Long id;
    private String nome;
    private byte[] arquivo;
    private LocalDate validade;
}
