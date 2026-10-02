package br.com.startjob.acesso.entrypoint.api.dto;

public record AppLoginResponse(String token, String tipo, AppUsuarioDto usuario) {
}
