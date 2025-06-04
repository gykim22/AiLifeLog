package com.pnu.ailifelogv2.config;

import com.pnu.ailifelogv2.component.jwt.JwtAccessDeniedHandler;
import com.pnu.ailifelogv2.component.jwt.JwtAuthenticateFilter;
import com.pnu.ailifelogv2.component.jwt.JwtAuthenticationEntryPoint;
import com.pnu.ailifelogv2.component.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // PasswordEncoder는 BCryptPasswordEncoder를 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, CorsConfigurationSource corsConfigurationSource) throws Exception {
        return httpSecurity
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // token을 사용하는 방식이기 때문에 csrf를 disable 하고 session을 사용하지 않음
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // exception handling
                .exceptionHandling(
                        config->
                                config.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 인증 실패 시 핸들러
                                        .accessDeniedHandler(jwtAccessDeniedHandler) // 인가 실패 시 핸들러
                )
                // 필터 추가
                .addFilterBefore(
                        new JwtAuthenticateFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                // 요청에 대한 권한 설정
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll() // 로그인 허용
                                .requestMatchers(HttpMethod.POST, "/api/v1/auth/signup").permitAll() // 회원가입 허용
                                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                )
                .build();
    }
}
