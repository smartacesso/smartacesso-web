package br.com.startjob.acesso.application.identity;

import br.com.startjob.acesso.domain.entity.UsuarioEntity;
import br.com.startjob.acesso.domain.entity.WebPerfilPermissaoEntity;
import br.com.startjob.acesso.domain.enumeration.WebPermissao;
import br.com.startjob.acesso.domain.repository.WebPerfilPermissaoRepository;
import br.com.startjob.acesso.domain.web.WebPermissaoMatriz;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class WebPermissaoService {

    private final WebPerfilPermissaoRepository repository;

    public WebPermissaoService(WebPerfilPermissaoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Set<String> resolverPermissoesWeb(UsuarioEntity usuario) {
        if (usuario == null || usuario.getPerfil() == null || usuario.getCliente() == null) {
            return new HashSet<>();
        }
        Long idCliente = usuario.getCliente().getId();
        if (repository.countByClienteAtivo(idCliente) > 0) {
            List<WebPerfilPermissaoEntity> lista = repository.findByClienteAndPerfil(idCliente, usuario.getPerfil());
            Set<String> codigos = new HashSet<>();
            Set<String> presentes = new HashSet<>();
            for (WebPerfilPermissaoEntity row : lista) {
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

    private static void incluirPermissoesNovasAusentes(
            br.com.startjob.acesso.domain.enumeration.PerfilAcesso perfil,
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
}
