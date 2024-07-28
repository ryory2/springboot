package com.example.demo.filter;

import java.io.IOException;
import java.util.Locale;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.demo.domain.service.JwtService;
import com.example.demo.dto.api.CustomErrorResponse;
import com.example.demo.exception.TokenExpiredException;
import com.example.demo.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  private final MessageSource messageSource;

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
    try {
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

        // 次のフィルターチェーンにリクエストとレスポンスを渡す
        filterChain.doFilter(request, response);
      }
    } catch (ExpiredJwtException | TokenExpiredException e) {
      log.warn("エラー：トークン期限切れ");
      CustomErrorResponse errorResponse =
          Utils.createErrorResponse(
              HttpStatus.FORBIDDEN, HttpStatus.FORBIDDEN.getReasonPhrase(), messageSource
                  .getMessage("error.exception.tokenexpiredexception", null, Locale.getDefault()),
              request);
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.setContentType("application/json");
      response.getWriter()
          .write(objectMapper.writeValueAsString(errorResponse));
    } catch (SignatureException | MalformedJwtException e) {
      log.warn("エラー：JWTの署名が無効か、形式が正しくない");
      // JWTの署名が無効か、形式が正しくない場合
      CustomErrorResponse errorResponse =
          Utils.createErrorResponse(HttpStatus.FORBIDDEN, "Invalid JWT",
              messageSource.getMessage("error.exception.invalidtoken", null, Locale.getDefault()),
              request);
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.setContentType("application/json");
      response.getWriter()
          .write(objectMapper.writeValueAsString(errorResponse));
    } catch (Exception e) {
      log.warn("エラー：例外");
      CustomErrorResponse errorResponse = Utils.createErrorResponse(
          HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
          messageSource.getMessage("error.exception.internalserver", null, Locale.getDefault()),
          request);
      response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
      response.setContentType("application/json");
      response.getWriter()
          .write(objectMapper.writeValueAsString(errorResponse));
    }
  }

}
