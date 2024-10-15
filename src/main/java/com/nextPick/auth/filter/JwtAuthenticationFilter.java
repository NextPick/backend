package com.nextPick.auth.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextPick.auth.dto.LoginDto;
import com.nextPick.auth.jwt.JwtTokenizer;
import com.nextPick.exception.BusinessLogicException;
import com.nextPick.exception.ExceptionCode;
import com.nextPick.exception.ForbiddenException;
import com.nextPick.member.entity.Member;
import com.nextPick.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository; // 추가

    @Override
    @SneakyThrows
    public Authentication attemptAuthentication (HttpServletRequest request,
                                                 HttpServletResponse response){
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginDto = objectMapper
                .readValue(request.getInputStream(), LoginDto.class);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        // 인증을 위해 토큰을 사용하여 Authentication 객체 생성
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 사용자의 member_status를 확인하여 BAN 상태이면 로그인 차단
        Member member = memberRepository.findByEmail(loginDto.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (member.getStatus().equals(Member.memberStatus.BAN)) {
            throw new ForbiddenException("This account is banned.");
        }

        return authentication;
    }

    @Override
    protected  void successfulAuthentication(HttpServletRequest request,
                                             HttpServletResponse response,
                                             FilterChain chain,
                                             Authentication authResult) throws IOException, ServletException {
        Member member = (Member) authResult.getPrincipal();

        Member findMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        String accessToken = delegateAccessToken(member);
        String refreshToken = delegateRefreshToken(member);
        response.addCookie(createCookie(member.getEmail(), refreshToken));
        response.setHeader("Authorization", "Bearer " + accessToken);
        response.setHeader("Refresh", refreshToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Type", member.getType().toString());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("nickname", findMember.getNickname()); // 한글 닉네임 포함
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getWriter(), responseBody);

        System.out.println("accessToken : " + accessToken);
        System.out.println("refreshToken : " + refreshToken);
    }


    private String delegateAccessToken(Member member){
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", member.getEmail());
        claims.put("roles", member.getRoles());

        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());
        String base64encodedSecretKey = jwtTokenizer.encodedBase64SecretKey(jwtTokenizer.getSecretKey());

        String acceessKey = jwtTokenizer.generateAccessToken(claims,
                subject, expiration, base64encodedSecretKey);

        return acceessKey;
    }

    private String delegateRefreshToken(Member member){
        String subject = member.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getRefreshTokenExpirationMinutes());
        String base64encodedSecretKey = jwtTokenizer.encodedBase64SecretKey(jwtTokenizer.getSecretKey());

        String refreshKey = jwtTokenizer.generateRefreshToken(subject, expiration, base64encodedSecretKey );

        return refreshKey;
    }


    private URI createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String > quertParams = new LinkedMultiValueMap<>();
        quertParams.add("access_token", accessToken);
        quertParams.add("refresh_token", refreshToken);
        return UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host("127.0.0.1")
                .port(3000)
                .queryParams(quertParams)
                .build()
                .toUri();
    }

    public Cookie createCookie(String userName, String refreshToken) {
        String cookieName = "refreshtoken";
        String cookieValue = refreshToken; //
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // 쿠키 속성 설정
        cookie.setHttpOnly(true);  //httponly 옵션 설정
        cookie.setSecure(true); //https 옵션 설정(디폴트 false/요즘은 브라우저에서 거름)
        cookie.setPath("/"); // 모든 곳에서 쿠키열람이 가능하도록 설정
        cookie.setMaxAge(60 * 60 * 24); //쿠키 만료시간 설정
        return cookie;
    }


    }


