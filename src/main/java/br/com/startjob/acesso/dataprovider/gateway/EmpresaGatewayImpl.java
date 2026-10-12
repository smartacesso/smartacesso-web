package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.dataprovider.entity.EmpresaEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.EmpresaMapper;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class EmpresaGatewayImpl implements EmpresaGateway {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

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
                .map(entity -> empresaMapper.toDomain(entity, new CycleAvoidingMappingContext()));
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
                if (empresa.getEmail() != null) existing.setEmail(empresa.getEmail());
                if (empresa.getTelefone() != null) existing.setTelefone(empresa.getTelefone());
                if (empresa.getCelular() != null) existing.setCelular(empresa.getCelular());
                if (empresa.getStatus() != null) existing.setStatus(empresa.getStatus());
                if (empresa.getCodigoExterno() != null) existing.setCodigoExterno(empresa.getCodigoExterno());
                if (empresa.getCodEmpresaSenior() != null) existing.setCodEmpresaSenior(empresa.getCodEmpresaSenior());
                if (empresa.getAutoAtendimentoLiberado() != null) existing.setAutoAtendimentoLiberado(empresa.getAutoAtendimentoLiberado());
                var saved = empresaRepository.save(existing);
                return empresaMapper.toDomain(saved, new CycleAvoidingMappingContext());
            }
        }
        var entity = empresaMapper.toEntity(empresa, new CycleAvoidingMappingContext());
        var saved = empresaRepository.save(entity);
        return empresaMapper.toDomain(saved, new CycleAvoidingMappingContext());
    }
}
