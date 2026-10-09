package br.com.startjob.acesso.entrypoint.api.security.password;

import br.com.startjob.acesso.core.gateway.PasswordGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PasswordGatewayImpl implements PasswordGateway {

    private final PasswordHasher passwordHasher;

    @Override
    public String hash(String rawPassword) {
        return passwordHasher.hash(rawPassword);
    }
}
