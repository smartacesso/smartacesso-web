package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.empresa.Cargo;
import br.com.startjob.acesso.core.domain.empresa.CentroCusto;
import br.com.startjob.acesso.core.domain.empresa.Departamento;
import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.dataprovider.entity.CargoEntity;
import br.com.startjob.acesso.dataprovider.entity.CentroCustoEntity;
import br.com.startjob.acesso.dataprovider.entity.DepartamentoEntity;
import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import br.com.startjob.acesso.dataprovider.mapper.*;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class EmpresaGatewayImpl implements EmpresaGateway {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    private final DepartamentoMapper departamentoMapper;
    private final CentroCustoMapper centroCustoMapper;
    private final CargoMapper cargoMapper;
    private final EnderecoMapper enderecoMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<Empresa> buscarEmpresas(Long idCliente, String nome, String cnpj, Pageable pageable) {
        return empresaRepository.buscarEmpresas(idCliente, nome, cnpj, pageable)
                .map(entity -> empresaMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empresa> findById(Long id) {
        return empresaRepository.findByIdWithCliente(id)
                .map(entity -> {
                    var domain = empresaMapper.toDomain(entity, new CycleAvoidingMappingContext());
                    filterActiveChildren(domain);
                    return domain;
                });
    }

    @Override
    @Transactional
    public Empresa save(Empresa empresa) {
        if (empresa.getId() != null) {
            Optional<EmpresaEntity> existingOpt = empresaRepository.findById(empresa.getId());
            if (existingOpt.isPresent()) {
                var existing = existingOpt.get();
                existing.setRemovido(empresa.getRemovido());
                existing.setDataRemovido(empresa.getDataRemovido());
                if (empresa.getNome() != null) existing.setNome(empresa.getNome());
                if (empresa.getCnpj() != null) existing.setCnpj(empresa.getCnpj());
                existing.setEmail(empresa.getEmail());
                existing.setTelefone(empresa.getTelefone());
                existing.setCelular(empresa.getCelular());
                if (empresa.getStatus() != null) existing.setStatus(empresa.getStatus());
                if (empresa.getCodigoExterno() != null) existing.setCodigoExterno(empresa.getCodigoExterno());
                if (empresa.getCodEmpresaSenior() != null) existing.setCodEmpresaSenior(empresa.getCodEmpresaSenior());
                if (empresa.getAutoAtendimentoLiberado() != null) existing.setAutoAtendimentoLiberado(empresa.getAutoAtendimentoLiberado());

                syncEndereco(existing, empresa.getEndereco());

                if (empresa.getDepartamentos() != null) {
                    syncDepartamentos(existing, empresa.getDepartamentos());
                }

                if (empresa.getCentros() != null) {
                    syncCentros(existing, empresa.getCentros());
                }

                if (empresa.getCargos() != null) {
                    syncCargos(existing, empresa.getCargos());
                }

                var saved = empresaRepository.save(existing);
                var domain = empresaMapper.toDomain(saved, new CycleAvoidingMappingContext());
                filterActiveChildren(domain);
                return domain;
            }
        }

        var entity = empresaMapper.toEntity(empresa, new CycleAvoidingMappingContext());
        if (entity.getEndereco() != null) {
            if (isEnderecoEmpty(empresa.getEndereco())) {
                entity.setEndereco(null);
            } else if (entity.getEndereco().getRemovido() == null) {
                entity.getEndereco().setRemovido(false);
            }
        }
        if (entity.getDepartamentos() != null) {
            entity.getDepartamentos().forEach(d -> {
                d.setEmpresa(entity);
                if (d.getRemovido() == null) d.setRemovido(false);
            });
        }
        if (entity.getCentros() != null) {
            entity.getCentros().forEach(c -> {
                c.setEmpresa(entity);
                if (c.getRemovido() == null) c.setRemovido(false);
            });
        }
        if (entity.getCargos() != null) {
            entity.getCargos().forEach(ca -> {
                ca.setEmpresa(entity);
                if (ca.getRemovido() == null) ca.setRemovido(false);
            });
        }
        if (entity.getRemovido() == null) {
            entity.setRemovido(false);
        }

        var saved = empresaRepository.save(entity);
        var domain = empresaMapper.toDomain(saved, new CycleAvoidingMappingContext());
        filterActiveChildren(domain);
        return domain;
    }

    private void syncDepartamentos(EmpresaEntity existing, List<Departamento> incomingList) {
        Set<Long> incomingIds = incomingList.stream()
                .map(Departamento::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (DepartamentoEntity existingDep : existing.getDepartamentos()) {
            if (!incomingIds.contains(existingDep.getId())) {
                existingDep.setRemovido(true);
                existingDep.setDataRemovido(LocalDateTime.now());
            }
        }

        for (Departamento incoming : incomingList) {
            if (incoming.getId() == null) {
                DepartamentoEntity newDep = departamentoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newDep.setEmpresa(existing);
                newDep.setRemovido(false);
                existing.getDepartamentos().add(newDep);
            } else {
                existing.getDepartamentos().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingDep -> {
                            existingDep.setNome(incoming.getNome());
                            existingDep.setStatus(incoming.getStatus());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingDep.setRemovido(true);
                                existingDep.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingDep.setRemovido(false);
                                existingDep.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncCentros(EmpresaEntity existing, List<CentroCusto> incomingList) {
        Set<Long> incomingIds = incomingList.stream()
                .map(CentroCusto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (CentroCustoEntity existingCentro : existing.getCentros()) {
            if (!incomingIds.contains(existingCentro.getId())) {
                existingCentro.setRemovido(true);
                existingCentro.setDataRemovido(LocalDateTime.now());
            }
        }

        for (CentroCusto incoming : incomingList) {
            if (incoming.getId() == null) {
                CentroCustoEntity newCentro = centroCustoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newCentro.setEmpresa(existing);
                newCentro.setRemovido(false);
                existing.getCentros().add(newCentro);
            } else {
                existing.getCentros().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingCentro -> {
                            existingCentro.setNome(incoming.getNome());
                            existingCentro.setStatus(incoming.getStatus());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingCentro.setRemovido(true);
                                existingCentro.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingCentro.setRemovido(false);
                                existingCentro.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncCargos(EmpresaEntity existing, List<Cargo> incomingList) {
        Set<Long> incomingIds = incomingList.stream()
                .map(Cargo::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (CargoEntity existingCargo : existing.getCargos()) {
            if (!incomingIds.contains(existingCargo.getId())) {
                existingCargo.setRemovido(true);
                existingCargo.setDataRemovido(LocalDateTime.now());
            }
        }

        for (Cargo incoming : incomingList) {
            if (incoming.getId() == null) {
                CargoEntity newCargo = cargoMapper.toEntity(incoming, new CycleAvoidingMappingContext());
                newCargo.setEmpresa(existing);
                newCargo.setRemovido(false);
                existing.getCargos().add(newCargo);
            } else {
                existing.getCargos().stream()
                        .filter(e -> incoming.getId().equals(e.getId()))
                        .findFirst()
                        .ifPresent(existingCargo -> {
                            existingCargo.setNome(incoming.getNome());
                            existingCargo.setStatus(incoming.getStatus());
                            if (Boolean.TRUE.equals(incoming.getRemovido())) {
                                existingCargo.setRemovido(true);
                                existingCargo.setDataRemovido(incoming.getDataRemovido() != null ? incoming.getDataRemovido() : LocalDateTime.now());
                            } else {
                                existingCargo.setRemovido(false);
                                existingCargo.setDataRemovido(null);
                            }
                        });
            }
        }
    }

    private void syncEndereco(EmpresaEntity existing, Endereco incoming) {
        if (incoming == null) {
            existing.setEndereco(null);
            return;
        }

        boolean isEmpty = isEnderecoEmpty(incoming);
        if (isEmpty && existing.getEndereco() == null) {
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

    private void filterActiveChildren(Empresa domain) {
        if (domain == null) return;
        if (domain.getEndereco() != null && Boolean.TRUE.equals(domain.getEndereco().getRemovido())) {
            domain.setEndereco(null);
        }
        if (domain.getDepartamentos() != null) {
            domain.setDepartamentos(domain.getDepartamentos().stream()
                    .filter(d -> d.getRemovido() == null || !d.getRemovido())
                    .toList());
        }
        if (domain.getCentros() != null) {
            domain.setCentros(domain.getCentros().stream()
                    .filter(c -> c.getRemovido() == null || !c.getRemovido())
                    .toList());
        }
        if (domain.getCargos() != null) {
            domain.setCargos(domain.getCargos().stream()
                    .filter(ca -> ca.getRemovido() == null || !ca.getRemovido())
                    .toList());
        }
    }
}

