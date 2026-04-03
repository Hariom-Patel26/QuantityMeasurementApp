package com.app.quantitymeasurement.config;

import com.app.quantitymeasurement.security.oauth2.*;
import com.app.quantitymeasurement.security.CustomUserDetailsService;
import com.app.quantitymeasurement.security.jwt.*;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration @EnableWebSecurity @EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;
    private final CustomOAuth2UserService oauth2UserService;
    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;
    private final JwtAccessDeniedHandler jwtDeniedHandler;
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2FailureHandler;
    private final CorsConfigurationSource corsSource;

    public SecurityConfig(CustomUserDetailsService userDetailsService, CustomOAuth2UserService oauth2UserService,
            JwtAuthenticationFilter jwtFilter, JwtAuthenticationEntryPoint jwtEntryPoint,
            JwtAccessDeniedHandler jwtDeniedHandler, OAuth2AuthenticationSuccessHandler oauth2SuccessHandler,
            OAuth2AuthenticationFailureHandler oauth2FailureHandler,@Qualifier("corsConfigurationSource") CorsConfigurationSource corsSource) {
        this.userDetailsService = userDetailsService; this.oauth2UserService = oauth2UserService;
        this.jwtFilter = jwtFilter; this.jwtEntryPoint = jwtEntryPoint; this.jwtDeniedHandler = jwtDeniedHandler;
        this.oauth2SuccessHandler = oauth2SuccessHandler; this.oauth2FailureHandler = oauth2FailureHandler;
        this.corsSource = corsSource;
    }

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService); p.setPasswordEncoder(passwordEncoder()); return p;
    }

    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
        return c.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(c -> c.configurationSource(corsSource))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(e -> e.authenticationEntryPoint(jwtEntryPoint).accessDeniedHandler(jwtDeniedHandler))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/me").authenticated()
                .requestMatchers("/api/v1/auth/resetPassword/**").authenticated()
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register",
                    "/api/v1/auth/forgotPassword/**", "/api/v1/auth/otp/**",
                    "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/quantities/compare",
                    "/api/v1/quantities/convert", "/api/v1/quantities/add",
                    "/api/v1/quantities/subtract", "/api/v1/quantities/divide").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/quantities/history/errored").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/quantities/history/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/quantities/count/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            )
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .oauth2Login(o -> o.userInfoEndpoint(u -> u.userService(oauth2UserService))
                .successHandler(oauth2SuccessHandler).failureHandler(oauth2FailureHandler))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}