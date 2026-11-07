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
public class ConfirmarRetiradaCorrespondenciaUseCase {

    private final CorrespondenciaGateway correspondenciaGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public Correspondencia execute(Long idCorrespondencia, String nomeQuemRetirou, String documentoQuemRetirou) {
        if (idCorrespondencia == null) {
            throw new BusinessException("Identificador da correspondência não informado.");
        }

        if (nomeQuemRetirou == null || nomeQuemRetirou.trim().isEmpty()) {
            throw new BusinessException("Informe o nome de quem está retirando.");
        }

        if (documentoQuemRetirou == null || documentoQuemRetirou.trim().isEmpty()) {
            throw new BusinessException("Informe o documento de quem está retirando.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para confirmar retirada de correspondências.");
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

        if ("S".equalsIgnoreCase(correspondenciaExistente.getConfirmaRetirada())) {
            throw new BusinessException("A retirada desta correspondência já foi confirmada.");
        }

        correspondenciaExistente.setConfirmaRetirada("S");
        correspondenciaExistente.setDataRetirada(LocalDateTime.now());
        correspondenciaExistente.setNomeQuemRetirou(nomeQuemRetirou.trim());
        correspondenciaExistente.setDocumentoQuemRetirou(documentoQuemRetirou.trim());

        Correspondencia salva = correspondenciaGateway.save(correspondenciaExistente);
        log.info("Retirada da correspondência id={} confirmada com sucesso.", idCorrespondencia);
        return salva;
    }
}
