package com.example.demo.domain.service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.demo.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * JWTに関する処理を担当するサービスクラスです。このクラスはJWTトークンの生成、検証、情報抽出などの機能を提供します。
 */
@Service
public class JwtService {

  /**
   * JWTトークンの署名に使用する秘密鍵。 「@Value」・・・プロパティから取得する
   */
  @Value("${jwt.secret}")
  private String secret;

  /**
   * アクセストークンのJWTトークンの有効期限(秒)。
   */
  @Value("${jwt.expiration}")
  private Long expiration;

  /**
   * リフレッシュトークンのJWTトークンの有効期限(秒)。
   */
  @Value("${jwt.refreshExpiration}")
  private Long refreshExpiration;

  /**
   * JWTトークンからユーザー名を抽出します。
   *
   * @param token JWTトークン
   * @return ユーザー名
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * ユーザーの詳細情報を元にJWTアクセストークンを生成します。
   *
   * @param userDetails ユーザーの詳細情報
   * @return JWTアクセストークン
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * ユーザーの詳細情報と追加のクレームを元にJWTアクセストークンを生成します。
   *
   * @param extractClaims 追加のクレーム
   * @param userDetails ユーザーの詳細情報
   * @return JWTアクセストークン
   */
  public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extractClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * ユーザーの詳細情報を元にJWTトークンを無効化します。
   *
   * @param userDetails ユーザーの詳細情報
   * @return 無効化されたJWTトークン
   */
  public String revocateToken(UserDetails userDetails) {
    return revocateToken(new HashMap<>(), userDetails);
  }

  /**
   * ユーザーの詳細情報と追加のクレームを元にJWTトークンを無効化します。
   *
   * @param extractClaims 追加のクレーム
   * @param userDetails ユーザーの詳細情報
   * @return 無効化されたJWTトークン
   */
  public String revocateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
    return Jwts.builder()
        .setClaims(extractClaims)
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis()))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * JWTトークンが有効かどうかを検証します。
   *
   * @param token JWTトークン
   * @param userDetails ユーザーの詳細情報
   * @return トークンが有効であればtrue、無効であればfalse
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && isTokenExpired(token);
  }

  /**
   * JWTトークンが期限切れかどうかを検証します。
   *
   * @param token JWTトークン
   * @return トークンが期限切れであればtrue、そうでなければ例外
   */
  private boolean isTokenExpired(String token) {
    LocalDateTime expiration = extractExpiration(token).toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime();
    if (expiration.isAfter(LocalDateTime.now())) {
      return true;
    } else {
      throw new TokenExpiredException("Token has expired");
    }
  }


  /**
   * JWTトークンの有効期限を抽出します。
   *
   * @param token JWTトークン
   * @return トークンの有効期限
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * JWTトークンから全てのクレームを抽出します。
   *
   * @param token JWTトークン
   * @return クレーム
   */
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * JWTトークンから特定のクレームを抽出します。
   *
   * @param token JWTトークン
   * @param claimsResolver クレームを解決するための関数
   * @param <T> クレームの型
   * @return 特定のクレーム
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * 署名に使用する鍵を取得します。
   *
   * @return 署名に使用する鍵
   */
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
