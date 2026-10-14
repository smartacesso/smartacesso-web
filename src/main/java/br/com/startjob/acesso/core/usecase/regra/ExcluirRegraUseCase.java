package br.com.startjob.acesso.core.usecase.regra;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.RegraGateway;
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
public class ExcluirRegraUseCase {

    private final RegraGateway regraGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public void execute(Long idRegra) {
        if (idRegra == null) {
            throw new BusinessException("Identificador da regra não informado.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_REGRAS_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para excluir regras de acesso.");
        }

        Regra regraExistente = regraGateway.findById(idRegra)
                .orElseThrow(() -> new BusinessException("Regra não encontrada."));

        if (Boolean.TRUE.equals(regraExistente.getRemovido())) {
            throw new BusinessException("Regra não encontrada.");
        }

        Long idClienteLogado = usuarioLogado.getCliente() != null ? usuarioLogado.getCliente().getId() : null;
        if (regraExistente.getCliente() == null || !regraExistente.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("A regra não pertence ao seu cliente.");
        }

        regraExistente.setRemovido(true);
        regraExistente.setDataRemovido(LocalDateTime.now());

        regraGateway.save(regraExistente);
        log.info("Regra id={} excluída com sucesso (exclusão lógica).", idRegra);
    }
}
