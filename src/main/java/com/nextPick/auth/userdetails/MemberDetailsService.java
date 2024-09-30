package com.nextPick.auth.userdetails;

import org.springframework.security.core.userdetails.UserDetailsService;

public class MemberDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils authorityUtils;
}
