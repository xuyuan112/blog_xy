package com.xuyuan.blog.web.custom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xuyuan.blog.common.result.R;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AppSecurityConfig {

    private final AppJwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/doc.html",
                                "/webjars/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/favicon.ico",
                                "/xuyuan/app/auth/register",
                                "/xuyuan/app/auth/login",
                                "/xuyuan/app/auth/refresh",
                                "/xuyuan/app/auth/captcha",
                                "/xuyuan/auth/register",
                                "/xuyuan/auth/login",
                                "/xuyuan/auth/refresh",
                                "/xuyuan/auth/captcha"
                        ).permitAll()
                        .requestMatchers(
                                "/xuyuan/app/auth/userinfo",
                                "/xuyuan/app/auth/logout",
                                "/xuyuan/app/auth/password",
                                "/xuyuan/auth/userinfo",
                                "/xuyuan/auth/logout",
                                "/xuyuan/auth/password"
                        ).authenticated()
                        // Public content, comments and IP-based likes do not require an account.
                        // Accounts are only used when a caller opts into named identity features.
                        .requestMatchers("/xuyuan/comments/**", "/xuyuan/likes/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/xuyuan/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/xuyuan/friends/apply").permitAll()
                        .anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(objectMapper.writeValueAsString(R.fail(603, "请先登录后再进行该操作")));
                }))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
