package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsultaPedestreUseCase {

    private final PedestreGateway pedestreGateway;
    private final UsuarioGateway usuarioGateway;

    public Pedestre buscarPorId(Long id) {
        if (id == null) {
            throw new BusinessException("Identificador do pedestre não informado.");
        }

        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        Pedestre pedestre = pedestreGateway.findById(id)
                .orElseThrow(() -> new BusinessException("Pedestre não encontrado."));

        if (Boolean.TRUE.equals(pedestre.getRemovido())) {
            throw new BusinessException("Pedestre não encontrado.");
        }

        if (pedestre.getCliente() == null || !pedestre.getCliente().getId().equals(loggedUser.getCliente().getId())) {
            throw new BusinessException("O pedestre não pertence ao seu cliente.");
        }

        pedestre.setSenha(null);
        return pedestre;
    }
}
