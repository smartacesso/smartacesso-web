package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RegraGateway {

    Page<Regra> buscarRegras(
            Long idCliente,
            String nome,
            Long idEmpresa,
            String nomeEmpresa,
            TipoPedestre tipoPedestre,
            TipoRegra tipo,
            Pageable pageable
    );

    Optional<Regra> findById(Long id);

    Regra save(Regra regra);
}
