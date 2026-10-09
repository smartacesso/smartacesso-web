package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.WebPermissao;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PasswordGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import br.com.startjob.acesso.core.usecase.login.WebPermissaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class SalvarUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final WebPermissaoUseCase webPermissaoUseCase;
    private final PasswordGateway passwordGateway;

    private static final String SENHA_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{8,}$";

    @Transactional
    public Usuario execute(Usuario usuarioNovoOuEditado, String confirmarSenha) {
        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        Set<String> permissoes = webPermissaoUseCase.resolverPermissoesWeb(usuarioLogado);
        if (!permissoes.contains(WebPermissao.USUARIO_EDITAR.getCodigo())) {
            throw new BusinessException("Você não possui permissão para editar usuários.");
        }

        boolean novoUsuario = (usuarioNovoOuEditado.getId() == null);
        Long idClienteLogado = usuarioLogado.getCliente().getId();

        if (novoUsuario) {
            validarSenhas(usuarioNovoOuEditado.getSenha(), confirmarSenha);
            validarRegexSenha(usuarioNovoOuEditado.getSenha());
            
            usuarioNovoOuEditado.setSenha(passwordGateway.hash(usuarioNovoOuEditado.getSenha()));

            if (usuarioNovoOuEditado.getEmail() != null && !usuarioNovoOuEditado.getEmail().isEmpty()) {
                if (usuarioGateway.existsByEmail(usuarioNovoOuEditado.getEmail())) {
                    throw new BusinessException("msg.email.existente");
                }
            }

            if (usuarioGateway.existsByLoginAndCliente(usuarioNovoOuEditado.getLogin(), idClienteLogado)) {
                throw new BusinessException("msg.login.existente");
            }
            
            // Define o cliente do novo usuário como o mesmo do usuário logado
            usuarioNovoOuEditado.setCliente(usuarioLogado.getCliente());

        } else {
            Usuario usuarioExistente = usuarioGateway.findById(usuarioNovoOuEditado.getId())
                    .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

            if (!usuarioExistente.getCliente().getId().equals(idClienteLogado)) {
                throw new BusinessException("O usuário não pertence ao seu cliente.");
            }

            if (usuarioNovoOuEditado.getSenha() != null && !usuarioNovoOuEditado.getSenha().isEmpty()) {
                validarSenhas(usuarioNovoOuEditado.getSenha(), confirmarSenha);
                validarRegexSenha(usuarioNovoOuEditado.getSenha());
                usuarioNovoOuEditado.setSenha(passwordGateway.hash(usuarioNovoOuEditado.getSenha()));
            } else {
                usuarioNovoOuEditado.setSenha(usuarioExistente.getSenha());
            }

            // Mantém o mesmo cliente
            usuarioNovoOuEditado.setCliente(usuarioExistente.getCliente());
            
            // Mantém outros campos que não podem ser alterados ou são de controle, caso aplicável
            usuarioNovoOuEditado.setDataCriacao(usuarioExistente.getDataCriacao());
        }

        return usuarioGateway.save(usuarioNovoOuEditado);
    }

    private void validarSenhas(String senha, String confirmarSenha) {
        if (senha == null || !senha.equals(confirmarSenha)) {
            throw new BusinessException("As senhas não conferem.");
        }
    }

    private void validarRegexSenha(String senha) {
        if (!senha.matches(SENHA_REGEX)) {
            throw new BusinessException("Senha inválida. Deve conter letras maiúsculas, minúsculas, números e caracteres especiais.");
        }
    }
}
