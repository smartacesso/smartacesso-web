package br.com.startjob.acesso.core.usecase.correspondencia;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.correspondencia.CorrespondenciaFiltro;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.CorrespondenciaGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Set;

@RequiredArgsConstructor
@Component
public class ConsultaCorrespondenciaUseCase {

    private final CorrespondenciaGateway correspondenciaGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    public Page<Correspondencia> execute(CorrespondenciaFiltro filtro, Pageable pageable) {
        Usuario loggedUser = obterUsuarioLogadoComPermissao();

        CorrespondenciaFiltro filtroNormalizado = normalizarFiltro(filtro);

        return correspondenciaGateway.buscarCorrespondencias(
                loggedUser.getCliente().getId(),
                filtroNormalizado,
                pageable
        );
    }

    public Correspondencia buscarPorId(Long id) {
        if (id == null) {
            throw new BusinessException("Identificador da correspondência não informado.");
        }

        Usuario loggedUser = obterUsuarioLogadoComPermissao();

        Correspondencia correspondencia = correspondenciaGateway.findById(id)
                .orElseThrow(() -> new BusinessException("Correspondência não encontrada."));

        if (Boolean.TRUE.equals(correspondencia.getRemovido())) {
            throw new BusinessException("Correspondência não encontrada.");
        }

        if (correspondencia.getCliente() == null
                || !correspondencia.getCliente().getId().equals(loggedUser.getCliente().getId())) {
            throw new BusinessException("A correspondência não pertence ao seu cliente.");
        }

        return correspondencia;
    }

    private Usuario obterUsuarioLogadoComPermissao() {
        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(loggedUser);
        if (!permissoes.contains(WebPermissao.CORRESPONDENCIA_VER.getCodigo())
                && !permissoes.contains(WebPermissao.CORRESPONDENCIA_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para consultar correspondências.");
        }

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }

        return loggedUser;
    }

    private CorrespondenciaFiltro normalizarFiltro(CorrespondenciaFiltro filtro) {
        if (filtro == null) {
            return new CorrespondenciaFiltro();
        }

        String confirmaRetirada = trimToNull(filtro.getConfirmaRetirada());
        if (confirmaRetirada != null) {
            if ("TRUE".equalsIgnoreCase(confirmaRetirada) || "S".equalsIgnoreCase(confirmaRetirada)) {
                confirmaRetirada = "S";
            } else if ("FALSE".equalsIgnoreCase(confirmaRetirada) || "N".equalsIgnoreCase(confirmaRetirada)) {
                confirmaRetirada = "N";
            } else {
                confirmaRetirada = confirmaRetirada.toUpperCase();
            }
        }

        String tipo = trimToNull(filtro.getTipo());
        if (tipo != null) {
            tipo = tipo.toUpperCase();
        }

        return CorrespondenciaFiltro.builder()
                .nomeDestinatario(trimToNull(filtro.getNomeDestinatario()))
                .codigoRastreio(trimToNull(filtro.getCodigoRastreio()))
                .tipo(tipo)
                .confirmaRetirada(confirmaRetirada)
                .dataRecebimento(filtro.getDataRecebimento())
                .build();
    }

    private String trimToNull(String valor) {
        if (valor == null) {
            return null;
        }
        String trimmed = valor.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
