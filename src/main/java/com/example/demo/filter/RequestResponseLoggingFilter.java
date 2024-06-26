package com.example.demo.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestResponseLoggingFilter implements Filter {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
      FilterChain filterChain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    // リクエストの情報をログに出力
    logRequestInfo(request);

    // フィルターチェーンを実行
    filterChain.doFilter(request, servletResponse);

    // レスポンスの情報をログに出力
    logResponseInfo(response);
  }

  private void logRequestInfo(HttpServletRequest request) {
    // 日時
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    // IPアドレス
    String ipAddress = request.getRemoteAddr();

    // HTTPメソッド
    String method = request.getMethod();

    // リクエストURL
    String requestUrl = request.getRequestURL()
        .toString();

    // ログ出力
    log.info("リクエスト - Date: {}, IP Address: {}, Method: {}, URL: {}", formattedDateTime, ipAddress,
        method, requestUrl);
  }


  private void logResponseInfo(HttpServletResponse servletResponse) {
    // 日時
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    // HTTPステータス
    int status = servletResponse.getStatus();

    // ログ出力
    log.info("レスポンス - Date: {}, Status: {}", formattedDateTime, status);
  }


}
