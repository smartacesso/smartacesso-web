package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
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
        if (regra.getId() != null) {
            Optional<RegraEntity> existingOpt = regraRepository.findById(regra.getId());
            if (existingOpt.isPresent()) {
                var existing = existingOpt.get();
                existing.setRemovido(regra.getRemovido());
                existing.setDataRemovido(regra.getDataRemovido());
                if (regra.getNome() != null) existing.setNome(regra.getNome());
                if (regra.getStatus() != null) existing.setStatus(regra.getStatus());
                if (regra.getValidade() != null) existing.setValidade(regra.getValidade());
                if (regra.getTipo() != null) existing.setTipo(regra.getTipo());
                if (regra.getTipoPedestre() != null) existing.setTipoPedestre(regra.getTipoPedestre());
                if (regra.getDescricao() != null) existing.setDescricao(regra.getDescricao());
                if (regra.getDataInicioPeriodo() != null) existing.setDataInicioPeriodo(regra.getDataInicioPeriodo());
                if (regra.getDataFimPeriodo() != null) existing.setDataFimPeriodo(regra.getDataFimPeriodo());
                if (regra.getTipoEscala() != null) existing.setTipoEscala(regra.getTipoEscala());
                if (regra.getPermiteBloqueio() != null) existing.setPermiteBloqueio(regra.getPermiteBloqueio());
                if (regra.getHorarioInicioTurno() != null) existing.setHorarioInicioTurno(regra.getHorarioInicioTurno());
                if (regra.getQtdeDeCreditos() != null) existing.setQtdeDeCreditos(regra.getQtdeDeCreditos());
                if (regra.getDiasValidadeCredito() != null) existing.setDiasValidadeCredito(regra.getDiasValidadeCredito());
                if (regra.getIdTemplate() != null) existing.setIdTemplate(regra.getIdTemplate());
                if (regra.getIdPlano() != null) existing.setIdPlano(regra.getIdPlano());
                if (regra.getIdEscala() != null) existing.setIdEscala(regra.getIdEscala());

                var saved = regraRepository.save(existing);
                return regraMapper.toDomain(saved, new CycleAvoidingMappingContext());
            }
        }

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
