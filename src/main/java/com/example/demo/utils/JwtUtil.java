package com.example.demo.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

  // 「@Value」・・・プロパティから取得する
  @Value("${jwt.secret}")
  private String secret;// JWTトークンの署名に使用する秘密鍵

  @Value("${jwt.expiration}")
  private Long expiration;// JWTトークンの有効期限(秒)

  // トークンからユーザー名を抽出するメソッド
  // 役割: トークンの検証と情報抽出
  // 実行タイミング: トークンの検証時
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // トークンから有効期限を抽出するメソッド
  // 役割: トークンの有効期限チェック
  // 実行タイミング: トークンの検証時
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // トークンから特定の情報を抽出するメソッド
  // 役割: トークンからの情報抽出の一般化
  // 実行タイミング: 他のextractメソッドから呼び出される
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  // トークンからすべての情報を抽出するメソッド
  // 役割: トークンのペイロード全体を取得
  // 実行タイミング: 他のextractメソッドから呼び出される
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .setSigningKey(secret)
        .parseClaimsJws(token)
        .getBody();
  }

  // トークンが有効期限切れかどうかを確認するメソッド
  // 役割: トークンの有効性チェック
  // 実行タイミング: トークンの検証時
  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // 新しいトークンを生成するメソッド
  // 役割: ユーザー認証後のトークン生成
  // 実行タイミング: ユーザーのログイン成功時
  public String generateToken(String username) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, username);
  }

  // トークンを作成するメソッド
  // 役割: JWTトークンの実際の生成
  // 実行タイミング: generateTokenメソッドから呼び出される
  private String createToken(Map<String, Object> claims, String subject) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 10時間の有効期限
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
  }

  // トークンを検証するメソッド
  // 役割: トークンの有効性確認
  // 実行タイミング: 保護されたリソースへのアクセス時
  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}
