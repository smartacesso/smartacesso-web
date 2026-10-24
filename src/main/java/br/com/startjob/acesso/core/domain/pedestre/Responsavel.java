package br.com.startjob.acesso.core.domain.pedestre;

import br.com.startjob.acesso.core.domain.BaseDomain;
import br.com.startjob.acesso.core.domain.cliente.Cliente;
import br.com.startjob.acesso.core.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@ToString(exclude = {"password", "cliente"})
@EqualsAndHashCode(callSuper = true, exclude = {"password", "cliente"})
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Setter
@Getter
public class Responsavel extends BaseDomain {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private Status status;
    private String rg;
    private String celular;
    private LocalDate dataNascimento;
    private String login;
    private String password;
    private String token;
    private String descricao;
    private String deviceKey;
    private Cliente cliente;
}
