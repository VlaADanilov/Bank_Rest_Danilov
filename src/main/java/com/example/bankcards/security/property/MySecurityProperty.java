package com.example.bankcards.security.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
@Data
public class MySecurityProperty {
    private String privateKey;

    private String publicKey;

    private Long expiration;
}
