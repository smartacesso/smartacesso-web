package br.com.startjob.acesso.entrypoint.api.dto;

public record WebLoginResponse(
        String token,
        String type,
        WebUsuarioDto usuario
) {}
