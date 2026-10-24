package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.parametro.Parametro;
import br.com.startjob.acesso.core.enumeration.ParametroCatalogo;
import br.com.startjob.acesso.core.gateway.ParametroGateway;
import br.com.startjob.acesso.dataprovider.entity.ClienteEntity;
import br.com.startjob.acesso.dataprovider.entity.ParametroEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.ParametroMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.ParametroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ParametroGatewayImpl implements ParametroGateway {

    private final ParametroRepository parametroRepository;
    private final ClienteRepository clienteRepository;
    private final ParametroMapper parametroMapper;

    @Override
    public Optional<Parametro> findByClienteAndNome(Long clienteId, String nome) {
        return parametroRepository.findByClienteAndNome(clienteId, nome)
                .map(entity -> parametroMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    public List<Parametro> findAllByCliente(Long clienteId) {
        if (clienteId == null) {
            return List.of();
        }
        CycleAvoidingMappingContext context = new CycleAvoidingMappingContext();
        return parametroRepository.findAllByCliente(clienteId).stream()
                .map(entity -> parametroMapper.toDomain(entity, context))
                .toList();
    }

    @Override
    @Transactional
    public void salvarParametrosCliente(Long clienteId, Map<String, String> valoresPorNome) {
        if (clienteId == null || valoresPorNome == null || valoresPorNome.isEmpty()) {
            return;
        }
        ClienteEntity cliente = clienteRepository.getReferenceById(clienteId);
        List<ParametroEntity> existentes = parametroRepository.findAllByCliente(clienteId);

        Map<String, ParametroEntity> indice = new LinkedHashMap<>();
        if (existentes != null) {
            for (ParametroEntity row : existentes) {
                String nomeNormalizado = ParametroCatalogo.normalizarNomeLegado(row.getNome());
                if (nomeNormalizado == null) {
                    continue;
                }
                row.setNome(nomeNormalizado);
                indice.putIfAbsent(nomeNormalizado, row);
            }
        }

        List<ParametroEntity> paraSalvar = new ArrayList<>();
        for (Map.Entry<String, String> entry : valoresPorNome.entrySet()) {
            String nome = entry.getKey();
            String valor = entry.getValue();
            if (nome == null) {
                continue;
            }
            ParametroEntity row = indice.get(nome);
            if (row == null) {
                row = new ParametroEntity();
                row.setCliente(cliente);
                row.setNome(nome);
            }
            row.setValor(valor);
            row.setRemovido(false);
            row.setDataRemovido(null);
            paraSalvar.add(row);
            indice.put(nome, row);
        }

        parametroRepository.saveAll(paraSalvar);
    }

}
