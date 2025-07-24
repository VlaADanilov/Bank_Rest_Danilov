package com.example.bankcards.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
@Data
public class MySecurityProperty {
    private String privateKey;

    private String publicKey;

    private Long expiration;
}
