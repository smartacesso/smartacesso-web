package br.com.startjob.acesso.entrypoint.api.controller;

import br.com.startjob.acesso.core.domain.usuario.Usuario;
import br.com.startjob.acesso.core.gateway.UsuarioGateway;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuario Controller", description = "Endpoints para gerenciamento de usuarios")
public class UsuarioController {

    private final UsuarioGateway usuarioGateway;

    @GetMapping
    public void buscarUsuarios() {
        Optional<Usuario> loggedUser = usuarioGateway.findLoggedUser();

        log.info("Usuário logado: {}", loggedUser.map(Usuario::getLogin).orElse("Nenhum usuário logado"));
        // Implementação do endpoint para buscar usuários
    }
}
