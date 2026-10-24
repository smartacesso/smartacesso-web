package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.enumeration.Dedo;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(exclude = {"template", "sample"})
@EqualsAndHashCode(callSuper = true, exclude = {"template", "sample"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Biometria extends BaseDomain {

    private Long id;
    private Dedo dedo;
    private byte[] template;
    private byte[] sample;
}
