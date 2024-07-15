package com.example.demo.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Enumeration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

  private static final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    // リクエストの情報をログに出力
    logRequestInfo(request);
    if (log.isDebugEnabled()) {
      logRequestDetails(request);
    }

    // 次のフィルターチェーンにリクエストとレスポンスを渡す
    filterChain.doFilter(request, response);

    // レスポンスの情報をログに出力
    logResponseInfo(response);
    if (log.isDebugEnabled()) {
      logResponseDetails(response);
    }
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
    log.info("START --- {} {} {} {}", formattedDateTime, ipAddress, method, requestUrl);
  }


  private void logResponseInfo(HttpServletResponse servletResponse) {
    // 日時
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    // HTTPステータス
    int status = servletResponse.getStatus();

    // ログ出力
    log.info(" END  --- {} {}", formattedDateTime, status);
  }

  private void logRequestDetails(HttpServletRequest request) {
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    StringBuilder sb = new StringBuilder();
    sb.append("Request Details at ")
        .append(formattedDateTime)
        .append(":\n")
        .append("  Method: ")
        .append(request.getMethod())
        .append("\n")
        .append("  URL: ")
        .append(request.getRequestURL())
        .append("\n")
        .append("  Query String: ")
        .append(request.getQueryString())
        .append("\n")
        .append("  IP Address: ")
        .append(request.getRemoteAddr())
        .append("\n")
        .append("  Headers: ");

    // Logging request headers
    Enumeration<String> headers = request.getHeaderNames();
    while (headers.hasMoreElements()) {
      String header = headers.nextElement();
      sb.append("\n    ")
          .append(header)
          .append(": ")
          .append(Collections.list(request.getHeaders(header)));
    }

    // Optionally include request parameters if needed
    sb.append("\n  Parameters: ");
    request.getParameterMap()
        .forEach((key, value) -> sb.append("\n    ")
            .append(key)
            .append(": ")
            .append(String.join(", ", value)));

    log.debug(sb.toString());
  }

  private void logResponseDetails(HttpServletResponse response) {
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    StringBuilder sb = new StringBuilder();
    sb.append("Response Details at ")
        .append(formattedDateTime)
        .append(":\n")
        .append("  Status: ")
        .append(response.getStatus())
        .append("\n")
        .append("  Headers: ");

    // Logging response headers
    response.getHeaderNames()
        .forEach(header -> sb.append("\n    ")
            .append(header)
            .append(": ")
            .append(response.getHeader(header)));

    log.debug(sb.toString());
  }

}
