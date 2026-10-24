package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.pedestre.Pedestre;
import br.com.startjob.acesso.core.domain.pedestre.PedestreRegra;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import br.com.startjob.acesso.dataprovider.entity.HorarioEntity;
import br.com.startjob.acesso.dataprovider.entity.PedestreEntity;
import br.com.startjob.acesso.dataprovider.entity.PedestreRegraEntity;
import br.com.startjob.acesso.dataprovider.mapper.*;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
import br.com.startjob.acesso.dataprovider.repository.PedestreRepository;
import br.com.startjob.acesso.dataprovider.repository.RegraRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
public class PedestreGatewayImpl implements PedestreGateway {

    private final PedestreRepository pedestreRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final RegraRepository regraRepository;
    private final PedestreMapper pedestreMapper;
    private final PedestreRegraMapper pedestreRegraMapper;
    private final EnderecoMapper enderecoMapper;
    private final DepartamentoMapper departamentoMapper;
    private final CentroCustoMapper centroCustoMapper;
    private final CargoMapper cargoMapper;
    private final HorarioMapper horarioMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedestre> findByLoginOtimizado(String login, String unidade) {
        return pedestreRepository.findByLoginOtimizado(login, unidade)
                .map(entity -> pedestreMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedestre> findById(Long id) {
        return pedestreRepository.findByIdComplete(id)
                .map(entity -> {
                    var domain = pedestreMapper.toDomain(entity, new CycleAvoidingMappingContext());
                    filterActiveChildren(domain);
                    return domain;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAtivoByCpfAndCliente(String cpf, Long idCliente, Long excludeId) {
        return pedestreRepository.existsAtivoByCpfAndCliente(cpf, idCliente, excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAtivoByRgAndCliente(String rg, Long idCliente, Long excludeId) {
        return pedestreRepository.existsAtivoByRgAndCliente(rg, idCliente, excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAtivoByMatriculaAndCliente(String matricula, Long idCliente, Long excludeId) {
        return pedestreRepository.existsAtivoByMatriculaAndCliente(matricula, idCliente, excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAtivoByCartaoNormalizadoAndCliente(String cartaoNormalizado, Long idCliente, Long excludeId) {
        return pedestreRepository.existsAtivoByCartaoNormalizadoAndCliente(cartaoNormalizado, idCliente, excludeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> findUltimaMatriculaByCliente(Long idCliente) {
        return pedestreRepository.findMatriculasByClienteOrderByIdDesc(idCliente, PageRequest.of(0, 1))
                .stream()
                .findFirst();
    }

    @Override
    @Transactional
    public Pedestre save(Pedestre pedestre) {
        if (pedestre.getId() != null) {
            Optional<PedestreEntity> existingOpt = pedestreRepository.findByIdComplete(pedestre.getId());
            if (existingOpt.isPresent()) {
                PedestreEntity existing = existingOpt.get();
                existing.setRemovido(pedestre.getRemovido() != null ? pedestre.getRemovido() : false);
                existing.setDataRemovido(pedestre.getDataRemovido());
                existing.setNome(pedestre.getNome());
                existing.setStatus(pedestre.getStatus());
                existing.setEmail(pedestre.getEmail());
                existing.setCpf(pedestre.getCpf());
                existing.setRg(pedestre.getRg());
                existing.setTelefone(pedestre.getTelefone());
                existing.setCelular(pedestre.getCelular());
                existing.setMatricula(pedestre.getMatricula());
                existing.setCodigoCartaoAcesso(pedestre.getCodigoCartaoAcesso());
                existing.setObservacoes(pedestre.getObservacoes());
                existing.setTipoQRCode(pedestre.getTipoQRCode());
                existing.setQrCodeParaAcesso(pedestre.getQrCodeParaAcesso());
                existing.setSempreLiberado(pedestre.getSempreLiberado());
                existing.setAcessoLivre(pedestre.getAcessoLivre());
                existing.setHabilitarTeclado(pedestre.getHabilitarTeclado());
                existing.setCadastroFacialObrigatorio(pedestre.getCadastroFacialObrigatorio());
                existing.setFoto(pedestre.getFoto());
                existing.setDataAlteracaoFoto(pedestre.getDataAlteracaoFoto());
                existing.setDataNascimento(pedestre.getDataNascimento());
                existing.setTipo(pedestre.getTipo());
                existing.setGenero(pedestre.getGenero());
                existing.setEnviaSmsAoPassarNaCatraca(pedestre.getEnviaSmsAoPassarNaCatraca());
                existing.setUuidLocal(pedestre.getUuidLocal());
                existing.setEmpresaVisitada(pedestre.getEmpresaVisitada());
                existing.setLogin(pedestre.getLogin());
                existing.setSenha(pedestre.getSenha());
                existing.setPerfilApp(pedestre.getPerfilApp());
                existing.setJustificativa(pedestre.getJustificativa());
                existing.setDataInicioPeriodoAgendamento(pedestre.getDataInicioPeriodoAgendamento());
                existing.setDataFimPeriodoAgendamento(pedestre.getDataFimPeriodoAgendamento());
                existing.setAgendamentoLiberado(pedestre.getAgendamentoLiberado());

                syncEndereco(existing, pedestre.getEndereco());
                syncOrganizacional(existing, pedestre);
                syncRegras(existing, pedestre.getRegras());

                var saved = pedestreRepository.save(existing);
                var domain = pedestreMapper.toDomain(saved, new CycleAvoidingMappingContext());
                filterActiveChildren(domain);
                return domain;
            }
        }

        var entity = pedestreMapper.toEntity(pedestre, new CycleAvoidingMappingContext());
        if (pedestre.getCliente() != null && pedestre.getCliente().getId() != null) {
            entity.setCliente(clienteRepository.getReferenceById(pedestre.getCliente().getId()));
        }
        if (pedestre.getUsuario() != null && pedestre.getUsuario().getId() != null) {
            entity.setUsuario(usuarioRepository.getReferenceById(pedestre.getUsuario().getId()));
        } else {
            entity.setUsuario(null);
        }
        syncOrganizacional(entity, pedestre);

        if (entity.getEndereco() != null) {
            if (isEnderecoEmpty(pedestre.getEndereco())) {
                entity.setEndereco(null);
            } else if (entity.getEndereco().getRemovido() == null) {
                entity.getEndereco().setRemovido(false);
            }
        }

        if (entity.getRegras() != null) {
            entity.getRegras().forEach(pr -> {
                pr.setPedestre(entity);
                if (pr.getRegra() != null && pr.getRegra().getId() != null) {
                    pr.setRegra(regraRepository.getReferenceById(pr.getRegra().getId()));
                }
                if (pr.getRemovido() == null) {
                    pr.setRemovido(false);
                }
                if (pr.getHorarios() != null) {
                    pr.getHorarios().forEach(h -> {
                        h.setRegra(null);
                        if (h.getRemovido() == null) {
                            h.setRemovido(false);
                        }
                    });
                }
            });
        }

        if (entity.getRemovido() == null) {
            entity.setRemovido(false);
        }

        var saved = pedestreRepository.save(entity);
        var domain = pedestreMapper.toDomain(saved, new CycleAvoidingMappingContext());
        filterActiveChildren(domain);
        return domain;
    }

    private void syncOrganizacional(PedestreEntity target, Pedestre source) {
        if (source.getEmpresa() != null && source.getEmpresa().getId() != null) {
            target.setEmpresa(empresaRepository.getReferenceById(source.getEmpresa().getId()));
        } else {
            target.setEmpresa(null);
        }

        if (source.getEmpresaVisitadaRef() != null && source.getEmpresaVisitadaRef().getId() != null) {
            target.setEmpresaVisitadaRef(empresaRepository.getReferenceById(source.getEmpresaVisitadaRef().getId()));
        } else {
            target.setEmpresaVisitadaRef(null);
        }

        if (source.getDepartamento() != null && source.getDepartamento().getId() != null) {
            target.setDepartamento(departamentoMapper.toEntity(source.getDepartamento(), new CycleAvoidingMappingContext()));
        } else {
            target.setDepartamento(null);
        }

        if (source.getCentroCusto() != null && source.getCentroCusto().getId() != null) {
            target.setCentroCusto(centroCustoMapper.toEntity(source.getCentroCusto(), new CycleAvoidingMappingContext()));
        } else {
            target.setCentroCusto(null);
        }

        if (source.getCargo() != null && source.getCargo().getId() != null) {
            target.setCargo(cargoMapper.toEntity(source.getCargo(), new CycleAvoidingMappingContext()));
        } else {
            target.setCargo(null);
        }
    }

    private void syncEndereco(PedestreEntity existing, Endereco incoming) {
        if (incoming == null || isEnderecoEmpty(incoming)) {
            existing.setEndereco(null);
            return;
        }

        if (existing.getEndereco() != null) {
            EnderecoEntity existingEnd = existing.getEndereco();
            existingEnd.setCep(incoming.getCep());
            existingEnd.setLogradouro(incoming.getLogradouro());
            existingEnd.setNumero(incoming.getNumero());
            existingEnd.setComplemento(incoming.getComplemento());
            existingEnd.setBairro(incoming.getBairro());
            existingEnd.setCidade(incoming.getCidade());
            existingEnd.setEstado(incoming.getEstado());
            if (incoming.getRemovido() != null) {
                existingEnd.setRemovido(incoming.getRemovido());
            }
        } else {
            EnderecoEntity newEnd = enderecoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
            if (newEnd.getRemovido() == null) {
                newEnd.setRemovido(false);
            }
            existing.setEndereco(newEnd);
        }
    }

    private void syncRegras(PedestreEntity existing, List<PedestreRegra> incomingList) {
        if (existing.getRegras() == null) {
            existing.setRegras(new ArrayList<>());
        }

        if (incomingList == null || incomingList.isEmpty()) {
            for (PedestreRegraEntity existingPr : existing.getRegras()) {
                existingPr.setRemovido(true);
                existingPr.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingList.stream()
                .map(PedestreRegra::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (PedestreRegraEntity existingPr : existing.getRegras()) {
            if (!incomingIds.contains(existingPr.getId())) {
                existingPr.setRemovido(true);
                existingPr.setDataRemovido(LocalDateTime.now());
            }
        }

        for (PedestreRegra incoming : incomingList) {
            if (incoming.getId() == null) {
                PedestreRegraEntity newPr = pedestreRegraMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newPr.setPedestre(existing);
                if (incoming.getRegra() != null && incoming.getRegra().getId() != null) {
                    newPr.setRegra(regraRepository.getReferenceById(incoming.getRegra().getId()));
                }
                newPr.setRemovido(Boolean.TRUE.equals(incoming.getRemovido()));
                newPr.setDataRemovido(incoming.getDataRemovido());
                if (newPr.getHorarios() != null) {
                    newPr.getHorarios().forEach(h -> {
                        h.setRegra(null);
                        if (h.getRemovido() == null) {
                            h.setRemovido(false);
                        }
                    });
                }
                existing.getRegras().add(newPr);
            } else {
                existing.getRegras().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingPr -> {
                            if (incoming.getRegra() != null && incoming.getRegra().getId() != null) {
                                existingPr.setRegra(regraRepository.getReferenceById(incoming.getRegra().getId()));
                            }
                            existingPr.setValidade(incoming.getValidade());
                            existingPr.setQtdeDeCreditos(incoming.getQtdeDeCreditos());
                            existingPr.setQtdeTotalDeCreditos(incoming.getQtdeTotalDeCreditos());
                            existingPr.setDiasValidadeCredito(incoming.getDiasValidadeCredito());
                            existingPr.setBloqueado(incoming.getBloqueado());
                            existingPr.setDataInicioPeriodo(incoming.getDataInicioPeriodo());
                            existingPr.setDataFimPeriodo(incoming.getDataFimPeriodo());
                            existingPr.setDataInicioEscala3_3(incoming.getDataInicioEscala3_3());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingPr.setRemovido(true);
                                existingPr.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingPr.setRemovido(false);
                                existingPr.setDataRemovido(null);
                            }
                            syncHorariosPedestreRegra(existingPr, incoming.getHorarios());
                        });
            }
        }
    }

    private void syncHorariosPedestreRegra(PedestreRegraEntity existingPr, List<Horario> incomingHorarios) {
        if (existingPr.getHorarios() == null) {
            existingPr.setHorarios(new ArrayList<>());
        }
        if (incomingHorarios == null || incomingHorarios.isEmpty()) {
            for (HorarioEntity h : existingPr.getHorarios()) {
                h.setRemovido(true);
                h.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingHorarios.stream()
                .map(Horario::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (HorarioEntity existingH : existingPr.getHorarios()) {
            if (!incomingIds.contains(existingH.getId())) {
                existingH.setRemovido(true);
                existingH.setDataRemovido(LocalDateTime.now());
            }
        }

        for (Horario incoming : incomingHorarios) {
            if (incoming.getId() == null) {
                HorarioEntity newH = horarioMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newH.setRegra(null);
                newH.setIdPedestreRegra(existingPr.getId());
                newH.setRemovido(false);
                existingPr.getHorarios().add(newH);
            } else {
                existingPr.getHorarios().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingH -> {
                            existingH.setNome(incoming.getNome());
                            existingH.setStatus(incoming.getStatus());
                            existingH.setDiasSemana(incoming.getDiasSemana());
                            existingH.setHorarioInicio(incoming.getHorarioInicio());
                            existingH.setHorarioFim(incoming.getHorarioFim());
                            existingH.setQtdeDeCreditos(incoming.getQtdeDeCreditos());
                            existingH.setRemovido(false);
                            existingH.setDataRemovido(null);
                        });
            }
        }
    }

    private boolean isEnderecoEmpty(Endereco endereco) {
        if (endereco == null) return true;
        return (endereco.getCep() == null || endereco.getCep().isBlank())
                && (endereco.getLogradouro() == null || endereco.getLogradouro().isBlank())
                && (endereco.getNumero() == null || endereco.getNumero().isBlank())
                && (endereco.getComplemento() == null || endereco.getComplemento().isBlank())
                && (endereco.getBairro() == null || endereco.getBairro().isBlank())
                && (endereco.getCidade() == null || endereco.getCidade().isBlank())
                && (endereco.getEstado() == null || endereco.getEstado().isBlank());
    }

    private void filterActiveChildren(Pedestre domain) {
        if (domain == null) return;
        if (domain.getEndereco() != null && Boolean.TRUE.equals(domain.getEndereco().getRemovido())) {
            domain.setEndereco(null);
        }
        if (domain.getRegras() != null) {
            List<PedestreRegra> activeRegras = domain.getRegras().stream()
                    .filter(r -> r.getRemovido() == null || !r.getRemovido())
                    .toList();
            activeRegras.forEach(r -> {
                if (r.getHorarios() != null) {
                    r.setHorarios(r.getHorarios().stream()
                            .filter(h -> h.getRemovido() == null || !h.getRemovido())
                            .toList());
                }
            });
            domain.setRegras(activeRegras);
        }
    }
}
