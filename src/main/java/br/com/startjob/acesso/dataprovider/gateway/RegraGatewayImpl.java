package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.domain.regra.Regra;
import br.com.startjob.acesso.core.enumeration.TipoPedestre;
import br.com.startjob.acesso.core.enumeration.TipoRegra;
import br.com.startjob.acesso.core.gateway.RegraGateway;
import br.com.startjob.acesso.dataprovider.entity.HorarioEntity;
import br.com.startjob.acesso.dataprovider.entity.RegraEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.HorarioMapper;
import br.com.startjob.acesso.dataprovider.mapper.RegraMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
import br.com.startjob.acesso.dataprovider.repository.RegraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RegraGatewayImpl implements RegraGateway {

    private final RegraRepository regraRepository;
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final RegraMapper regraMapper;
    private final HorarioMapper horarioMapper;

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
            Optional<RegraEntity> existingOpt = regraRepository.findByIdComplete(regra.getId());
            if (existingOpt.isPresent()) {
                var existing = existingOpt.get();
                existing.setRemovido(regra.getRemovido() != null ? regra.getRemovido() : false);
                existing.setDataRemovido(regra.getDataRemovido());
                existing.setNome(regra.getNome());
                existing.setStatus(regra.getStatus());
                existing.setValidade(regra.getValidade());
                existing.setTipo(regra.getTipo());
                existing.setTipoPedestre(regra.getTipoPedestre());
                existing.setDescricao(regra.getDescricao());
                existing.setDataInicioPeriodo(regra.getDataInicioPeriodo());
                existing.setDataFimPeriodo(regra.getDataFimPeriodo());
                existing.setTipoEscala(regra.getTipoEscala());
                existing.setPermiteBloqueio(regra.getPermiteBloqueio());
                existing.setHorarioInicioTurno(regra.getHorarioInicioTurno());
                existing.setQtdeDeCreditos(regra.getQtdeDeCreditos());
                existing.setDiasValidadeCredito(regra.getDiasValidadeCredito());
                existing.setIdTemplate(regra.getIdTemplate());
                existing.setIdPlano(regra.getIdPlano());
                existing.setIdEscala(regra.getIdEscala());

                if (regra.getEmpresa() != null && regra.getEmpresa().getId() != null) {
                    if (existing.getEmpresa() == null || !existing.getEmpresa().getId().equals(regra.getEmpresa().getId())) {
                        existing.setEmpresa(empresaRepository.getReferenceById(regra.getEmpresa().getId()));
                    }
                } else {
                    existing.setEmpresa(null);
                }

                if (existing.getHorarios() == null) {
                    existing.setHorarios(new ArrayList<>());
                }

                if (regra.getHorarios() != null) {
                    syncHorarios(existing, regra.getHorarios());
                } else {
                    for (HorarioEntity existingH : existing.getHorarios()) {
                        existingH.setRemovido(true);
                        existingH.setDataRemovido(LocalDateTime.now());
                    }
                }

                var saved = regraRepository.save(existing);
                var domain = regraMapper.toDomain(saved, new CycleAvoidingMappingContext());
                filterActiveHorarios(domain);
                return domain;
            }
        }

        var entity = regraMapper.toEntity(regra, new CycleAvoidingMappingContext());
        if (regra.getEmpresa() != null && regra.getEmpresa().getId() != null) {
            entity.setEmpresa(empresaRepository.getReferenceById(regra.getEmpresa().getId()));
        } else {
            entity.setEmpresa(null);
        }
        if (regra.getCliente() != null && regra.getCliente().getId() != null) {
            entity.setCliente(clienteRepository.getReferenceById(regra.getCliente().getId()));
        }
        if (entity.getHorarios() != null) {
            entity.getHorarios().forEach(h -> {
                h.setRegra(entity);
                if (h.getRemovido() == null) h.setRemovido(false);
            });
        }
        if (entity.getRemovido() == null) {
            entity.setRemovido(false);
        }

        var saved = regraRepository.save(entity);
        var domain = regraMapper.toDomain(saved, new CycleAvoidingMappingContext());
        filterActiveHorarios(domain);
        return domain;
    }

    private void syncHorarios(RegraEntity existing, List<Horario> incomingList) {
        Set<Long> incomingIds = incomingList.stream()
                .map(Horario::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (HorarioEntity existingH : existing.getHorarios()) {
            if (!incomingIds.contains(existingH.getId())) {
                existingH.setRemovido(true);
                existingH.setDataRemovido(LocalDateTime.now());
            }
        }

        for (Horario incoming : incomingList) {
            if (incoming.getId() == null) {
                HorarioEntity newH = horarioMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newH.setRegra(existing);
                newH.setRemovido(false);
                existing.getHorarios().add(newH);
            } else {
                existing.getHorarios().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingH -> {
                            existingH.setNome(incoming.getNome());
                            existingH.setStatus(incoming.getStatus());
                            existingH.setDiasSemana(incoming.getDiasSemana());
                            existingH.setHorarioInicio(incoming.getHorarioInicio());
                            existingH.setHorarioFim(incoming.getHorarioFim());
                            existingH.setQtdeDeCreditos(incoming.getQtdeDeCreditos());
                            existingH.setIdPedestreRegra(incoming.getIdPedestreRegra());
                            existingH.setIdHorarioSenior(incoming.getIdHorarioSenior());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingH.setRemovido(true);
                                existingH.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingH.setRemovido(false);
                                existingH.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void filterActiveHorarios(Regra domain) {
        if (domain == null) return;
        if (domain.getHorarios() != null) {
            domain.setHorarios(domain.getHorarios().stream()
                    .filter(h -> h.getRemovido() == null || !h.getRemovido())
                    .toList());
        }
    }
}
