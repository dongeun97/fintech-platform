package com.fintech.fintech_platform.domain.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fintech.fintech_platform.global.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signUp(String username, String password) {

        // 중복 아이디 체크
        if (memberRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 회원 객체 생성
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(encodedPassword);
        member.setRole(Member.Role.USER);

        // DB 저장
        memberRepository.save(member);
    }


    @Transactional(readOnly = true)
    public String login(String username, String password) {

        // 회원 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 발급
        String token = jwtUtil.createToken(username, member.getRole().name());

        return token;
    }
}
