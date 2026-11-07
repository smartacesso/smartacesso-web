package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
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
public class ExcluirCorrespondenciaUseCase {

    private final CorrespondenciaGateway correspondenciaGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public void execute(Long idCorrespondencia) {
        if (idCorrespondencia == null) {
            throw new BusinessException("Identificador da correspondência não informado.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para excluir correspondências.");
        }

        Correspondencia correspondenciaExistente = correspondenciaGateway.findById(idCorrespondencia)
                .orElseThrow(() -> new BusinessException("Correspondência não encontrada."));

        if (Boolean.TRUE.equals(correspondenciaExistente.getRemovido())) {
            throw new BusinessException("Correspondência não encontrada.");
        }

        Long idClienteLogado = usuarioLogado.getCliente() != null ? usuarioLogado.getCliente().getId() : null;
        if (correspondenciaExistente.getCliente() == null
                || !correspondenciaExistente.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("A correspondência não pertence ao seu cliente.");
        }

        correspondenciaExistente.setRemovido(true);
        correspondenciaExistente.setDataRemovido(LocalDateTime.now());

        correspondenciaGateway.save(correspondenciaExistente);
        log.info("Correspondência id={} excluída com sucesso (exclusão lógica).", idCorrespondencia);
    }
}
