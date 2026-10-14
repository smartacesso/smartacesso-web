package br.com.startjob.acesso.core.usecase.regra;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ConsultaRegraUseCase {

    private final RegraGateway regraGateway;
    private final UsuarioGateway usuarioGateway;

    public Page<Regra> execute(
            String nome,
            Long idEmpresa,
            String nomeEmpresa,
            TipoPedestre tipoPedestre,
            TipoRegra tipo,
            Pageable pageable) {

        Usuario loggedUser = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário logado não encontrado"));

        if (loggedUser.getCliente() == null || loggedUser.getCliente().getId() == null) {
            throw new BusinessException("Usuário logado não possui cliente associado");
        }

        return regraGateway.buscarRegras(
                loggedUser.getCliente().getId(),
                nome,
                idEmpresa,
                nomeEmpresa,
                tipoPedestre,
                tipo,
                pageable
        );
    }
}
