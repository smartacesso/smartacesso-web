package br.com.startjob.acesso.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "smartacesso")
public class SmartAcessoProperties {

    private Security security = new Security();
    private Audit audit = new Audit();
    private Bootstrap bootstrap = new Bootstrap();

    @Getter
    @Setter
    public static class Security {
        private Jwt jwt = new Jwt();
        private RateLimit loginRateLimit = new RateLimit();
        private DesktopApiMode desktopApiMode = DesktopApiMode.COMPAT;
        private boolean passwordUpgradeOnLogin;
        private boolean includePasswordHashInDesktopLogin;

        @Getter
        @Setter
        public static class Jwt {
            private String secret;
            private Duration expiration;
        }

        @Getter
        @Setter
        public static class RateLimit {
            private int capacity;
            private Duration window;
        }
    }

    public enum DesktopApiMode {
        COMPAT,
        REQUIRED
    }

    @Getter
    @Setter
    public static class Audit {
        private boolean loginEvents;
    }

    @Getter
    @Setter
    public static class Bootstrap {
        private boolean enabled;
        private String unidade;
        private String adminLogin;
        private String adminPassword;
        private String adminNome;
        private String appLogin;
        private String appPassword;
        private String appNome;
    }
}
