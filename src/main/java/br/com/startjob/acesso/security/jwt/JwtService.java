package br.com.startjob.acesso.security.jwt;

import br.com.startjob.acesso.config.SmartAcessoProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SmartAcessoProperties properties;

    public JwtService(SmartAcessoProperties properties) {
        this.properties = properties;
    }

    public boolean isConfigured() {
        String secret = secret();
        return secret != null && !secret.isBlank();
    }

    public String generate(Long userId, String cliente, String perfil) {
        Instant now = Instant.now();
        Instant exp = now.plus(properties.security().jwt().expiration());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("cliente", cliente)
                .claim("perfil", perfil)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        byte[] bytes = requireSecret().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("JWT secret deve ter pelo menos 32 caracteres");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    private String requireSecret() {
        String secret = secret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret não configurado");
        }
        return secret;
    }

    private String secret() {
        if (properties.security() == null || properties.security().jwt() == null) {
            return null;
        }
        return properties.security().jwt().secret();
    }
}
