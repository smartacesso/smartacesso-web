package br.com.startjob.acesso.entrypoint.api.dto;

import java.util.Set;

public record WebUsuarioDto(
        Long id,
        String nome,
        String perfil,
        Set<String> permissoes
) {}
