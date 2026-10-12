package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.empresa.Empresa;
import br.com.startjob.acesso.core.gateway.EmpresaGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.EmpresaMapper;
import br.com.startjob.acesso.dataprovider.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
}
