package com.example.demo.domain.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


// JWTに関する処理を担当する
@Service
public class JwtService {

  // // 「@Value」・・・プロパティから取得する
  @Value("${jwt.secret}")
  private String secret;// JWTトークンの署名に使用する秘密鍵
  // // private static final String SECRET_KEY =
  // // "W0jkyDxVmK3nrDlHkM6Spu+haQTqYKH3IpWvDoPDQtIpBAC5bguQIdIO63fOkHMH";
  //
  @Value("${jwt.expiration}")
  private Long expiration;// JWTトークンの有効期限(秒)

  // private static final String SECRET_KEY =
  // "W0jkyDxVmK3nrDlHkM6Spu+haQTqYKH3IpWvDoPDQtIpBAC5bguQIdIO63fOkHMH";

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extractClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String revocateToken(UserDetails userDetails) {
    return revocateToken(new HashMap<>(), userDetails);
  }

  public String revocateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extractClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
