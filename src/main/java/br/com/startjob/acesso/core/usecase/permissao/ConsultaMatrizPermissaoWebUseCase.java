package br.com.startjob.acesso.core.usecase.permissao;

import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ConsultaMatrizPermissaoWebUseCase {

    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional(readOnly = true)
    public List<WebPermissaoLinha> execute(boolean padrao) {
        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_WEB_PERMISSOES_VER.getCodigo())
                && !permissoes.contains(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para visualizar a matriz de permissões.");
        }

        Map<PerfilAcesso, Set<String>> matriz = padrao
                ? webPermissaoUseCase.carregarMatrizPadrao()
                : webPermissaoUseCase.carregarMatrizEfetiva(usuarioLogado.getCliente().getId());

        return webPermissaoUseCase.montarLinhas(matriz);
    }
}
