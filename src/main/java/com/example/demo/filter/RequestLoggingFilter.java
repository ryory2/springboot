package com.example.demo.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
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

    // chain.doFilter() を呼び出す前にレスポンスをキャプチャする準備
    ResponseWrapper responseWrapper = new ResponseWrapper(httpResponse);

    // リクエストの情報をログに出力
    logRequestInfo(httpRequest);

    // フィルターチェーンを実行
    chain.doFilter(request, responseWrapper);

    // レスポンスの情報をログに出力
    logResponseInfo(response, responseWrapper);
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
    log.info("リクエスト - Date: {}, IP Address: {}, Method: {}, URL: {},  Parameters: {}",
        formattedDateTime, ipAddress, method, requestUrl, parameters);
  }

  private void logResponseInfo(ServletResponse response, ResponseWrapper responseWrapper) {
    // 日時
    LocalDateTime now = LocalDateTime.now();
    String formattedDateTime = formatter.format(now);

    // ステータスコードを取得
    int statusCode = responseWrapper.getStatus();

    // レスポンスボディ（JSON）を取得
    String responseBody = "";
    try {
      responseBody = responseWrapper.getResponseContent();
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
    }


    // ログ出力
    log.info("レスポンス - Date: {}, statusCode: {}, responseBody: {}", formattedDateTime, statusCode,
        responseBody);
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

  // レスポンスボディをキャプチャするラッパークラス
  private static class ResponseWrapper extends HttpServletResponseWrapper {
    private ResponseCaptureServletOutputStream outputStream;
    private PrintWriter writer;

    public ResponseWrapper(HttpServletResponse response) {
      super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (outputStream == null) {
        outputStream = new ResponseCaptureServletOutputStream(super.getOutputStream());
      }
      return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      if (writer == null) {
        writer = new PrintWriter(getOutputStream());
      }
      return writer;
    }

    public String getResponseContent() throws IOException {
      if (outputStream != null) {
        return outputStream.getContent();
      }
      return null;
    }
  }

  // レスポンスボディをキャプチャするための出力ストリーム
  private static class ResponseCaptureServletOutputStream extends ServletOutputStream {
    private final ServletOutputStream outputStream;
    private final StringBuilder content;

    public ResponseCaptureServletOutputStream(ServletOutputStream outputStream) {
      this.outputStream = outputStream;
      this.content = new StringBuilder();
    }

    @Override
    public void write(int b) throws IOException {
      outputStream.write(b);
      content.append((char) b);
    }

    public String getContent() {
      return content.toString();
    }

    @Override
    public boolean isReady() {
      // TODO 自動生成されたメソッド・スタブ
      return false;
    }

    @Override
    public void setWriteListener(WriteListener listener) {
      // TODO 自動生成されたメソッド・スタブ

    }
  }
}
