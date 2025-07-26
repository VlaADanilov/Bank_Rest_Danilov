package com.example.bankcards.service;

import com.example.bankcards.config.property.MySecurityProperty;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.util.WorkWithJwt;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WorkWithJwtTest {

    private WorkWithJwt workWithJwt;

    @BeforeEach
    void setUp() {
        MySecurityProperty property = new MySecurityProperty();
        String privateKeyStr = """
                -----BEGIN PRIVATE KEY-----
                MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDNGKvSkv8Wyhxo
                Tb7fiHGQnLtN+S4mJ9EXKLCQK8vMpEszoX1WRnTtJgYUqejuPhJqQCm6qzxcK90V
                vPlcE6RWRx/pQozzSBQu7irukFa9GpshBUWfG/E58R6D89Yk2PMD170D7s9MtdSX
                zR9y/aUGYBOABd0eHeanvJmv2bvQYNmhVp43AtSQf526/C5QNZ7PEdcA2WqCNJpi
                g4peILfmZ9mkCF9G7J+HAHiMNaJbdejMgkgyQaDPTOiijxXnErWr9BcQQ46ipOAT
                gqkf8EhegbMEgSVx3CKC0VtSTFtFP86Q1PJh9EQtS39+aKarw60LX1VJPZ4VlSdK
                SC4si3qlAgMBAAECggEAck9w3NGfKWWGhb/nRZE3sqEYaDIcvZltmVnNZt1yHmHn
                pCQ6xk5ItzB9LSEyOS8RdnTCjU8JOlocRBRqax5rdsb3izvTcXjX/vwxo3uBTK66
                jaHxluetAF1aY7MQxMLCV37z03BTy+6ASgdCowWp566+z5j1D+cceprMkc1qN43z
                7v8Z25HA3j2NFHl6XQceYmqGH8XP99sMHp7awNVvwPfKBNcMjiIQ8U7v9osypPO5
                1bA7HAxvofKLXrh7rHzrRFf98baA0M3CJ6K2Hl3SOe05F0jYLHpWHPoL7cweNDax
                3UbhuysJLglsY+HznW+v8UzAQ2OPBWytrlQ9XLhmlQKBgQDnLcPIZhfDXwNB/CRi
                8vtwsRKe6WAfOAs5mC9nuiqkBayK16mgjqzVeU/wYvl1iVHb6/eY34iwHunkwH2H
                IKQKB2TaieMIrsoKkxWdEFLmJYpL7ETayUYaZvdCjIW56nrDyLd0zxt11zc7kM46
                XqLabeRo7j4OwC3EGg9svX6KcwKBgQDjHf/W9Cqxw/JfXJf7pjeyPOmCjE5h/hrp
                pd7b9jgGDeI7shoJquEsfhCfq9g35Kz9vF7OU6v4PAe6H6V70TpwD8RWge417ye0
                I2yF8EJU+KAHBoEiEB6ISQquMfdMIr6NOztYw7nVO2vq94gpkgw+OVZwo+NZBlX9
                2F7bJfeohwKBgGEkfvevxt6HB2Rr4pJdzkCtd8ilAoo7z6O1Cn6I4TApXja9pv7b
                1U6i+m9l97NnL6m9Pz7S96oiY2CHbnggxC2eq8eVV3ynjijhY5yrZlplffanYsuY
                9kdT6yTgzbSxDkNFDaoc+UoMVJ5IHBC1AbPNp8RcEGG8ab7UwJSOECV3AoGAHWeT
                6Ruhr3REHA6b6svhaCU0wl8yAsPobhm4mdft9vKzOLFdZ6UFFEKaGmIgU1Q7BXru
                912j5Ta4dWOFhFtZaorQC7c0xSzaghsYANtTazbpWR+BdoyJt3FlfZogf238J8Lk
                cyCcHbxcw4Yaze8HwMKhq2G8nJkSZxQta8gloV8CgYEAyMiY9373tK3kspGFVQVR
                j2HZ4XTJWw15so4x0bBjloONrApibcwOTZmxzDaKHZxVOdJR1Pd7xwd2Io70ncgz
                X5KdwBrMUeAKSE6Aa1KTQ4JjnEw6PtQ2J0mrQA3BX3SYNLnzRWuv0X/E830cAq2t
                scBEf6mKHHSmNcL5o8DQiA4=
                -----END PRIVATE KEY-----
                """
                ;
        String publicKeyStr = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzRir0pL/FsocaE2+34hx
                kJy7TfkuJifRFyiwkCvLzKRLM6F9VkZ07SYGFKno7j4SakApuqs8XCvdFbz5XBOk
                Vkcf6UKM80gULu4q7pBWvRqbIQVFnxvxOfEeg/PWJNjzA9e9A+7PTLXUl80fcv2l
                BmATgAXdHh3mp7yZr9m70GDZoVaeNwLUkH+duvwuUDWezxHXANlqgjSaYoOKXiC3
                5mfZpAhfRuyfhwB4jDWiW3XozIJIMkGgz0zooo8V5xK1q/QXEEOOoqTgE4KpH/BI
                XoGzBIElcdwigtFbUkxbRT/OkNTyYfRELUt/fmimq8OtC19VST2eFZUnSkguLIt6
                pQIDAQAB
                -----END PUBLIC KEY-----
                """;
        Long expiration = 3600L * 1000; // 1 hour in ms

        property.setPrivateKey(privateKeyStr);
        property.setPublicKey(publicKeyStr);
        property.setExpiration(expiration);

        workWithJwt = new WorkWithJwt(property);
    }

    @Test
    void testGetUserIdFromToken() {
        UUID userId = UUID.randomUUID();
        String token = workWithJwt.generateAccessToken(userId, Role.USER);

        UUID result = workWithJwt.getUserIdFromToken(token);

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void testGetRoleFromToken() {
        UUID userId = UUID.randomUUID();
        String token = workWithJwt.generateAccessToken(userId, Role.ADMIN);

        Role role = workWithJwt.getRoleFromToken(token);

        assertThat(role).isEqualTo(Role.ADMIN);
    }

    @Test
    void testValidateToken_validToken_returnsTrue() {
        UUID userId = UUID.randomUUID();
        String token = workWithJwt.generateAccessToken(userId, Role.USER);

        boolean valid = workWithJwt.validateToken(token);

        assertThat(valid).isTrue();
    }

    @Test
    void testValidateToken_invalidToken_returnsFalse() {
        String invalidToken = "invalid.token.here";

        boolean valid = workWithJwt.validateToken(invalidToken);

        assertThat(valid).isFalse();
    }

    @Test
    void testExtractClaim() {
        UUID userId = UUID.randomUUID();
        String token = workWithJwt.generateAccessToken(userId, Role.USER);

        String subject = workWithJwt.extractClaim(token, Claims::getSubject);

        assertThat(subject).isEqualTo(userId.toString());
    }

    @Test
    void testParseClaims() {
        UUID userId = UUID.randomUUID();
        String token = workWithJwt.generateAccessToken(userId, Role.USER);

        Claims claims = workWithJwt.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
    }

    @Test
    void testGenerateAccessToken() {
        UUID userId = UUID.randomUUID();
        Role role = Role.USER;
        String token = workWithJwt.generateAccessToken(userId, role);

        Claims claims = workWithJwt.parseClaims(token);
        assertThat(claims.getSubject()).isEqualTo(userId.toString());
        assertThat(claims.get("role", String.class)).isEqualTo("USER");
        assertThat(claims.getExpiration().getTime())
                .isGreaterThan(new Date().getTime());
    }
}
