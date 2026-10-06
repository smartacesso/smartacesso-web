package br.com.startjob.acesso.core.exception;

public final class LoginErrorCodes {

    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public static final String INVALID_PASSWORD = "INVALID_PASSWORD";
    public static final String INACTIVE_USER = "INACTIVE_USER";
    public static final String WEB_ACCESS_DENIED = "WEB_ACCESS_DENIED";
    public static final String NO_ACTIVE_PLAN = "NO_ACTIVE_PLAN";

    private LoginErrorCodes() {
    }
}
