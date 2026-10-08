package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsultaUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;

    public Page<Usuario> execute(String nome, String cpf, Pageable pageable) {
        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        return usuarioGateway.buscarUsuarios(loggedUser.getCliente().getId(), nome, cpf, pageable);
    }
}
