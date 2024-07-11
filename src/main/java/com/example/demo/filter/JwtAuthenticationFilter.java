package com.example.demo.filter;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.domain.service.JwtService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    // Authorizationヘッダーからトークンを取得
    // 「final」修飾子・・・一度代入された後には変更されない
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;

    // Authorizationヘッダーが存在しないか、Bearerトークンで始まらない場合は次のフィルターに移行（jwtのフィルターを通さない）
    if (authHeader == null || !authHeader.startsWith("Bearer")) {
      filterChain.doFilter(request, response);
      return;
    }

    // BearerトークンからJWT部分のみ抽出
    jwt = authHeader.substring(7);

    // JWTからユーザーのメールアドレスを取得
    userEmail = jwtService.extractUsername(jwt);

    // ユーザーのメールアドレスが取得できて、かつ現在のセキュリティコンテキストに認証情報がない場合
    if (userEmail != null && SecurityContextHolder.getContext()
        .getAuthentication() == null) {
      // ユーザーの詳細をロードして認証トークンを生成
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        // 詳細情報を設定
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        // 認証情報をセキュリティコンテキストに設定
        SecurityContextHolder.getContext()
            .setAuthentication(authToken);
      }
    }

    // 次のフィルターチェーンにリクエストとレスポンスを渡す
    filterChain.doFilter(request, response);
  }

}
