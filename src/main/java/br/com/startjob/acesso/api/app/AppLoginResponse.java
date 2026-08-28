package br.com.startjob.acesso.api.app;

public record AppLoginResponse(String token, String tipo, AppUsuarioDto usuario) {
}
