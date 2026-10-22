package br.com.startjob.acesso.core.usecase.permissao;

import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class RestaurarMatrizPermissaoWebUseCase {

    private final UsuarioGateway usuarioGateway;
    private final WebPerfilPermissaoGateway webPerfilPermissaoGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public List<WebPermissaoLinha> execute() {
        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para restaurar a matriz de permissões.");
        }

        Long idCliente = usuarioLogado.getCliente().getId();
        Map<PerfilAcesso, Set<String>> matrizPadrao = webPermissaoUseCase.carregarMatrizPadrao();

        webPerfilPermissaoGateway.salvarMatrizCliente(idCliente, matrizPadrao);
        log.info("Matriz de permissões web restaurada para o padrão no clienteId={}", idCliente);

        Map<PerfilAcesso, Set<String>> matrizAtualizada = webPermissaoUseCase.carregarMatrizEfetiva(idCliente);
        return webPermissaoUseCase.montarLinhas(matrizAtualizada);
    }
}
