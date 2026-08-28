package br.com.startjob.acesso.security.legacy;

import org.springframework.stereotype.Component;

/**
 * Token legado {@code timestamp-id-hashSenha} usado pelo desktop e apps pedestre.
 * A expiração de 30 minutos está comentada no legado e permanece desligada
 * para não quebrar clientes em campo.
 */
@Component
public class LegacyDesktopTokenService {

    public String issue(Long userId, String storedPasswordHash) {
        return System.currentTimeMillis() + "-" + userId + "-" + storedPasswordHash;
    }

    public ParsedToken parse(String token) {
        if (token == null) {
            return null;
        }
        String[] parts = token.split("-");
        if (parts.length != 3) {
            return null;
        }
        try {
            return new ParsedToken(Long.parseLong(parts[0]), Long.parseLong(parts[1]), parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean matchesStoredHash(ParsedToken parsed, String storedHash) {
        return parsed != null && storedHash != null && storedHash.equals(parsed.hash());
    }

    public record ParsedToken(long issuedAt, long userId, String hash) {
    }
}
