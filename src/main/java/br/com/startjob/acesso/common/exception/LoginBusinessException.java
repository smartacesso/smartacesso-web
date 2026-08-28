package br.com.startjob.acesso.common.exception;

public class LoginBusinessException extends RuntimeException {

    private final String messageKey;

    public LoginBusinessException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
