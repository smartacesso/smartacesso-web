package br.com.startjob.acesso.core.usecase.empresa;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsultaEmpresaUseCase {

    private final EmpresaGateway empresaGateway;
    private final UsuarioGateway usuarioGateway;

    public Page<Empresa> execute(String nome, String cnpj, Pageable pageable) {
        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        return empresaGateway.buscarEmpresas(loggedUser.getCliente().getId(), nome, cnpj, pageable);
    }

    public Empresa buscarPorId(Long id) {
        if (id == null) {
            throw new BusinessException("Identificador da empresa não informado.");
        }

        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        Empresa empresa = empresaGateway.findById(id)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada."));

        if (Boolean.TRUE.equals(empresa.getRemovido())) {
            throw new BusinessException("Empresa não encontrada.");
        }

        if (empresa.getCliente() == null || !empresa.getCliente().getId().equals(loggedUser.getCliente().getId())) {
            throw new BusinessException("A empresa não pertence ao seu cliente.");
        }

        return empresa;
    }
}
