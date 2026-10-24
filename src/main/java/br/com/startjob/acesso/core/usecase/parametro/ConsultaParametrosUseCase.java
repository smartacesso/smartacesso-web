package br.com.startjob.acesso.core.usecase.parametro;

import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.domain.parametro.ParametroItem;
import br.com.startjob.acesso.core.domain.parametro.SecaoParametroGrupo;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.enumeration.SecaoParametro;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Component
public class ConsultaParametrosUseCase {

    private final UsuarioGateway usuarioGateway;
    private final ParametroGateway parametroGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional(readOnly = true)
    public List<SecaoParametroGrupo> execute() {
        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_PARAMETROS_VER.getCodigo())
                && !permissoes.contains(WebPermissao.CONFIG_PARAMETROS_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para visualizar os parâmetros do sistema.");
        }

        return montarSecoesCliente(usuarioLogado.getCliente().getId());
    }

    public List<SecaoParametroGrupo> montarSecoesCliente(Long clienteId) {
        Map<String, String> valoresPersistidos = carregarMapaValoresCliente(clienteId);

        Map<SecaoParametro, List<ParametroItem>> agrupado = new EnumMap<>(SecaoParametro.class);
        for (SecaoParametro secao : SecaoParametro.values()) {
            agrupado.put(secao, new ArrayList<>());
        }

        for (ParametroCatalogo spec : ParametroCatalogo.values()) {
            if (!spec.isVisivel()) {
                continue;
            }
            String valorBruto = valoresPersistidos.getOrDefault(
                    spec.getNomePersistido(),
                    spec.getValorPadraoPersistido()
            );
            ParametroItem item = ParametroItem.builder()
                    .codigo(spec.getCodigo())
                    .nome(spec.getNomePersistido())
                    .rotulo(spec.getRotulo())
                    .secao(spec.getSecao())
                    .tipo(spec.getTipo())
                    .valor(spec.converterPersistidoParaTipado(valorBruto))
                    .valorPadrao(spec.getValorPadraoTipado())
                    .min(spec.getMin())
                    .max(spec.getMax())
                    .opcoes(spec.getOpcoes())
                    .build();
            agrupado.get(spec.getSecao()).add(item);
        }

        Comparator<ParametroItem> porRotulo = Comparator.comparing(
                item -> item.getRotulo() == null ? "" : item.getRotulo(),
                String.CASE_INSENSITIVE_ORDER
        );

        List<SecaoParametroGrupo> secoes = new ArrayList<>();
        for (SecaoParametro secao : SecaoParametro.values()) {
            List<ParametroItem> itens = agrupado.get(secao);
            if (itens == null || itens.isEmpty()) {
                continue;
            }
            if (secao != SecaoParametro.MENSAGENS && secao != SecaoParametro.MENSAGENS_AVANCADAS) {
                itens.sort(porRotulo);
            }
            secoes.add(SecaoParametroGrupo.builder()
                    .secao(secao)
                    .titulo(secao.getTitulo())
                    .parametros(List.copyOf(itens))
                    .build());
        }

        return secoes;
    }

    public Map<String, String> carregarMapaValoresCliente(Long clienteId) {
        List<Parametro> existentes = parametroGateway.findAllByCliente(clienteId);
        Map<String, String> mapa = new LinkedHashMap<>();
        if (existentes != null) {
            for (Parametro p : existentes) {
                if (p == null || p.getNome() == null) {
                    continue;
                }
                String nomeNormalizado = ParametroCatalogo.normalizarNomeLegado(p.getNome());
                if (ParametroCatalogo.isDepreciado(nomeNormalizado)) {
                    continue;
                }
                mapa.putIfAbsent(nomeNormalizado, p.getValor());
            }
        }
        return mapa;
    }
}
