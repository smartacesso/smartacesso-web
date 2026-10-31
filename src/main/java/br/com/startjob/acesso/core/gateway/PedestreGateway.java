package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreFiltro;
import br.com.startjob.acesso.core.domain.pedestre.PedestreResumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PedestreGateway {

    Page<PedestreResumo> buscarPedestres(Long idCliente, PedestreFiltro filtro, Pageable pageable);

    Optional<Pedestre> findByLoginOtimizado(String login, String unidade);

    Optional<Pedestre> findById(Long id);

    Pedestre save(Pedestre pedestre);

    boolean existsAtivoByCpfAndCliente(String cpf, Long idCliente, Long excludeId);

    boolean existsAtivoByRgAndCliente(String rg, Long idCliente, Long excludeId);

    boolean existsAtivoByMatriculaAndCliente(String matricula, Long idCliente, Long excludeId);

    boolean existsAtivoByCartaoNormalizadoAndCliente(String cartaoNormalizado, Long idCliente, Long excludeId);

    Optional<String> findUltimaMatriculaByCliente(Long idCliente);

}
