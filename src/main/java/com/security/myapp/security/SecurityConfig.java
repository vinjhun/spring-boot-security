/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.security.myapp.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.security.myapp.model.ApiErrorResponse;
import com.security.myapp.security.handler.CustomAuthFailureHandler;
import com.security.myapp.security.handler.CustomAuthSuccessHandler;
import com.security.myapp.security.handler.CustomAuthenticationHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * test
 */
@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {

    @Autowired
    private CustomAuthSuccessHandler successHandler;

    @Autowired
    private CustomAuthFailureHandler failureHandler;

    final ObjectMapper mapper = Jackson2ObjectMapperBuilder.json()
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();

    Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new CustomEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CustomAuthenticationHandler authenticationFilter() {
        CustomAuthenticationHandler filter = new CustomAuthenticationHandler();
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/auth/login", "POST"));
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        filter.setSessionAuthenticationStrategy(authenticationStrategy());
        return filter;
    }

    @Bean
    public CompositeSessionAuthenticationStrategy authenticationStrategy() {
        List<SessionAuthenticationStrategy> authenticationStrategyList = new ArrayList<>();
        authenticationStrategyList.add(new RegisterSessionAuthenticationStrategy(sessionRegistry()));

        //concurrent strategy
        ConcurrentSessionControlAuthenticationStrategy concurrentStrategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry());
        concurrentStrategy.setMaximumSessions(1);
        authenticationStrategyList.add(concurrentStrategy);
        return new CompositeSessionAuthenticationStrategy(authenticationStrategyList);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.httpBasic().and()
                //.csrf().csrfTokenRepository(this.getCsrfTokenRepository())
                .csrf().disable().cors().disable();
                //.and()
                //.cors().configurationSource(corsConfig());

        http.authorizeHttpRequests(authorize ->
                        authorize.mvcMatchers("/auth/**").permitAll().anyRequest().authenticated())
                .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        http.sessionManagement()
                .invalidSessionStrategy((req, res) -> {
                    req.getSession(true);
                    //return post response
                    constructResponseBody(res);
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


    static class CustomEncoder implements PasswordEncoder{

        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return true;
        }
    }

    protected SessionInformationExpiredStrategy responseExpiredStrategy() {
        return event -> {
            HttpServletResponse response = event.getResponse();
            constructResponseBody(response);
        };
    }



    @Bean
    public CorsConfigurationSource corsConfig() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(Collections.singletonList("*"));
        cors.setAllowedMethods(Collections.singletonList("*"));
        cors.setAllowedHeaders(Collections.singletonList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);

        return source;
    }

    public CsrfTokenRepository getCsrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookiePath("/");
        return repository;
    }


    private void constructResponseBody(HttpServletResponse response) throws IOException {
        if (!response.isCommitted()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiErrorResponse err = new ApiErrorResponse();
            err.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            err.setStatusCode("1005");

            response.getWriter().write(mapper.writeValueAsString(err));
        }
    }
}
