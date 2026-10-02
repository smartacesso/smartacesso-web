package br.com.startjob.acesso.core.exception;

public class LoginBusinessException extends BusinessException {

    private final String messageKey;

    public LoginBusinessException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
