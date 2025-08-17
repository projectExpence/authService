package org.example.auth;

import lombok.Data;
// IMPORTANT: Add imports for the new classes
import org.example.service.UserDetailsServiceImplement;
import org.example.service.GoogleUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@Data
public class SecurityConfig {

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserDetailsServiceImplement userDetailsServiceImplement;


    @Configuration
    @Order(1)
    public static class JwtApiSecurityConfig {

        @Autowired
        private JwtAuthFilter jwtAuthFilter;

        @Bean
        public SecurityFilterChain jwtApiFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
            http
                    .securityMatcher("/auth/v1/**", "/api/**","/auth/admin/**")
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource))
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/auth/admin/signup","/auth/admin/login").permitAll()
                            .requestMatchers("/auth/admin/**").hasRole("ADMIN")
                            .requestMatchers("/auth/v1/login", "/auth/v1/refreshToken", "/auth/v1/signup").permitAll()
                            .anyRequest().authenticated()
                    )
                    .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .httpBasic(AbstractHttpConfigurer::disable)
                    .formLogin(AbstractHttpConfigurer::disable);

            return http.build();
        }
    }


    @Configuration
    @Order(2)
    public static class WebSecurityConfig {

        // --- STEP 1: INJECT YOUR NEW CUSTOM COMPONENTS ---
        @Autowired
        private GoogleUserService customOAuth2GoogleUserService;

        @Autowired
        private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;


        @Bean
        public SecurityFilterChain oauth2WebFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/", "/login**", "/oauth2/**").permitAll() // Added /oauth2/**
                            .anyRequest().authenticated()
                    )
                    // --- STEP 2: REPLACE THE DEFAULT OAUTH2 LOGIN WITH YOUR CUSTOMIZED VERSION ---
                    .oauth2Login(oauth2 -> {
                        oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2GoogleUserService)); // Use custom service to save user
                        oauth2.successHandler(customOAuth2SuccessHandler); // Use custom handler to generate tokens and redirect
                    });
            return http.build();
        }
    }


    // --- Beans required for JWT/API authentication ---
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsServiceImplement);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Your frontend URL
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "x-auth-token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}