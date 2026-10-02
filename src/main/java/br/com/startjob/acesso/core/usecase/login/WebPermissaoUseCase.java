package br.com.startjob.acesso.core.usecase.login;

import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.core.domain.permissao.WebPermissaoMatriz;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class WebPermissaoUseCase {

    private final WebPerfilPermissaoGateway webPerfilPermissaoGateway;

    private static void incluirPermissoesNovasAusentes(
            PerfilAcesso perfil,
            Set<String> habilitadas,
            Set<String> presentesNaMatriz
    ) {
        if (perfil == null || habilitadas == null) {
            return;
        }
        Set<String> padrao = WebPermissaoMatriz.codigosPadrao(perfil);
        for (WebPermissao permissao : WebPermissao.values()) {
            String codigo = permissao.getCodigo();
            if (!presentesNaMatriz.contains(codigo) && padrao.contains(codigo)) {
                habilitadas.add(codigo);
            }
        }
    }

    @Transactional(readOnly = true)
    public Set<String> resolverPermissoesWeb(Usuario usuario) {
        if (usuario == null || usuario.getPerfil() == null || usuario.getCliente() == null) {
            return new HashSet<>();
        }
        Long idCliente = usuario.getCliente().getId();
        if (webPerfilPermissaoGateway.countByClienteAtivo(idCliente) > 0) {
            List<WebPerfilPermissao> lista = webPerfilPermissaoGateway.findByClienteAndPerfil(idCliente, usuario.getPerfil());
            Set<String> codigos = new HashSet<>();
            Set<String> presentes = new HashSet<>();
            for (WebPerfilPermissao row : lista) {
                presentes.add(row.getCodigoPermissao());
                if (Boolean.TRUE.equals(row.getHabilitado())) {
                    codigos.add(row.getCodigoPermissao());
                }
            }
            incluirPermissoesNovasAusentes(usuario.getPerfil(), codigos, presentes);
            return codigos;
        }
        return WebPermissaoMatriz.codigosPadrao(usuario.getPerfil());
    }
}
