package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.RegraMapper;
import br.com.startjob.acesso.dataprovider.repository.RegraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class RegraGatewayImpl implements RegraGateway {

    private final RegraRepository regraRepository;
    private final RegraMapper regraMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Regra> buscarRegras(
            Long idCliente,
            String nome,
            Long idEmpresa,
            String nomeEmpresa,
            TipoPedestre tipoPedestre,
            TipoRegra tipo,
            Pageable pageable) {

        return regraRepository.buscarRegras(idCliente, nome, idEmpresa, nomeEmpresa, tipoPedestre, tipo, pageable)
                .map(entity -> regraMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Regra> findById(Long id) {
        return regraRepository.findByIdComplete(id)
                .map(entity -> regraMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    @Transactional
    public Regra save(Regra regra) {
        var entity = regraMapper.toEntity(regra, new CycleAvoidingMappingContext());
        if (entity.getHorarios() != null) {
            entity.getHorarios().forEach(h -> {
                h.setRegra(entity);
                if (h.getRemovido() == null) h.setRemovido(false);
            });
        }
        var saved = regraRepository.save(entity);
        return regraMapper.toDomain(saved, new CycleAvoidingMappingContext());
    }
}
