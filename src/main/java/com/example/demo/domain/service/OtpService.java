package com.example.demo.domain.service;


import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.example.demo.domain.model.Otp;
import com.example.demo.domain.repository.OtpRepository;
import com.example.demo.dto.api.auth.SendOtpRequest;
import com.example.demo.dto.api.auth.SendOtpResponse;
import com.example.demo.exception.SendTimesExceededException;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;

@Service
@RequiredArgsConstructor
public class OtpService {
  @Autowired
  private MessageSource messageSource;
  private final OtpRepository otpRepository;
  private final int OTP_IS_NOT_USED = 0;

  @Transactional
  public SendOtpResponse sendOtp(SendOtpRequest request) {
    // メールアドレスからレコードを取得
    Optional<Otp> optionalOtp = otpRepository.findByMail(request.getMail());

    String code = generateOtp();

    Otp newOtp = Otp.builder()
        .mail(request.getMail())
        .code(code)
        .expiredAt(LocalDateTime.now()
            .plusMinutes(10))
        .failedTimes(0)
        .isUsed(OTP_IS_NOT_USED)
        .build();

    // 発行済みの場合
    if (optionalOtp.isPresent()) {
      Otp otp = optionalOtp.get();

      // 送信期間チェック（初回送信から30分経っていたら初回送信日を更新）
      if (otp.getFirstSendAt()
          .plusMinutes(30)
          .isBefore(LocalDateTime.now())) {
        newOtp.setId(otp.getId());
        newOtp.setFirstSendAt(LocalDateTime.now());
        newOtp.setSendTimes(1);
      } else {

        // 送信回数チェック
        if (otp.getSendTimes() >= 10) {
          throw new SendTimesExceededException("送信回数が上限を超えました。");
        }
        // 発行済として送信回数+1して上書き
        newOtp.setId(otp.getId());
        newOtp.setFirstSendAt(otp.getFirstSendAt());
        newOtp.setSendTimes(otp.getSendTimes() + 1);
      }


    } else {
      newOtp.setFirstSendAt(LocalDateTime.now());
      newOtp.setSendTimes(1);
    }

    otpRepository.save(newOtp);

    DefaultCredentialsProvider provider = DefaultCredentialsProvider.create();

    SesV2Client sesClient = SesV2Client.builder()
        .credentialsProvider(provider)
        .region(Region.AP_NORTHEAST_1)
        .build();

    String from = "hideyoshi.adm@gmail.com";
    String to = "jojohunter.mean@gmail.com";
    String subject = "【重要】ログイン用ワンタイムパスワード（OTP）のご案内";
    String message =
        String.format(messageSource.getMessage("message.otp", null, Locale.JAPAN), optionalOtp.get()
            .getMail(), code);

    String body = String.format(message, to, code);

    send(sesClient, from, to, subject, body);
    return SendOtpResponse.builder()
        .code(code)
        .build();
  }



  public static void send(SesV2Client client, String sender, String recipient, String subject,
      String bodyHTML) {

    Destination destination = Destination.builder()
        .toAddresses(recipient)
        .build();

    Content content = Content.builder()
        .data(bodyHTML)
        .build();

    Content sub = Content.builder()
        .data(subject)
        .build();

    Body body = Body.builder()
        .html(content)
        .build();

    Message msg = Message.builder()
        .subject(sub)
        .body(body)
        .build();

    EmailContent emailContent = EmailContent.builder()
        .simple(msg)
        .build();

    SendEmailRequest emailRequest = SendEmailRequest.builder()
        .destination(destination)
        .content(emailContent)
        .fromEmailAddress(sender)
        .build();


    client.sendEmail(emailRequest);
  }

  private String generateOtp() {
    // Randomクラスのインスタンスを生成
    Random random = new Random();

    // 0から999999までの範囲のランダムな整数を生成
    int randomNumber = random.nextInt(1000000);

    // 6桁の数値としてゼロ埋めして表示
    return String.format("%06d", randomNumber);
  }

  // public AuthenticationResponse register(RegisterRequest request) {
  // var user = User.builder()
  // .mail(request.getMail())
  // .password(passwordEncoder.encode(request.getPassword()))
  // .role(Role.USER)
  // .build();
  // Optional<User> existingUser = userRepository.findByMail(user.getMail());
  // if (existingUser.isPresent()) {
  // throw new UserAlreadyExistsException(
  // "User with email " + user.getMail() + " already exists.");
  // }
  // userRepository.save(user);
  // var jwtToken = jwtService.generateToken(user);
  // return AuthenticationResponse.builder()
  // .token(jwtToken)
  // .build();
  // }

  // // カスタムの認証プロバイダー
  // public AuthenticationResponse authenticate(AuthenticationRequest request) {
  // String jwtAccessToken = null;
  // try {
  // authenticationManager.authenticate(
  // new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
  // var user = userRepository.findByMail(request.getMail())
  // .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  // jwtAccessToken = jwtService.generateToken(user);
  // } catch (UserNotFoundException e) {
  // throw new UserNotFoundException("No users found in the database");
  // } catch (BadCredentialsException e) {
  // e.printStackTrace();
  // throw new BadCredentialsException(
  // messageSource.getMessage("error.exception.badcredentials", null, Locale.getDefault()));
  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // return AuthenticationResponse.builder()
  // .token(jwtAccessToken)
  // .build();
  // }
  //
  // public AuthenticationResponse logout(AuthenticationRequest request) {
  // authenticationManager.authenticate(
  // new UsernamePasswordAuthenticationToken(request.getMail(), request.getPassword()));
  // var user = userRepository.findByMail(request.getMail())
  // .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  // var jwtToken = jwtService.revocateToken(user);
  // return AuthenticationResponse.builder()
  // .token(jwtToken)
  // .build();
  // }

  // // JTWトークンの認証を行う
  // public ResponseEntity<Void> authenticateJwt() {
  // try {
  // } catch (Exception e) {
  // }
  // return ResponseEntity.noContent()
  // .build();
  // }
}
