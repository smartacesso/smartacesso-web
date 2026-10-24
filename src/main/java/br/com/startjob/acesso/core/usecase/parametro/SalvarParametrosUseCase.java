package br.com.startjob.acesso.core.usecase.parametro;

import br.com.startjob.acesso.core.domain.parametro.AtualizarParametroInput;
import br.com.startjob.acesso.core.domain.parametro.SecaoParametroGrupo;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarParametrosUseCase {

    private final UsuarioGateway usuarioGateway;
    private final ParametroGateway parametroGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;
    private final ConsultaParametrosUseCase consultaParametrosUseCase;

    @Transactional
    public List<SecaoParametroGrupo> execute(List<AtualizarParametroInput> entradas) {
        if (entradas == null || entradas.isEmpty()) {
            throw new BusinessException("Dados dos parâmetros não informados.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.CONFIG_PARAMETROS_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para editar os parâmetros do sistema.");
        }

        Long clienteId = usuarioLogado.getCliente().getId();
        Map<String, String> existentes = consultaParametrosUseCase.carregarMapaValoresCliente(clienteId);

        Map<String, String> valoresParaPersistir = new LinkedHashMap<>();
        for (ParametroCatalogo spec : ParametroCatalogo.values()) {
            String valorAtual = existentes.get(spec.getNomePersistido());
            valoresParaPersistir.put(
                    spec.getNomePersistido(),
                    spec.normalizarValorPersistidoLeitura(valorAtual)
            );
        }

        for (AtualizarParametroInput entrada : entradas) {
            if (entrada == null || entrada.getCodigo() == null || entrada.getCodigo().isBlank()) {
                throw new BusinessException("Código do parâmetro é obrigatório.");
            }
            ParametroCatalogo spec = ParametroCatalogo.porCodigoOuNome(entrada.getCodigo());
            if (spec == null) {
                throw new BusinessException("Parâmetro inválido ou desconhecido: " + entrada.getCodigo());
            }
            String valorFormatado = spec.validarEFormatarParaPersistencia(entrada.getValor());
            valoresParaPersistir.put(spec.getNomePersistido(), valorFormatado);
        }

        parametroGateway.salvarParametrosCliente(clienteId, valoresParaPersistir);
        log.info("Parâmetros gerais salvos com sucesso para clienteId={}", clienteId);

        return consultaParametrosUseCase.montarSecoesCliente(clienteId);
    }
}
