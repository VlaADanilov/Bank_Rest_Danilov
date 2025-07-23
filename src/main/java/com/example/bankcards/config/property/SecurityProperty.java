package com.example.bankcards.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security")
@Data
public class SecurityProperty {
    private String privateKey;

    private String publicKey;

    private Long expiration;
}
