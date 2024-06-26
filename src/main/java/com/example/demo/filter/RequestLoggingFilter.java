package com.example.demo.filter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebFilter("/*")
public class RequestLoggingFilter implements Filter {

  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    log.info("RequestLoggingFilter initialized");
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    // リクエストの情報をログに出力
    logRequestInfo(httpRequest);

    // フィルターチェーンを実行
    chain.doFilter(request, response);

    // レスポンスの情報をログに出力
    logResponseInfo(httpResponse);
  }

  @Override
  public void destroy() {
    log.info("RequestLoggingFilter destroyed");
  }

  private void logRequestInfo(HttpServletRequest request) {
    // 日時
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    // リクエストURL
    String requestUrl = request.getRequestURL()
        .toString();

    // パラメータ
    String parameters = formatParameters(request.getParameterMap());

    // ユーザ情報（IPアドレス）
    String ipAddress = request.getRemoteAddr();

    // メソッド
    String method = request.getMethod();

    // ログ出力
    log.info("リクエスト開始 - Date: {}, IP Address: {}, Method: {}, URL: {},  Parameters: {}",
        formattedDateTime, ipAddress, method, requestUrl, parameters);
  }

  private void logResponseInfo(HttpServletResponse response) {
    // レスポンスの情報をログに出力する処理（必要に応じて実装）
    log.info("レスポンス開始");
  }

  private String formatParameters(Map<String, String[]> parameterMap) {
    StringBuilder formattedParams = new StringBuilder();
    parameterMap.forEach((name, values) -> {
      for (int i = 0; i < values.length; i++) {
        if (i > 0) {
          formattedParams.append(", ");
        }
        formattedParams.append(name)
            .append(":")
            .append(values[i]);
      }
    });
    return formattedParams.toString();
  }
}
