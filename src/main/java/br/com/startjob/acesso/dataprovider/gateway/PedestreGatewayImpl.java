package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.pedestre.*;
import br.com.startjob.acesso.core.domain.regra.Horario;
import br.com.startjob.acesso.core.gateway.PedestreGateway;
import br.com.startjob.acesso.dataprovider.entity.*;
import br.com.startjob.acesso.dataprovider.mapper.*;
import br.com.startjob.acesso.dataprovider.repository.*;
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
    private final EquipamentoRepository equipamentoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final PedestreMapper pedestreMapper;
    private final PedestreRegraMapper pedestreRegraMapper;
    private final PedestreEquipamentoMapper pedestreEquipamentoMapper;
    private final DocumentoMapper documentoMapper;
    private final BiometriaMapper biometriaMapper;
    private final MensagemEquipamentoMapper mensagemEquipamentoMapper;
    private final HistoricoCotaMapper historicoCotaMapper;
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
                existing.setQtdAcessoAntesSinc(pedestre.getQtdAcessoAntesSinc());

                syncEndereco(existing, pedestre.getEndereco());
                syncOrganizacional(existing, pedestre);
                syncRegras(existing, pedestre.getRegras());
                syncEquipamentos(existing, pedestre.getEquipamentos());
                syncDocumentos(existing, pedestre.getDocumentos());
                syncBiometrias(existing, pedestre.getBiometrias());
                syncMensagensPersonalizadas(existing, pedestre.getMensagensPersonalizadas());
                syncCotas(existing, pedestre.getCotas());
                syncResponsaveis(existing, pedestre.getResponsaveis(), pedestre.getResponsaveisEntidade());

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

        if (entity.getEquipamentos() != null) {
            entity.getEquipamentos().forEach(pe -> {
                pe.setPedestre(entity);
                if (pe.getEquipamento() != null && pe.getEquipamento().getId() != null) {
                    pe.setEquipamento(equipamentoRepository.getReferenceById(pe.getEquipamento().getId()));
                }
                if (pe.getRemovido() == null) {
                    pe.setRemovido(false);
                }
            });
        }

        if (entity.getDocumentos() != null) {
            entity.getDocumentos().forEach(doc -> {
                doc.setPedestre(entity);
                if (doc.getRemovido() == null) {
                    doc.setRemovido(false);
                }
            });
        }

        if (entity.getBiometrias() != null) {
            entity.getBiometrias().forEach(bio -> {
                bio.setPedestre(entity);
                if (bio.getRemovido() == null) {
                    bio.setRemovido(false);
                }
            });
        }

        if (entity.getMensagensPersonalizadas() != null) {
            entity.getMensagensPersonalizadas().forEach(msg -> {
                msg.setPedestre(entity);
                if (msg.getRemovido() == null) {
                    msg.setRemovido(false);
                }
            });
        }

        if (entity.getCotas() != null) {
            entity.getCotas().forEach(cota -> {
                cota.setPedestre(entity);
                cota.setCliente(entity.getCliente());
                if (cota.getRemovido() == null) {
                    cota.setRemovido(false);
                }
            });
        }

        syncResponsaveis(entity, pedestre.getResponsaveis(), pedestre.getResponsaveisEntidade());

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

    private void syncEquipamentos(PedestreEntity existing, List<PedestreEquipamento> incomingList) {
        if (existing.getEquipamentos() == null) {
            existing.setEquipamentos(new ArrayList<>());
        }
        if (incomingList == null || incomingList.isEmpty()) {
            for (PedestreEquipamentoEntity existingPe : existing.getEquipamentos()) {
                existingPe.setRemovido(true);
                existingPe.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingList.stream()
                .map(PedestreEquipamento::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (PedestreEquipamentoEntity existingPe : existing.getEquipamentos()) {
            if (!incomingIds.contains(existingPe.getId())) {
                existingPe.setRemovido(true);
                existingPe.setDataRemovido(LocalDateTime.now());
            }
        }

        for (PedestreEquipamento incoming : incomingList) {
            if (incoming.getId() == null) {
                PedestreEquipamentoEntity newPe = pedestreEquipamentoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newPe.setPedestre(existing);
                if (incoming.getEquipamento() != null && incoming.getEquipamento().getId() != null) {
                    newPe.setEquipamento(equipamentoRepository.getReferenceById(incoming.getEquipamento().getId()));
                }
                newPe.setRemovido(Boolean.TRUE.equals(incoming.getRemovido()));
                newPe.setDataRemovido(incoming.getDataRemovido());
                existing.getEquipamentos().add(newPe);
            } else {
                existing.getEquipamentos().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingPe -> {
                            if (incoming.getEquipamento() != null && incoming.getEquipamento().getId() != null) {
                                existingPe.setEquipamento(equipamentoRepository.getReferenceById(incoming.getEquipamento().getId()));
                            }
                            existingPe.setValidade(incoming.getValidade());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingPe.setRemovido(true);
                                existingPe.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingPe.setRemovido(false);
                                existingPe.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncDocumentos(PedestreEntity existing, List<Documento> incomingList) {
        if (existing.getDocumentos() == null) {
            existing.setDocumentos(new ArrayList<>());
        }
        if (incomingList == null || incomingList.isEmpty()) {
            for (DocumentoEntity existingDoc : existing.getDocumentos()) {
                existingDoc.setRemovido(true);
                existingDoc.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingList.stream()
                .map(Documento::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (DocumentoEntity existingDoc : existing.getDocumentos()) {
            if (!incomingIds.contains(existingDoc.getId())) {
                existingDoc.setRemovido(true);
                existingDoc.setDataRemovido(LocalDateTime.now());
            }
        }

        for (Documento incoming : incomingList) {
            if (incoming.getId() == null) {
                DocumentoEntity newDoc = documentoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newDoc.setPedestre(existing);
                newDoc.setRemovido(Boolean.TRUE.equals(incoming.getRemovido()));
                newDoc.setDataRemovido(incoming.getDataRemovido());
                existing.getDocumentos().add(newDoc);
            } else {
                existing.getDocumentos().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingDoc -> {
                            existingDoc.setNome(incoming.getNome());
                            if (incoming.getArquivo() != null && incoming.getArquivo().length > 0) {
                                existingDoc.setArquivo(incoming.getArquivo());
                            }
                            existingDoc.setValidade(incoming.getValidade());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingDoc.setRemovido(true);
                                existingDoc.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingDoc.setRemovido(false);
                                existingDoc.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncBiometrias(PedestreEntity existing, List<Biometria> incomingList) {
        if (existing.getBiometrias() == null) {
            existing.setBiometrias(new ArrayList<>());
        }
        if (incomingList == null || incomingList.isEmpty()) {
            for (BiometriaEntity existingBio : existing.getBiometrias()) {
                existingBio.setRemovido(true);
                existingBio.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingList.stream()
                .map(Biometria::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (BiometriaEntity existingBio : existing.getBiometrias()) {
            if (!incomingIds.contains(existingBio.getId())) {
                existingBio.setRemovido(true);
                existingBio.setDataRemovido(LocalDateTime.now());
            }
        }

        for (Biometria incoming : incomingList) {
            if (incoming.getId() == null) {
                BiometriaEntity newBio = biometriaMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newBio.setPedestre(existing);
                newBio.setRemovido(Boolean.TRUE.equals(incoming.getRemovido()));
                newBio.setDataRemovido(incoming.getDataRemovido());
                existing.getBiometrias().add(newBio);
            } else {
                existing.getBiometrias().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingBio -> {
                            existingBio.setDedo(incoming.getDedo());
                            if (incoming.getTemplate() != null && incoming.getTemplate().length > 0) {
                                existingBio.setTemplate(incoming.getTemplate());
                            }
                            if (incoming.getSample() != null && incoming.getSample().length > 0) {
                                existingBio.setSample(incoming.getSample());
                            }
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingBio.setRemovido(true);
                                existingBio.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingBio.setRemovido(false);
                                existingBio.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncMensagensPersonalizadas(PedestreEntity existing, List<MensagemEquipamento> incomingList) {
        if (existing.getMensagensPersonalizadas() == null) {
            existing.setMensagensPersonalizadas(new ArrayList<>());
        }
        if (incomingList == null || incomingList.isEmpty()) {
            for (MensagemEquipamentoEntity existingMsg : existing.getMensagensPersonalizadas()) {
                existingMsg.setRemovido(true);
                existingMsg.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingList.stream()
                .map(MensagemEquipamento::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (MensagemEquipamentoEntity existingMsg : existing.getMensagensPersonalizadas()) {
            if (!incomingIds.contains(existingMsg.getId())) {
                existingMsg.setRemovido(true);
                existingMsg.setDataRemovido(LocalDateTime.now());
            }
        }

        for (MensagemEquipamento incoming : incomingList) {
            if (incoming.getId() == null) {
                MensagemEquipamentoEntity newMsg = mensagemEquipamentoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newMsg.setPedestre(existing);
                newMsg.setRemovido(Boolean.TRUE.equals(incoming.getRemovido()));
                newMsg.setDataRemovido(incoming.getDataRemovido());
                existing.getMensagensPersonalizadas().add(newMsg);
            } else {
                existing.getMensagensPersonalizadas().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingMsg -> {
                            existingMsg.setNome(incoming.getNome());
                            existingMsg.setStatus(incoming.getStatus());
                            existingMsg.setMensagem(incoming.getMensagem());
                            existingMsg.setQuantidade(incoming.getQuantidade());
                            existingMsg.setValidade(incoming.getValidade());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingMsg.setRemovido(true);
                                existingMsg.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingMsg.setRemovido(false);
                                existingMsg.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncCotas(PedestreEntity existing, List<HistoricoCota> incomingList) {
        if (existing.getCotas() == null) {
            existing.setCotas(new ArrayList<>());
        }
        if (incomingList == null || incomingList.isEmpty()) {
            for (HistoricoCotaEntity existingCota : existing.getCotas()) {
                existingCota.setRemovido(true);
                existingCota.setDataRemovido(LocalDateTime.now());
            }
            return;
        }

        Set<Long> incomingIds = incomingList.stream()
                .map(HistoricoCota::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (HistoricoCotaEntity existingCota : existing.getCotas()) {
            if (!incomingIds.contains(existingCota.getId())) {
                existingCota.setRemovido(true);
                existingCota.setDataRemovido(LocalDateTime.now());
            }
        }

        for (HistoricoCota incoming : incomingList) {
            if (incoming.getId() == null) {
                HistoricoCotaEntity newCota = historicoCotaMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newCota.setPedestre(existing);
                newCota.setCliente(existing.getCliente());
                newCota.setRemovido(Boolean.TRUE.equals(incoming.getRemovido()));
                newCota.setDataRemovido(incoming.getDataRemovido());
                existing.getCotas().add(newCota);
            } else {
                existing.getCotas().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingCota -> {
                            existingCota.setMes(incoming.getMes());
                            existingCota.setAno(incoming.getAno());
                            existingCota.setCotaMensal(incoming.getCotaMensal());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingCota.setRemovido(true);
                                existingCota.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingCota.setRemovido(false);
                                existingCota.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncResponsaveis(
            PedestreEntity target,
            List<Pedestre> responsaveisPedestres,
            List<Responsavel> responsaveisEntidades
    ) {
        if (target.getResponsaveis() == null) {
            target.setResponsaveis(new ArrayList<>());
        } else {
            target.getResponsaveis().clear();
        }
        if (responsaveisPedestres != null) {
            for (Pedestre r : responsaveisPedestres) {
                if (r != null && r.getId() != null) {
                    target.getResponsaveis().add(pedestreRepository.getReferenceById(r.getId()));
                }
            }
        }

        if (target.getResponsaveisEntidade() == null) {
            target.setResponsaveisEntidade(new ArrayList<>());
        } else {
            target.getResponsaveisEntidade().clear();
        }
        if (responsaveisEntidades != null) {
            for (Responsavel re : responsaveisEntidades) {
                if (re != null && re.getId() != null) {
                    target.getResponsaveisEntidade().add(responsavelRepository.getReferenceById(re.getId()));
                }
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
        if (domain.getEquipamentos() != null) {
            domain.setEquipamentos(domain.getEquipamentos().stream()
                    .filter(e -> e.getRemovido() == null || !e.getRemovido())
                    .toList());
        }
        if (domain.getDocumentos() != null) {
            domain.setDocumentos(domain.getDocumentos().stream()
                    .filter(d -> d.getRemovido() == null || !d.getRemovido())
                    .toList());
        }
        if (domain.getBiometrias() != null) {
            domain.setBiometrias(domain.getBiometrias().stream()
                    .filter(b -> b.getRemovido() == null || !b.getRemovido())
                    .toList());
        }
        if (domain.getMensagensPersonalizadas() != null) {
            domain.setMensagensPersonalizadas(domain.getMensagensPersonalizadas().stream()
                    .filter(m -> m.getRemovido() == null || !m.getRemovido())
                    .toList());
        }
        if (domain.getCotas() != null) {
            domain.setCotas(domain.getCotas().stream()
                    .filter(c -> c.getRemovido() == null || !c.getRemovido())
                    .toList());
        }
    }
}
