package br.com.startjob.acesso.core.usecase.empresa;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
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

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarEmpresaUseCase {

    private final EmpresaGateway empresaGateway;
    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;

    @Transactional
    public Empresa execute(Empresa empresa) {
        if (empresa == null) {
            throw new BusinessException("Dados da empresa não informados.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.EMPRESA_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para salvar empresas.");
        }

        if (usuarioLogado.getCliente() == null || usuarioLogado.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado.");
        }
        Long idClienteLogado = usuarioLogado.getCliente().getId();

        validarTelefones(empresa);

        boolean novaEmpresa = (empresa.getId() == null);

        if (novaEmpresa) {
            empresa.setCliente(usuarioLogado.getCliente());
            empresa.setRemovido(false);

            validarNovosFilhos(empresa);
        } else {
            Empresa empresaExistente = empresaGateway.findById(empresa.getId())
                    .orElseThrow(() -> new BusinessException("Empresa não encontrada."));

            if (Boolean.TRUE.equals(empresaExistente.getRemovido())) {
                throw new BusinessException("Empresa não encontrada.");
            }

            if (empresaExistente.getCliente() == null || !empresaExistente.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("A empresa não pertence ao seu cliente.");
            }

            empresa.setCliente(empresaExistente.getCliente());
            empresa.setDataCriacao(empresaExistente.getDataCriacao());
            empresa.setRemovido(false);

            validarFilhosParaEdicao(empresa, empresaExistente);
        }

        Empresa salva = empresaGateway.save(empresa);
        log.info("Empresa id={} salva com sucesso.", salva.getId());
        return salva;
    }

    private void validarTelefones(Empresa empresa) {
        boolean hasTelefone = empresa.getTelefone() != null && !empresa.getTelefone().isBlank();
        boolean hasCelular = empresa.getCelular() != null && !empresa.getCelular().isBlank();
        if (!hasTelefone && !hasCelular) {
            throw new BusinessException("msg.add.telefone");
        }
    }

    private void validarNovosFilhos(Empresa empresa) {
        if (empresa.getDepartamentos() != null) {
            for (Departamento dep : empresa.getDepartamentos()) {
                if (dep.getId() != null) {
                    throw new BusinessException("Não é permitido informar identificador para novos departamentos.");
                }
                if (dep.getNome() == null || dep.getNome().isBlank()) {
                    throw new BusinessException("Nome do departamento é obrigatório.");
                }
                if (dep.getStatus() == null) {
                    throw new BusinessException("Status do departamento é obrigatório.");
                }
            }
        }

        if (empresa.getCentros() != null) {
            for (CentroCusto centro : empresa.getCentros()) {
                if (centro.getId() != null) {
                    throw new BusinessException("Não é permitido informar identificador para novos centros de custo.");
                }
                if (centro.getNome() == null || centro.getNome().isBlank()) {
                    throw new BusinessException("Nome do centro de custo é obrigatório.");
                }
                if (centro.getStatus() == null) {
                    throw new BusinessException("Status do centro de custo é obrigatório.");
                }
            }
        }

        if (empresa.getCargos() != null) {
            for (Cargo cargo : empresa.getCargos()) {
                if (cargo.getId() != null) {
                    throw new BusinessException("Não é permitido informar identificador para novos cargos.");
                }
                if (cargo.getNome() == null || cargo.getNome().isBlank()) {
                    throw new BusinessException("Nome do cargo é obrigatório.");
                }
                if (cargo.getStatus() == null) {
                    throw new BusinessException("Status do cargo é obrigatório.");
                }
            }
        }
    }

    private void validarFilhosParaEdicao(Empresa empresa, Empresa empresaExistente) {
        if (empresa.getDepartamentos() != null) {
            Set<Long> existingDepIds = empresaExistente.getDepartamentos() == null
                    ? Set.of()
                    : empresaExistente.getDepartamentos().stream()
                            .filter(d -> d.getRemovido() == null || !d.getRemovido())
                            .map(Departamento::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

            for (Departamento dep : empresa.getDepartamentos()) {
                if (dep.getId() != null && !existingDepIds.contains(dep.getId())) {
                    throw new BusinessException("Departamento informado não pertence a esta empresa.");
                }
                if (dep.getNome() == null || dep.getNome().isBlank()) {
                    throw new BusinessException("Nome do departamento é obrigatório.");
                }
                if (dep.getStatus() == null) {
                    throw new BusinessException("Status do departamento é obrigatório.");
                }
            }
        }

        if (empresa.getCentros() != null) {
            Set<Long> existingCentroIds = empresaExistente.getCentros() == null
                    ? Set.of()
                    : empresaExistente.getCentros().stream()
                            .filter(c -> c.getRemovido() == null || !c.getRemovido())
                            .map(CentroCusto::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

            for (CentroCusto centro : empresa.getCentros()) {
                if (centro.getId() != null && !existingCentroIds.contains(centro.getId())) {
                    throw new BusinessException("Centro de custo informado não pertence a esta empresa.");
                }
                if (centro.getNome() == null || centro.getNome().isBlank()) {
                    throw new BusinessException("Nome do centro de custo é obrigatório.");
                }
                if (centro.getStatus() == null) {
                    throw new BusinessException("Status do centro de custo é obrigatório.");
                }
            }
        }

        if (empresa.getCargos() != null) {
            Set<Long> existingCargoIds = empresaExistente.getCargos() == null
                    ? Set.of()
                    : empresaExistente.getCargos().stream()
                            .filter(c -> c.getRemovido() == null || !c.getRemovido())
                            .map(Cargo::getId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

            for (Cargo cargo : empresa.getCargos()) {
                if (cargo.getId() != null && !existingCargoIds.contains(cargo.getId())) {
                    throw new BusinessException("Cargo informado não pertence a esta empresa.");
                }
                if (cargo.getNome() == null || cargo.getNome().isBlank()) {
                    throw new BusinessException("Nome do cargo é obrigatório.");
                }
                if (cargo.getStatus() == null) {
                    throw new BusinessException("Status do cargo é obrigatório.");
                }
            }
        }
    }
}
