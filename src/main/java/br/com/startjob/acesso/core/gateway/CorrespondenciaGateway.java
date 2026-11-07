package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.correspondencia.Correspondencia;
import br.com.startjob.acesso.core.domain.correspondencia.CorrespondenciaFiltro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CorrespondenciaGateway {

    Page<Correspondencia> buscarCorrespondencias(Long idCliente, CorrespondenciaFiltro filtro, Pageable pageable);

    Optional<Correspondencia> findById(Long id);

    Correspondencia save(Correspondencia correspondencia);

}
