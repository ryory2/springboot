package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.demo.filter.JwtAuthenticationFilter;

// セキュリティに関する設定
@Configuration
public class SecurityConfig {

  @Autowired
  private final User user;

  public SecurityConfig(User user) {
    this.user = user;
  }

  // この設定を使用すると、/api/v1/loginへのアクセスは認証を必要とせず、他の全てのリクエストには認証が必要となります。また、フォームログインとHTTP
  // Basic認証の両方がサポートされています。
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    // セキュリティポリシーの設定
    // CSRF設定
    http
        .csrf(csrf -> csrf
            // // CSRF（Cross-Site Request Forgery）保護を無効にする設定
            // // CSRF保護が無効になると、リクエストにCSRFトークンを含める必要がなくなる
            .disable());

    // 認証・認可設定
    // .authorizeHttpRequests(authorize -> authorize ... )によって、リクエストの認可設定
    http
        .authorizeHttpRequests(authorize -> authorize
            // 特定のリクエストパスに対するアクセス許可を設定しています。
            // "/api/v1/login"パスに対して、認証なしでアクセスを許可しています。
            // ・「.requestMatchers("/api/v1/login")」・・・「/api/v1/login」のリクエスト
            // ・「.authenticated()」認証の必要なし
            .requestMatchers("/api/v1/login")
            .permitAll()
            // 他の全てのリクエストに対しては、認証が必要であることを設定しています。
            // ログイン後に認証済みのユーザーにのみ、アクセスを許可します。
            // 逆に対象の配下だけ認証が必要のように設定したい場合、「.requestMatchers("/api/**").authenticated());」とすると、/api/配下の場合に認証が必要になる。
            // ・「.anyRequest()」すべてのリクエスト
            // ・「.authenticated()」認証が必要
            .anyRequest()
            .authenticated());

    // セッション管理
    // セッションの管理ポリシーを「状態レス（Stateless）」に設定
    // サーバー側でセッションを保持しない
    // .sessionManagement(management -> managementでセッション管理を設定
    // sessionCreationPolicy(SessionCreationPolicy.STATELESS)メソッドを呼び出して、セッションの生成ポリシーを「状態レス」に設定。
    http
        .sessionManagement(management -> management
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    // フォームベースのログイン機能を有効にします。これにより、自動生成されたログインページが提供され、ユーザーはそこで認証情報を入力できます。
    // http
    // .formLogin(withDefaults());
    // HTTP Basic認証を有効にします。この方法では、ブラウザなどのクライアントが認証情報を送信するための標準的な方法を提供します。
    // http
    // .httpBasic(withDefaults());

    // 認証フィルター
    // 認証方式の設定
    // 他にフォームベース認証、OAuth2認証、APIキー認証も設定可能
    // ここではJWT認証フィルターを設定
    http
        .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    //
    // 最後に、httpオブジェクトをビルドしてSecurityFilterChainを返します。
    // return http
    // .build();
    return http
        .build();
  }


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // このメソッド全体が@Beanアノテーションでマークされているため、SpringコンテナにUserDetailsServiceのBeanとして登録
  @Bean
  public UserDetailsService userDetailsService() {
    UserDetails user = User
        // デフォルトのパスワードエンコーダを設定
        .withDefaultPasswordEncoder()
        // /ユーザー名を"user"に設定
        .username("user")
        // パスワードを"password"に設定
        .password("password")
        // ユーザーに"USER"ロールを割り当て
        .roles("USER")
        // オブジェクトを作成
        .build();
    System.out
        .printf("password -> [%s]\n", passwordEncoder()
            .encode("password"));
    return new InMemoryUserDetailsManager(user);
  }

  @Bean
  // 認証処理を行う中心的な役割を担っています。
  // Springでは、AuthenticationManagerはデフォルトではBeanとして自動的に提供されません。そのため、明示的にBeanとして定義する必要があります。
  // 上記のコードでは、AuthenticationConfigurationを使用して、Springが提供するAuthenticationManagerを取得し、それをBeanとして定義しています。
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
      throws Exception {
    return config
        .getAuthenticationManager();
  }
}
