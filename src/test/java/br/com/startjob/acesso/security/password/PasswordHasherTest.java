package br.com.startjob.acesso.security.password;

import br.com.startjob.acesso.entrypoint.api.security.password.PasswordHasher;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher();

    @Test
    void sha256Of123456MatchesLegacyEncryptionUtils() {
        assertThat(hasher.sha256Hex("123456"))
                .isEqualTo("8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92");
    }

    @Test
    void matchesLegacySha256StoredHash() {
        String stored = hasher.sha256Hex("123456");
        assertThat(hasher.matches("123456", stored)).isTrue();
        assertThat(hasher.matches("wrong", stored)).isFalse();
    }

    @Test
    void matchesBcryptStoredHash() {
        String stored = hasher.hash("secret-pass");
        assertThat(PasswordHasher.isBcrypt(stored)).isTrue();
        assertThat(hasher.matches("secret-pass", stored)).isTrue();
        assertThat(hasher.matches("other", stored)).isFalse();
    }

    @Test
    void desktopDoHashesPlainPassword() {
        String stored = hasher.sha256Hex("123456");
        assertThat(hasher.matchesPreHashedOrRaw("123456", stored, false)).isTrue();
        assertThat(hasher.matchesPreHashedOrRaw(stored, stored, true)).isTrue();
        assertThat(hasher.matchesPreHashedOrRaw("123456", stored, true)).isFalse();
    }
}
