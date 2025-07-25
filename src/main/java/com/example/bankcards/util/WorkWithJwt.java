package com.example.bankcards.util;

import com.example.bankcards.config.property.MySecurityProperty;
import com.example.bankcards.entity.enums.Role;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;


@Component
public class WorkWithJwt {
    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    private final Long expiration;

    public WorkWithJwt(MySecurityProperty property) {
        this.privateKey = loadPrivateKey(property.getPrivateKey());
        this.publicKey = loadPublicKey(property.getPublicKey());
        this.expiration = property.getExpiration();
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(extractClaim(token, Claims::getSubject));
    }

    public Role getRoleFromToken(String token) {
        String string = extractClaim(token,
                (claims) -> claims.get("role", String.class));
        return Role.valueOf(string);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(UUID id, Role role) {
        return Jwts.builder()
                .setSubject(id.toString())
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    private RSAPublicKey loadPublicKey(String publicKeyResource) {
        try {
            String encoded = publicKeyResource.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(encoded);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private RSAPrivateKey loadPrivateKey(String privateKeyResource) {
        try {
            String encoded = privateKeyResource.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] decoded = Base64.getDecoder().decode(encoded);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) kf.generatePrivate(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
}
