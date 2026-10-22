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

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarMatrizPermissaoWebUseCase {

    private final UsuarioGateway usuarioGateway;
    private final WebPerfilPermissaoGateway webPerfilPermissaoGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public List<WebPermissaoLinha> execute(List<WebPermissaoLinha> linhas) {
        if (linhas == null || linhas.isEmpty()) {
            throw new BusinessException("Dados da matriz de permissões não informados.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_WEB_PERMISSOES_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para editar a matriz de permissões.");
        }

        Map<PerfilAcesso, Set<String>> matriz = new EnumMap<>(PerfilAcesso.class);
        for (PerfilAcesso perfil : PerfilAcesso.values()) {
            matriz.put(perfil, new HashSet<>());
        }

        for (WebPermissaoLinha linha : linhas) {
            if (linha == null || linha.getCodigo() == null || linha.getCodigo().isBlank()) {
                continue;
            }
            if (WebPermissao.porCodigo(linha.getCodigo()) == null) {
                throw new BusinessException("Código de permissão inválido: " + linha.getCodigo());
            }
            adicionarSe(matriz, PerfilAcesso.ADMINISTRADOR, linha.isAdministrador(), linha.getCodigo());
            adicionarSe(matriz, PerfilAcesso.GERENTE, linha.isGerente(), linha.getCodigo());
            adicionarSe(matriz, PerfilAcesso.OPERADOR, linha.isOperador(), linha.getCodigo());
            adicionarSe(matriz, PerfilAcesso.PORTEIRO, linha.isPorteiro(), linha.getCodigo());
            adicionarSe(matriz, PerfilAcesso.CUIDADOR, linha.isCuidador(), linha.getCodigo());
            adicionarSe(matriz, PerfilAcesso.RESPONSAVEL, linha.isResponsavel(), linha.getCodigo());
        }

        Long idCliente = usuarioLogado.getCliente().getId();
        webPerfilPermissaoGateway.salvarMatrizCliente(idCliente, matriz);
        log.info("Matriz de permissões web salva com sucesso para clienteId={}", idCliente);

        Map<PerfilAcesso, Set<String>> matrizAtualizada = webPermissaoUseCase.carregarMatrizEfetiva(idCliente);
        return webPermissaoUseCase.montarLinhas(matrizAtualizada);
    }

    private static void adicionarSe(
            Map<PerfilAcesso, Set<String>> matriz,
            PerfilAcesso perfil,
            boolean habilitado,
            String codigo
    ) {
        if (habilitado) {
            matriz.get(perfil).add(codigo);
        }
    }
}
