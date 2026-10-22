package br.com.startjob.acesso.core.usecase.login;

import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.core.domain.permissao.WebPermissaoLinha;
import br.com.startjob.acesso.core.domain.permissao.WebPermissaoMatriz;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class WebPermissaoUseCase {

    private final WebPerfilPermissaoGateway webPerfilPermissaoGateway;

    public static void incluirPermissoesNovasAusentes(
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

    public Map<PerfilAcesso, Set<String>> carregarMatrizPadrao() {
        Map<PerfilAcesso, Set<String>> matriz = new EnumMap<>(PerfilAcesso.class);
        for (PerfilAcesso perfil : PerfilAcesso.values()) {
            matriz.put(perfil, new HashSet<>(WebPermissaoMatriz.codigosPadrao(perfil)));
        }
        return matriz;
    }

    @Transactional(readOnly = true)
    public Map<PerfilAcesso, Set<String>> carregarMatrizEfetiva(Long idCliente) {
        Map<PerfilAcesso, Set<String>> matriz = carregarMatrizPadrao();
        if (idCliente == null || webPerfilPermissaoGateway.countByClienteAtivo(idCliente) <= 0) {
            return matriz;
        }

        List<WebPerfilPermissao> rows = webPerfilPermissaoGateway.findByClienteAtivo(idCliente);
        for (PerfilAcesso perfil : PerfilAcesso.values()) {
            matriz.put(perfil, new HashSet<>());
        }
        if (rows != null) {
            Map<PerfilAcesso, Set<String>> presentes = new EnumMap<>(PerfilAcesso.class);
            for (WebPerfilPermissao row : rows) {
                if (row.getPerfil() == null) {
                    continue;
                }
                presentes.computeIfAbsent(row.getPerfil(), p -> new HashSet<>()).add(row.getCodigoPermissao());
                Set<String> set = matriz.computeIfAbsent(row.getPerfil(), p -> new HashSet<>());
                if (Boolean.TRUE.equals(row.getHabilitado())) {
                    set.add(row.getCodigoPermissao());
                }
            }
            for (PerfilAcesso perfil : PerfilAcesso.values()) {
                incluirPermissoesNovasAusentes(
                        perfil,
                        matriz.get(perfil),
                        presentes.getOrDefault(perfil, Set.of())
                );
            }
        }
        return matriz;
    }

    public List<WebPermissaoLinha> montarLinhas(Map<PerfilAcesso, Set<String>> matriz) {
        List<WebPermissaoLinha> linhas = new ArrayList<>();
        for (WebPermissao permissao : WebPermissao.values()) {
            linhas.add(WebPermissaoLinha.builder()
                    .codigo(permissao.getCodigo())
                    .descricao(permissao.getDescricao())
                    .administrador(contem(matriz, PerfilAcesso.ADMINISTRADOR, permissao))
                    .gerente(contem(matriz, PerfilAcesso.GERENTE, permissao))
                    .operador(contem(matriz, PerfilAcesso.OPERADOR, permissao))
                    .porteiro(contem(matriz, PerfilAcesso.PORTEIRO, permissao))
                    .cuidador(contem(matriz, PerfilAcesso.CUIDADOR, permissao))
                    .responsavel(contem(matriz, PerfilAcesso.RESPONSAVEL, permissao))
                    .build());
        }
        return linhas;
    }

    private static boolean contem(Map<PerfilAcesso, Set<String>> matriz, PerfilAcesso perfil, WebPermissao permissao) {
        if (matriz == null) {
            return false;
        }
        Set<String> set = matriz.get(perfil);
        return set != null && set.contains(permissao.getCodigo());
    }
}
