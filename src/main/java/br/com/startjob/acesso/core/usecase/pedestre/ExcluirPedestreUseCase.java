package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.Status;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
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
public class ExcluirPedestreUseCase {

    private final PedestreGateway pedestreGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public void execute(Long idPedestre) {
        if (idPedestre == null) {
            throw new BusinessException("Identificador do pedestre não informado.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        Pedestre pedestreExistente = pedestreGateway.findById(idPedestre)
                .orElseThrow(() -> new BusinessException("Pedestre não encontrado."));

        if (Boolean.TRUE.equals(pedestreExistente.getRemovido())) {
            throw new BusinessException("Pedestre não encontrado.");
        }

        Long idClienteLogado = usuarioLogado.getCliente().getId();
        if (pedestreExistente.getCliente() == null
                || !pedestreExistente.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("O pedestre não pertence ao seu cliente.");
        }

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (pedestreExistente.isVisitante()) {
            if (!permissoes.contains(WebPermissao.VISITANTE_EXCLUIR.getCodigo())) {
                throw new BusinessException("Você não possui permissão para excluir visitantes.");
            }
        } else {
            if (!permissoes.contains(WebPermissao.PEDESTRE_EXCLUIR.getCodigo())) {
                throw new BusinessException("Você não possui permissão para excluir pedestres.");
            }
        }

        pedestreExistente.setStatus(Status.INATIVO);
        pedestreExistente.setDataRemovido(LocalDateTime.now());
        pedestreExistente.setRemovido(true);
        pedestreExistente.setCodigoCartaoAcesso(null);

        pedestreGateway.save(pedestreExistente);
        log.info("Pedestre/Visitante id={} tipo={} excluído com sucesso (exclusão lógica).",
                idPedestre, pedestreExistente.getTipo());
    }
}
