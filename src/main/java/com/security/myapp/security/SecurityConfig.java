/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.myapp.security;

import com.security.myapp.security.handler.CustomAuthenticationHandler;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * test
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return customEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationHandler authenticationFilter() {
        CustomAuthenticationHandler filter = new CustomAuthenticationHandler();
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/login", "POST"));

        return filter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic().and()
                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //.csrf().disable().cors().disable();
                .and()
                .cors();

        http.mvcMatcher("/auth/**")
                .authorizeRequests(authorize -> authorize.anyRequest().permitAll())
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        http.sessionManagement()
                .invalidSessionStrategy((req, res) -> {
                    //return post response
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                })
                .maximumSessions(1)
                .expiredSessionStrategy(responseExpiredStrategy())
                .maxSessionsPreventsLogin(true)
                .sessionRegistry(sessionRegistry());

        return http.build();
    }

    @Bean
    public SessionRegistryImpl sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder customEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return true;
            }
        };
    }

    protected SessionInformationExpiredStrategy responseExpiredStrategy() {
        return new SessionInformationExpiredStrategy() {
            @Override
            public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
                HttpServletResponse response = event.getResponse();
                //wrote response body

            }
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedMethods("*");
            }
        };
    }
}
