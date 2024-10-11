package com.nextPick.helper.email2;

import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.member.repository.MemberRepository;
import com.nextPick.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EmailTool emailTool;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    @Value("${email.super-code:SUPER_CODE}")
    private String superCode;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    // 이메일로 인증 코드를 전송하는 메서드
    public void sendCodeToEmail(String toEmail) {
        //이메일 중복 검사
        if(memberRepository.existsByEmail(toEmail)) throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);

        //인증코드 생성
        String title = "유저 이메일 인증 번호";
        String authCode = this.createCode();

        //더미계정생성을 위한 authCode 노출
        System.out.println("*".repeat(30));
        System.out.println("Email authCode : " + authCode);
        System.out.println("*".repeat(30));

        // Redis에 인증 코드를 저장, 설정된 시간(authCodeExpirationMillis) 이후에 자동으로 만료됨
        redisUtil.setValues(AUTH_CODE_PREFIX + toEmail,
                authCode, Duration.ofMillis(authCodeExpirationMillis));
        // 인증 코드를 사용자의 이메일로 전송
        emailTool.sendEmail(toEmail, title, authCode);
    }

    // 사용자가 제출한 인증 코드가 맞는지 검증하는 메서드
    public boolean verifyCode(String email, String authCode) {
        String redisAuthCode = redisUtil.getValues(AUTH_CODE_PREFIX + email);

        return redisUtil.checkExistsValue(redisAuthCode) && redisAuthCode.equals(authCode);
    }

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

    public boolean verifySuperCode(String email, String inputCode) {
        if(superCode.equals(inputCode)) {
            return true;
        }

        String redisAuthCode = redisUtil.getValues(AUTH_CODE_PREFIX + email);

        return redisUtil.checkExistsValue(redisAuthCode) && redisAuthCode.equals(inputCode);
    }
}