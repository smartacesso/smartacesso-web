package br.com.startjob.acesso.core.domain.equipamento;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@ToString(exclude = {"cliente", "usuario"})
@EqualsAndHashCode(callSuper = true, exclude = {"cliente", "usuario"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Equipamento extends BaseDomain {

    private Long id;
    private String nome;
    private String ip;
    private String identificador;
    private String marca;
    private String modelo;
    private String local;
    private Cliente cliente;
    private Usuario usuario;
}
