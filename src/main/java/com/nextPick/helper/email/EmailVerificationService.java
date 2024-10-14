package com.nextPick.helper.email;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Random;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;
    private final JavaMailSender mailSender;  // JavaMailSender 추가
    private final TemplateEngine templateEngine;  // Thymeleaf 템플릿 엔진 추가
    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Value("${email.super-code:SUPER_CODE}")
    private String superCode;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    // 이메일로 인증 코드를 전송하는 메서드 (HTML 템플릿 사용)
    public void sendCodeToEmail(String toEmail) {
        // 이메일 중복 검사
        if (memberRepository.existsByEmail(toEmail)) {
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
        }

        // 인증 코드 생성
        String authCode = this.createCode();

        // 더미계정 생성을 위한 authCode 노출 (개발 환경용)
        System.out.println("*".repeat(30));
        System.out.println("Email authCode : " + authCode);
        System.out.println("*".repeat(30));

        // Redis에 인증 코드를 저장, 설정된 시간(authCodeExpirationMillis) 이후에 자동으로 만료됨
        redisUtil.setValues(AUTH_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(authCodeExpirationMillis));

        // 인증 코드와 함께 HTML 템플릿을 사용하여 이메일 전송
        try {
            sendVerificationEmail(toEmail, authCode);
        } catch (MessagingException e) {
            throw new BusinessLogicException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    // 인증 코드를 이메일로 전송하는 메서드 (HTML 템플릿 적용)
    private void sendVerificationEmail(String toEmail, String authCode) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 이메일 수신자와 제목 설정
        helper.setTo(toEmail);
        helper.setSubject("NEXTPICK 이메일 인증입니다.");

        // Thymeleaf Context에 인증 코드를 전달
        Context context = new Context();
        context.setVariable("code", authCode);  // 인증 코드 설정

        // HTML 템플릿을 처리하여 이메일 내용 생성
        String htmlContent = templateEngine.process("email.html", context);

        // 이메일 내용 설정 (HTML 형식)
        helper.setText(htmlContent, true);

        // 이메일 전송
        mailSender.send(message);
    }

    // 사용자가 제출한 인증 코드가 맞는지 검증하는 메서드
    public boolean verifyCode(String email, String authCode) {
        String redisAuthCode = redisUtil.getValues(AUTH_CODE_PREFIX + email);
        return redisUtil.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
    }

    // 인증 코드 생성 메서드
    private String createCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessLogicException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    // 슈퍼 코드 검증 메서드
    public boolean verifySuperCode(String email, String inputCode) {
        if (superCode.equals(inputCode)) {
            return true;
        }

        String redisAuthCode = redisUtil.getValues(AUTH_CODE_PREFIX + email);
        return redisUtil.checkExistsValue(redisAuthCode) && redisAuthCode.equals(inputCode);
    }
}
