package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.UsuarioMapper;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Component
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Optional<Usuario> findLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof String userId)) {
            return Optional.empty();
        }
        try {
            return usuarioRepository.findByIdWithCliente(Long.valueOf(userId))
                    .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Usuario> findByLoginAndUnidade(String login, String unidade) {
        return usuarioRepository.findByLoginAndUnidade(login, unidade).stream()
                .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Usuario> buscarUsuarios(Long idCliente, String nome, String cpf, Pageable pageable) {
        return usuarioRepository.buscarUsuarios(idCliente, nome, cpf, pageable)
                .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }
}
