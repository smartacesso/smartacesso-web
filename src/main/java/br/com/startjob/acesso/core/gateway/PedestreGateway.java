package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;

import java.util.Optional;

public interface PedestreGateway {

    Optional<Pedestre> findByLoginOtimizado(String login, String unidade);

    Optional<Pedestre> findById(Long id);

    Pedestre save(Pedestre pedestre);

    boolean existsAtivoByCpfAndCliente(String cpf, Long idCliente, Long excludeId);

    boolean existsAtivoByRgAndCliente(String rg, Long idCliente, Long excludeId);

    boolean existsAtivoByMatriculaAndCliente(String matricula, Long idCliente, Long excludeId);

    boolean existsAtivoByCartaoNormalizadoAndCliente(String cartaoNormalizado, Long idCliente, Long excludeId);

    Optional<String> findUltimaMatriculaByCliente(Long idCliente);

}
