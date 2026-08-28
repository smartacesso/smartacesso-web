package br.com.startjob.acesso.security.password;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Dual hasher: SHA-256 hex (legado) e BCrypt (novas senhas).
 * Upgrade automático fica desligado enquanto o token desktop embute o hash armazenado.
 */
@Component
public class PasswordHasher {

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(12);

    public boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || stored == null || stored.isBlank()) {
            return false;
        }
        if (isBcrypt(stored)) {
            return bcrypt.matches(rawPassword, stored);
        }
        return sha256Hex(rawPassword).equalsIgnoreCase(stored);
    }

    /**
     * {@code /login/do} envia senha em claro e o servidor aplica SHA-256.
     * {@code /login/interno} envia o hash SHA-256 já calculado (contrato legado).
     * Se o stored for BCrypt, sempre compara a senha em claro.
     */
    public boolean matchesPreHashedOrRaw(String presented, String stored, boolean presentedIsSha256) {
        if (presented == null || stored == null || stored.isBlank()) {
            return false;
        }
        if (isBcrypt(stored)) {
            return bcrypt.matches(presented, stored);
        }
        if (presentedIsSha256) {
            return presented.equalsIgnoreCase(stored);
        }
        return sha256Hex(presented).equalsIgnoreCase(stored);
    }

    public String hash(String rawPassword) {
        return bcrypt.encode(rawPassword);
    }

    public String sha256Hex(String value) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
            byte[] digest = algorithm.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02X", 0xFF & b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }

    public static boolean isBcrypt(String stored) {
        return stored != null && (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$"));
    }
}
