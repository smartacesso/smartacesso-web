package br.com.startjob.acesso.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "smartacesso")
public record SmartAcessoProperties(
        Security security,
        Audit audit,
        Bootstrap bootstrap
) {
    public record Security(
            Jwt jwt,
            RateLimit loginRateLimit,
            DesktopApiMode desktopApiMode,
            boolean passwordUpgradeOnLogin,
            boolean includePasswordHashInDesktopLogin
    ) {
        public record Jwt(String secret, Duration expiration) {
        }

        public record RateLimit(int capacity, Duration window) {
        }
    }

    public enum DesktopApiMode {
        COMPAT,
        REQUIRED
    }

    public record Audit(boolean loginEvents) {
    }

    public record Bootstrap(
            boolean enabled,
            String unidade,
            String adminLogin,
            String adminPassword,
            String adminNome,
            String appLogin,
            String appPassword,
            String appNome
    ) {
    }
}
