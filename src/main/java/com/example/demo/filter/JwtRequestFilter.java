package com.example.demo.filter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.domain.service.MyUserDetailsService;
import com.example.demo.utils.JwtUtil;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

  @Autowired
  private MyUserDetailsService userDetailsService;

  @Autowired
  private JwtUtil jwtUtil;

  // 各リクエストに対して実行されるフィルターメソッド
  // 役割: JWTトークンの検証と認証の設定
  // 実行タイミング: 各HTTPリクエストが処理される前
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {

    // Authorization ヘッダーからJWTトークンを取得
    // 役割: リクエストヘッダーからトークンを抽出
    // 実行タイミング: 各リクエスト時
    final String authorizationHeader = request.getHeader("Authorization");

    String username = null;
    String jwt = null;

    // JWTトークンの存在確認と形式チェック
    // 役割: トークンの形式を確認し、ユーザー名を抽出
    // 実行タイミング: Authorization ヘッダーが存在し、"Bearer "で始まる場合
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      jwt = authorizationHeader.substring(7);
      username = jwtUtil.extractUsername(jwt);
    }

    // ユーザー名が存在し、現在の認証コンテキストが空の場合の処理
    // 役割: トークンの検証と認証の設定
    // 実行タイミング: 有効なユーザー名が抽出され、現在の認証コンテキストが空の場合
    if (username != null && SecurityContextHolder.getContext()
        .getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

      // トークンが有効な場合、Spring Securityの認証を設定
      // 役割: 認証オブジェクトの作成と設定
      // 実行タイミング: トークンが有効であると確認された場合
      if (jwtUtil.validateToken(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());
        usernamePasswordAuthenticationToken
            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext()
            .setAuthentication(usernamePasswordAuthenticationToken);
      }
    }
    // フィルターチェーンの次のフィルターを呼び出す
    // 役割: リクエスト処理の継続
    // 実行タイミング: このフィルターの処理が完了した後
    chain.doFilter(request, response);
  }
}
