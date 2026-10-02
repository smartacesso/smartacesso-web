package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.plano.Plano;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanoGateway {

    List<Plano> findAtivos(Long clienteId, LocalDateTime agora, Pageable pageable);

}
