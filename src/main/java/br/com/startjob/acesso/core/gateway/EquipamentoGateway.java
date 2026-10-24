package br.com.startjob.acesso.core.gateway;

import br.com.startjob.acesso.core.domain.equipamento.Equipamento;

import java.util.Optional;

public interface EquipamentoGateway {

    Optional<Equipamento> findById(Long id);

}
