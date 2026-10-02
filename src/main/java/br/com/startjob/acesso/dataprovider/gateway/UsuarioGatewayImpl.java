package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.UsuarioMapper;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public List<Usuario> findByLoginAndUnidade(String login, String unidade) {
        return usuarioRepository.findByLoginAndUnidade(login, unidade).stream()
                .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

}
