package com.nhnacademy.aiot.authentication.service;

import com.nhnacademy.aiot.authentication.exception.CryptoOperationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

@Slf4j
@Service
public class JwtService {

    public static final String PREFIX = "Bearer";

    @Value("${jwt.private-key}")
    private String privateKey;

    @Value("${jwt.public-key}")
    private String publicKey;

    @Getter
    @Value("${jwt.access.expiry-time}")
    private Integer accessExpiryTime;

    @Getter
    @Value("${jwt.refresh.expiry-time}")
    private Integer refreshExpiryTime;

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(getPublicKeyDecryption())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String generateAccessToken(String userId, String authority) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, accessExpiryTime);

        return generateToken(userId, authority, calendar);
    }

    public String generateRefreshToken(String userId, String authority) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, refreshExpiryTime);

        return generateToken(userId, authority, calendar);
    }

    private String generateToken(String userId, String authority, Calendar expiryTime) {
        return Jwts.builder()
                   .claim("userId", userId)
                   .claim("authority", authority)
                   .setIssuedAt(new Date())
                   .setExpiration(expiryTime.getTime())
                   .signWith(getPrivateKeyEncryption(), SignatureAlgorithm.RS256)
                   .compact();
    }

    private PrivateKey getPrivateKeyEncryption() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] keyBytes = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new CryptoOperationException("JwtService: " + e);
        }
    }

    private PublicKey getPublicKeyDecryption() {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] keyBytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
            return keyFactory.generatePublic(x509EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CryptoOperationException("JwtService: " + e);
        }
    }

    public String getPrefix() {
        return PREFIX;
    }
}
