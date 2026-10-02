package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.WebPerfilPermissaoMapper;
import br.com.startjob.acesso.dataprovider.repository.WebPerfilPermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WebPerfilPermissaoGatewayImpl implements WebPerfilPermissaoGateway {

    private final WebPerfilPermissaoRepository webPerfilPermissaoRepository;
    private final WebPerfilPermissaoMapper webPerfilPermissaoMapper;

    @Override
    public long countByClienteAtivo(Long clienteId) {
        return webPerfilPermissaoRepository.countByClienteAtivo(clienteId);
    }

    @Override
    public List<WebPerfilPermissao> findByClienteAndPerfil(Long clienteId, PerfilAcesso perfil) {
        return webPerfilPermissaoRepository.findByClienteAndPerfil(clienteId, perfil).stream()
                .map(entity -> webPerfilPermissaoMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

}
