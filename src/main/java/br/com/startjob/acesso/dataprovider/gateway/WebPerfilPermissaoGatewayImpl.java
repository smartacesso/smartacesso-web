package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.permissao.WebPerfilPermissao;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.gateway.WebPerfilPermissaoGateway;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.WebPerfilPermissaoEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.WebPerfilPermissaoMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.WebPerfilPermissaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class WebPerfilPermissaoGatewayImpl implements WebPerfilPermissaoGateway {

    private final WebPerfilPermissaoRepository webPerfilPermissaoRepository;
    private final ClienteRepository clienteRepository;
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

    @Override
    public List<WebPerfilPermissao> findByClienteAtivo(Long clienteId) {
        return webPerfilPermissaoRepository.findByClienteAtivo(clienteId).stream()
                .map(entity -> webPerfilPermissaoMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void salvarMatrizCliente(Long clienteId, Map<PerfilAcesso, Set<String>> permissoesHabilitadas) {
        if (clienteId == null || permissoesHabilitadas == null) {
            return;
        }
        ClienteEntity cliente = clienteRepository.getReferenceById(clienteId);

        List<WebPerfilPermissaoEntity> existentes = webPerfilPermissaoRepository.findAllByCliente(clienteId);
        Map<String, WebPerfilPermissaoEntity> indice = new HashMap<>();
        if (existentes != null) {
            for (WebPerfilPermissaoEntity row : existentes) {
                if (row.getPerfil() == null || row.getCodigoPermissao() == null) {
                    continue;
                }
                indice.put(chaveMatriz(row.getPerfil(), row.getCodigoPermissao()), row);
            }
        }

        List<WebPerfilPermissaoEntity> paraSalvar = new ArrayList<>();
        for (Map.Entry<PerfilAcesso, Set<String>> entry : permissoesHabilitadas.entrySet()) {
            PerfilAcesso perfil = entry.getKey();
            Set<String> habilitadas = entry.getValue();
            if (perfil == null || habilitadas == null) {
                continue;
            }
            for (WebPermissao permissao : WebPermissao.values()) {
                String codigo = permissao.getCodigo();
                String chave = chaveMatriz(perfil, codigo);
                WebPerfilPermissaoEntity row = indice.get(chave);
                if (row == null) {
                    row = new WebPerfilPermissaoEntity();
                    row.setCliente(cliente);
                    row.setPerfil(perfil);
                    row.setCodigoPermissao(codigo);
                }
                row.setHabilitado(habilitadas.contains(codigo));
                row.setRemovido(false);
                row.setDataRemovido(null);
                paraSalvar.add(row);
                indice.put(chave, row);
            }
        }

        webPerfilPermissaoRepository.saveAll(paraSalvar);
    }

    private static String chaveMatriz(PerfilAcesso perfil, String codigo) {
        return perfil.name() + "|" + codigo;
    }

}
