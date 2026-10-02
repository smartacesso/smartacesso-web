package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.pedestre.Pedestre;

import java.util.Optional;

public interface PedestreGateway {

    Optional<Pedestre> findByLoginOtimizado(String login, String unidade);
    
}
