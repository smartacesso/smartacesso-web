package br.com.startjob.acesso.core.exception;

import lombok.Getter;

@Getter
public class LoginBusinessException extends BusinessException {

    private final String messageKey;

    public LoginBusinessException(String messageKey) {
        super(messageKey);
        this.messageKey = messageKey;
    }

}
