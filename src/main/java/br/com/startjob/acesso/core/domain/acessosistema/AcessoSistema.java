package br.com.startjob.acesso.core.domain.acessosistema;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class AcessoSistema extends BaseDomain {

    private Long id;

    private Usuario usuario;

    private Pedestre pedestre;

    private LocalDateTime data;

    private String dispositivo;

    private String tipo;
}
