package br.com.startjob.acesso.core.gateway;

public interface PasswordGateway {
    String hash(String rawPassword);
}
