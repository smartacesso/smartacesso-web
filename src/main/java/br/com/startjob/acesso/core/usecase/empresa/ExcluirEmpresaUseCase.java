package br.com.startjob.acesso.core.usecase.empresa;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
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
public class ExcluirEmpresaUseCase {

    private final EmpresaGateway empresaGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public void execute(Long idEmpresa) {
        if (idEmpresa == null) {
            throw new BusinessException("Identificador da empresa não informado.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.EMPRESA_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para excluir empresas.");
        }

        Empresa empresaExistente = empresaGateway.findById(idEmpresa)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada."));

        if (Boolean.TRUE.equals(empresaExistente.getRemovido())) {
            throw new BusinessException("Empresa não encontrada.");
        }

        Long idClienteLogado = usuarioLogado.getCliente() != null ? usuarioLogado.getCliente().getId() : null;
        if (empresaExistente.getCliente() == null || !empresaExistente.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("A empresa não pertence ao seu cliente.");
        }

        empresaExistente.setRemovido(true);
        empresaExistente.setDataRemovido(LocalDateTime.now());

        empresaGateway.save(empresaExistente);
        log.info("Empresa id={} excluída com sucesso (exclusão lógica).", idEmpresa);
    }
}
