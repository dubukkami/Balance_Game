package com.drink.balancegame.config;

import com.drink.balancegame.security.CustomUserDetailsService;
import com.drink.balancegame.security.JwtAuthenticationEntryPoint;
import com.drink.balancegame.security.JwtAuthenticationFilter;
import com.drink.balancegame.security.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 설정 클래스
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    
    /**
     * 비밀번호 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Security Filter Chain 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/balance-games/**").permitAll()
                .requestMatchers("/api/web/**").permitAll()
                .requestMatchers("/api/mobile/**").permitAll()
                .requestMatchers("/api/comments/count/game/*").permitAll()
                .requestMatchers("/api/comments/game/*").permitAll()
                .requestMatchers("/api/comments/game/*/list").permitAll()
                .requestMatchers("/api/comments/*/replies").permitAll()
                .requestMatchers("/api/votes/balance-games/*/stats").permitAll()
                .requestMatchers("/api/users").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .requestMatchers("/api/users/login").permitAll()
                .requestMatchers("/api/users/check-**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                .requestMatchers("/login/**").permitAll()
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customUserDetailsService)
                )
                .successHandler(oAuth2AuthenticationSuccessHandler)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        // H2 Console 사용을 위한 설정
        http.headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions.disable())
        );
        
        return http.build();
    }
}