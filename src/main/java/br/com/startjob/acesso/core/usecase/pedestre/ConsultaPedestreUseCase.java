package br.com.startjob.acesso.core.usecase.pedestre;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreFiltro;
import br.com.startjob.acesso.core.domain.pedestre.PedestreResumo;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsultaPedestreUseCase {

    private final PedestreGateway pedestreGateway;
    private final UsuarioGateway usuarioGateway;

    public Page<PedestreResumo> execute(PedestreFiltro filtro, Pageable pageable) {
        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        PedestreFiltro filtroNormalizado = normalizarFiltro(filtro);

        return pedestreGateway.buscarPedestres(
                loggedUser.getCliente().getId(),
                filtroNormalizado,
                pageable
        );
    }

    public Pedestre buscarPorId(Long id) {
        if (id == null) {
            throw new BusinessException("Identificador do pedestre não informado.");
        }

        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        Pedestre pedestre = pedestreGateway.findById(id)
                .orElseThrow(() -> new BusinessException("Pedestre não encontrado."));

        if (Boolean.TRUE.equals(pedestre.getRemovido())) {
            throw new BusinessException("Pedestre não encontrado.");
        }

        if (pedestre.getCliente() == null || !pedestre.getCliente().getId().equals(loggedUser.getCliente().getId())) {
            throw new BusinessException("O pedestre não pertence ao seu cliente.");
        }

        pedestre.setSenha(null);
        return pedestre;
    }

    private PedestreFiltro normalizarFiltro(PedestreFiltro filtro) {
        if (filtro == null) {
            return new PedestreFiltro();
        }
        String cpfNormalizado = null;
        if (filtro.getCpf() != null && !filtro.getCpf().isBlank()) {
            String digitos = filtro.getCpf().replaceAll("\\D", "");
            cpfNormalizado = digitos.isEmpty() ? trimToNull(filtro.getCpf()) : digitos;
        }

        return PedestreFiltro.builder()
                .matricula(trimToNull(filtro.getMatricula()))
                .codigoCartaoAcesso(trimToNull(filtro.getCodigoCartaoAcesso()))
                .nome(trimToNull(filtro.getNome()))
                .contato(trimToNull(filtro.getContato()))
                .cpf(cpfNormalizado)
                .rg(trimToNull(filtro.getRg()))
                .tipo(filtro.getTipo())
                .idEmpresa(filtro.getIdEmpresa())
                .idDepartamento(filtro.getIdDepartamento())
                .idCentroCusto(filtro.getIdCentroCusto())
                .idCargo(filtro.getIdCargo())
                .uuidLocal(trimToNull(filtro.getUuidLocal()))
                .status(filtro.getStatus())
                .possuiFoto(filtro.getPossuiFoto())
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
