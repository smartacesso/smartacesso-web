package br.com.startjob.acesso.core.domain.login;

public record AppLoginResult(
        boolean success,
        int httpStatus,
        String message,
        String code,
        String token,
        Long userId,
        String nome,
        String cliente,
        String perfil
) {

    public static AppLoginResult ok(String token, Long userId, String nome, String cliente, String perfil) {
        return new AppLoginResult(true, 200, null, null, token, userId, nome, cliente, perfil);
    }

    public static AppLoginResult error(int status, String message, String code) {
        return new AppLoginResult(false, status, message, code, null, null, null, null, null);
    }
}
