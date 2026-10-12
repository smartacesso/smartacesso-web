package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExcluirUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public void execute(Long idUsuario) {
        if (idUsuario == null) {
            throw new BusinessException("Identificador do usuário não informado.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.USUARIO_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para excluir usuários.");
        }

        Usuario usuarioExistente = usuarioGateway.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        if (Boolean.TRUE.equals(usuarioExistente.getRemovido())) {
            throw new BusinessException("Usuário não encontrado.");
        }

        Long idClienteLogado = usuarioLogado.getCliente() != null ? usuarioLogado.getCliente().getId() : null;
        if (usuarioExistente.getCliente() == null || !usuarioExistente.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("O usuário não pertence ao seu cliente.");
        }

        if (usuarioExistente.getId().equals(usuarioLogado.getId())) {
            throw new BusinessException("Não é permitido excluir o próprio usuário logado.");
        }

        usuarioExistente.setRemovido(true);
        usuarioExistente.setDataRemovido(LocalDateTime.now());

        usuarioGateway.save(usuarioExistente);
        log.info("Usuário id={} excluído com sucesso (exclusão lógica).", idUsuario);
    }
}
