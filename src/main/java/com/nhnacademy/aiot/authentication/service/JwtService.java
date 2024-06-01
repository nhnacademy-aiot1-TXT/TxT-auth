package com.nhnacademy.aiot.authentication.service;

import com.nhnacademy.aiot.authentication.exception.CryptoOperationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

/**
 * JWT와 관련된 서비스입니다.
 *
 * @author jongsikk
 * @version 1.0.0
 */
@Slf4j
@Service
public class JwtService {

    /**
     * TOKEN PREFIX String
     */
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

    /**
     * token의 claim을 추출합니다.
     *
     * @param token 추출할 token
     * @return {@link Claims} 인스턴스를 반환합니다.
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getPublicKeyDecryption())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * access token을 생성합니다. properties에서 받아온 값을 만료 시간으로 설정합니다.
     * properties에서 받아온 값을 만료 시간으로 설정합니다. 만료 시간의 단위는 SECOND입니다.
     *
     * @param userId    payload에 넣을 userId
     * @param authority payload에 넣을 authority
     * @return {@link String} 타입의 access token을 반환합니다.
     */
    public String generateAccessToken(String userId, String authority) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, accessExpiryTime);

        return generateToken(userId, authority, calendar);
    }

    /**
     * refresh token을 생성합니다.
     * properties에서 받아온 값을 만료 시간으로 설정합니다. 만료 시간의 단위는 DATE입니다.
     *
     * @param userId    payload에 넣을 userId
     * @param authority payload에 넣을 authority
     * @return {@link String} 타입의 refresh token을 반환합니다.
     */
    public String generateRefreshToken(String userId, String authority) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, refreshExpiryTime);

        return generateToken(userId, authority, calendar);
    }

    /**
     * userId와 authority가 payload로 들어가는 token을 생성합니다.
     *
     * @param userId     payload에 넣을 userId
     * @param authority  payload에 넣을 authority
     * @param expiryTime token의 만료 시간
     * @return {@link String} 타입의 token을 반환합니다.
     */
    private String generateToken(String userId, String authority, Calendar expiryTime) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("authority", authority)
                .setIssuedAt(new Date())
                .setExpiration(expiryTime.getTime())
                .signWith(getPrivateKeyEncryption(), SignatureAlgorithm.RS256)
                .compact();
    }

    /**
     * Base64 인코딩된 문자열에서 암호화에 사용되는 개인 키를 검색합니다.
     * 제공된 Base64 인코딩된 개인 키를 디코딩하고, RSA 알고리즘을 사용하여 재구성한 후,
     * {@link PrivateKey} 인스턴스를 반환합니다.
     *
     * @return 암호화에 사용되는 {@link PrivateKey} 인스턴스.
     * @throws CryptoOperationException 키 디코딩 과정에서 오류가 발생하거나 지정된 키 사양이
     *                                  이 키 팩토리에서 개인 키를 생성하기에 부적절한 경우 발생합니다.
     */
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

    /**
     * Base64 인코딩된 문자열에서 복호화에 사용되는 공개 키를 검색합니다.
     * 제공된 Base64 인코딩된 공개 키를 디코딩하고, RSA 알고리즘을 사용하여 재구성한 후,
     * {@link PublicKey} 인스턴스를 반환합니다.
     *
     * @return 복호화에 사용되는 {@link PublicKey} 인스턴스.
     * @throws CryptoOperationException 키 디코딩 과정에서 오류가 발생하거나 지정된 키 사양이
     *                                  이 키 팩토리에서 공개 키를 생성하기에 부적절한 경우 발생합니다.
     */
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

    /**
     * Gets prefix.
     *
     * @return JWT의 prefix를 반환합니다.
     */
    public String getPrefix() {
        return PREFIX;
    }
}
