package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.DisableEncodeUrlFilter;
import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.filter.RequestResponseLoggingFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


// セキュリティに関する設定
// 「@Configuration」・・・起動時に実行される
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final AuthenticationProvider authenticationProvider;
  private final RequestResponseLoggingFilter requestResponseLoggingFilter;

  // 認証マネージャーの設定
  // 役割: ユーザー詳細サービスとパスワードエンコーダーの設定
  // 実行タイミング: アプリケーション起動時
  // @Override
  // protected void configure(AuthenticationManagerBuilder auth) throws Exception {
  // auth.userDetailsService(myUserDetailsService);
  // }
  // @Bean
  // AuthenticationManager authManager(HttpSecurity http) throws Exception {
  // AuthenticationManagerBuilder authenticationManagerBuilder =
  // http.getSharedObject(AuthenticationManagerBuilder.class);
  // authenticationManagerBuilder.authenticationProvider(authProvider);
  // return authenticationManagerBuilder.build();
  // }

  // セキュリティ設定
  // 役割: HTTPセキュリティの設定
  // 実行タイミング: アプリケーション起動時
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // セキュリティポリシーの設定
    // CSRF設定
    http.csrf(csrf -> csrf
        // // CSRF（Cross-Site Request Forgery）保護を無効にする設定
        // // CSRF保護が無効になると、リクエストにCSRFトークンを含める必要がなくなる
        .disable());

    // 認証・認可設定
    // .authorizeHttpRequests(authorize -> authorize ... )によって、リクエストの認可設定
    http.authorizeHttpRequests(authorize -> authorize
        // 特定のリクエストパスに対するアクセス許可を設定しています。
        // "/api/v1/login"パスに対して、認証なしでアクセスを許可しています。
        // ・「.requestMatchers("/api/v1/login")」・・・「/api/v1/login」のリクエスト
        // ・「.authenticated()」認証の必要なし
        .requestMatchers("/api/v1/auth/**")
        .permitAll()
        .requestMatchers("/api/v1/auth/jwt")
        .authenticated() // "/api/v1/auth/jwt" のみ要認証
        .requestMatchers("/api/v1/otp/**")
        .permitAll()
        // 他の全てのリクエストに対しては、認証が必要であることを設定しています。
        // ログイン後に認証済みのユーザーにのみ、アクセスを許可します。
        // 逆に対象の配下だけ認証が必要のように設定したい場合、「.requestMatchers("/api/**").authenticated());」とすると、/api/配下の場合に認証が必要になる。
        // ・「.anyRequest()」すべてのリクエスト
        // ・「.authenticated()」認証が必要
        .anyRequest()
        .authenticated())
        // 認証プロバイダーを指定する
        .authenticationProvider(authenticationProvider);

    // セッション管理
    // セッションの管理ポリシーを「状態レス（Stateless）」に設定
    // サーバー側でセッションを保持しない
    // .sessionManagement(management -> managementでセッション管理を設定
    // sessionCreationPolicy(SessionCreationPolicy.STATELESS)メソッドを呼び出して、セッションの生成ポリシーを「状態レス」に設定。
    http.sessionManagement(
        management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // 認証フィルター
    // 認証方式の設定
    // 他にフォームベース認証、OAuth2認証、APIキー認証も設定可能
    // ここではJWT認証フィルターを設定

    // フィルターの順番設定
    // RequestResponseLoggingFilter（→jwtAuthFilter）→UsernamePasswordAuthenticationFilter(基準)
    // JwtAuthFilterの前に、「JwtRequestFilter()」フィルターが実行される
    // http.addFilterAfter(jwtAuthFilter, RequestResponseLoggingFilter.class);
    // UsernamePasswordAuthenticationFilterの前に、「JwtRequestFilter()」フィルターが実行される
    // SecurityFilterChain実行前に、リクエストのログを出力
    http.addFilterBefore(requestResponseLoggingFilter, DisableEncodeUrlFilter.class);
    // SecurityFilterChainの中で、ログイン認証実行前に、jwtトークンの検証を行う
    http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    // 最後に、httpオブジェクトをビルドしてSecurityFilterChainを返します。
    // return http
    // .build();
    return http.build();
  }

  // パスワードエンコーダーの設定
  // 役割: パスワードの暗号化方式の指定
  // 実行タイミング: アプリケーション起動時
  // @Bean
  // public PasswordEncoder passwordEncoder() {
  // return new BCryptPasswordEncoder();
  // }

  // 認証マネージャーのBean定義
  // 役割: 認証マネージャーの提供
  // 実行タイミング: アプリケーション起動時

  // 認証処理を行う中心的な役割を担っています。
  // Springでは、AuthenticationManagerはデフォルトではBeanとして自動的に提供されません。そのため、明示的にBeanとして定義する必要があります。
  // 上記のコードでは、AuthenticationConfigurationを使用して、Springが提供するAuthenticationManagerを取得し、それをBeanとして定義しています。
  // @Bean
  // public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
  // throws Exception {
  // return config.getAuthenticationManager();
  // }
}
