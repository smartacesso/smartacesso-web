package br.com.startjob.acesso.dataprovider.gateway;

import br.com.startjob.acesso.core.domain.endereco.Endereco;
import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.dataprovider.entity.EnderecoEntity;
import br.com.startjob.acesso.dataprovider.entity.UsuarioEntity;
import br.com.startjob.acesso.dataprovider.mapper.CycleAvoidingMappingContext;
import br.com.startjob.acesso.dataprovider.mapper.EnderecoMapper;
import br.com.startjob.acesso.dataprovider.mapper.UsuarioMapper;
import br.com.startjob.acesso.dataprovider.repository.ClienteRepository;
import br.com.startjob.acesso.dataprovider.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UsuarioGatewayImpl implements UsuarioGateway {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioMapper usuarioMapper;
    private final EnderecoMapper enderecoMapper;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<Usuario> findByLoginAndUnidade(String login, String unidade) {
        return usuarioRepository.findByLoginAndUnidade(login, unidade).stream()
                .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> buscarUsuarios(Long idCliente, String nome, String cpf, Pageable pageable) {
        return usuarioRepository.buscarUsuarios(idCliente, nome, cpf, pageable)
                .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }

    @Override
    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByLoginAndCliente(String login, Long idCliente) {
        return usuarioRepository.existsByLoginAndCliente(login, idCliente);
    }

    @Override
    @Transactional
    public Usuario save(Usuario usuario) {
        if (usuario.getId() != null) {
            Optional<UsuarioEntity> existingOpt = usuarioRepository.findByIdWithCliente(usuario.getId());
            if (existingOpt.isPresent()) {
                UsuarioEntity existing = existingOpt.get();
                if (usuario.getRemovido() != null) existing.setRemovido(usuario.getRemovido());
                if (usuario.getDataRemovido() != null) existing.setDataRemovido(usuario.getDataRemovido());
                if (usuario.getNome() != null) existing.setNome(usuario.getNome());
                if (usuario.getStatus() != null) existing.setStatus(usuario.getStatus());
                if (usuario.getLogin() != null) existing.setLogin(usuario.getLogin());
                if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) existing.setSenha(usuario.getSenha());
                if (usuario.getEmail() != null) existing.setEmail(usuario.getEmail());
                if (usuario.getCpf() != null) existing.setCpf(usuario.getCpf());
                if (usuario.getRg() != null) existing.setRg(usuario.getRg());
                if (usuario.getTelefone() != null) existing.setTelefone(usuario.getTelefone());
                if (usuario.getCelular() != null) existing.setCelular(usuario.getCelular());
                if (usuario.getDataNascimento() != null) existing.setDataNascimento(usuario.getDataNascimento());
                if (usuario.getPerfil() != null) existing.setPerfil(usuario.getPerfil());
                if (usuario.getToken() != null) existing.setToken(usuario.getToken());
                if (usuario.getAcessaWeb() != null) existing.setAcessaWeb(usuario.getAcessaWeb());
                if (usuario.getCadastroSimples() != null) existing.setCadastroSimples(usuario.getCadastroSimples());
                if (usuario.getUsuarioMaster() != null) existing.setUsuarioMaster(usuario.getUsuarioMaster());
                if (usuario.getExpedidora() != null) existing.setExpedidora(usuario.getExpedidora());

                syncEndereco(existing, usuario.getEndereco());

                var savedEntity = usuarioRepository.save(existing);
                return usuarioMapper.toDomain(savedEntity, new CycleAvoidingMappingContext());
            }
        }

        var entity = usuarioMapper.toEntity(usuario, new CycleAvoidingMappingContext());
        if (usuario.getCliente() != null && usuario.getCliente().getId() != null) {
            entity.setCliente(clienteRepository.getReferenceById(usuario.getCliente().getId()));
        }
        if (entity.getEndereco() != null) {
            if (isEnderecoEmpty(usuario.getEndereco())) {
                entity.setEndereco(null);
            } else if (entity.getEndereco().getRemovido() == null) {
                entity.getEndereco().setRemovido(false);
            }
        }
        if (entity.getRemovido() == null) {
            entity.setRemovido(false);
        }
        var savedEntity = usuarioRepository.save(entity);
        return usuarioMapper.toDomain(savedEntity, new CycleAvoidingMappingContext());
    }

    private void syncEndereco(UsuarioEntity existing, Endereco incoming) {
        if (incoming == null) {
            existing.setEndereco(null);
            return;
        }

        boolean isEmpty = isEnderecoEmpty(incoming);
        if (isEmpty && existing.getEndereco() == null) {
            return;
        }

        if (isEmpty) {
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

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findByIdWithCliente(id)
                .map(entity -> usuarioMapper.toDomain(entity, new CycleAvoidingMappingContext()));
    }
}
