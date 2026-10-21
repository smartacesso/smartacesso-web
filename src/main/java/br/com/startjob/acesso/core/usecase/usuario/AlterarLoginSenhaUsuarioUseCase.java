package br.com.startjob.acesso.core.usecase.usuario;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.enumeration.PerfilAcesso;
import br.com.startjob.acesso.core.exception.BusinessException;
import br.com.startjob.acesso.core.gateway.PasswordGateway;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlterarLoginSenhaUsuarioUseCase {

    private final UsuarioGateway usuarioGateway;
    private final PasswordGateway passwordGateway;

    @Transactional
    public Usuario execute(Long idUsuario, String novoLogin, String novaSenha, String confirmacaoSenha) {
        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new BusinessException("As senhas não conferem.");
        }

        Usuario usuarioLogado = usuarioGateway.findLoggedUser()
                .orElseThrow(() -> new BusinessException("Usuário não está logado."));

        if (!PerfilAcesso.ADMINISTRADOR.equals(usuarioLogado.getPerfil())) {
            throw new BusinessException("Apenas administradores podem alterar o login e senha de usuários.");
        }

        Usuario usuarioExistente = usuarioGateway.findById(idUsuario)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        if (Boolean.TRUE.equals(usuarioExistente.getRemovido())) {
            throw new BusinessException("Usuário não encontrado.");
        }

        Long idClienteLogado = usuarioLogado.getCliente() != null ? usuarioLogado.getCliente().getId() : null;
        if (usuarioExistente.getCliente() == null || !usuarioExistente.getCliente().getId().equals(idClienteLogado)) {
            throw new BusinessException("O usuário não pertence ao seu cliente.");
        }

        usuarioGateway.findByLoginAndCliente(novoLogin, idClienteLogado)
                .ifPresent(usuarioComMesmoLogin -> {
                    if (!usuarioComMesmoLogin.getId().equals(idUsuario)) {
                        throw new BusinessException("Login já cadastrado para outro usuário.");
                    }
                });

        usuarioExistente.setLogin(novoLogin);
        usuarioExistente.setSenha(passwordGateway.hash(novaSenha));

        Usuario usuarioSalvo = usuarioGateway.save(usuarioExistente);
        log.info("Login e senha do usuário id={} alterados com sucesso.", idUsuario);
        return usuarioSalvo;
    }
}
