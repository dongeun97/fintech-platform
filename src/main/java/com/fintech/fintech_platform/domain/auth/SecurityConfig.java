package com.fintech.fintech_platform.domain.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF 비활성화
        http.csrf(csrf -> csrf.disable());

        // 세션 사용 안 함 (JWT 사용)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // URL 접근 권한 설정
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/**").permitAll(); // 회원가입, 로그인은 누구나
            auth.anyRequest().authenticated();                     // 나머지는 로그인 필요
        });

        return http.build();
    }
}