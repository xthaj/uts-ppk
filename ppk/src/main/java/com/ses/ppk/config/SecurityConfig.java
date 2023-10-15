package com.ses.ppk.config;

import com.ses.ppk.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/v1/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**"
    };
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                .requestMatchers(PUT, "api/v1/users/**").hasRole("ADMIN")
                                .requestMatchers(DELETE, "api/v1/users/*").hasRole("ADMIN")
                                .requestMatchers(POST, "api/v1/meetings").hasRole("ADMIN")
                                .requestMatchers(PUT, "api/v1/meetings/**").hasRole("ADMIN")
                                .requestMatchers(DELETE, "api/v1/meetings/**").hasRole("ADMIN")
                                .requestMatchers(PATCH, "api/v1/applicants/**").hasRole("ADMIN")
                                .requestMatchers(DELETE, "api/v1/applicants/**").hasRole("ADMIN")
                                .anyRequest()
                                .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                ;
        return http.build();
    }

}
