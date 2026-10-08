package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.usuario.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioGateway {

    List<Usuario> findByLoginAndUnidade(String login, String unidade);

    Optional<Usuario> findLoggedUser();

}
