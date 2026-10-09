package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UsuarioGateway {

    List<Usuario> findByLoginAndUnidade(String login, String unidade);

    Optional<Usuario> findLoggedUser();

    Page<Usuario> buscarUsuarios(Long idCliente, String nome, String cpf, Pageable pageable);

    boolean existsByEmail(String email);

    boolean existsByLoginAndCliente(String login, Long idCliente);

    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

}
