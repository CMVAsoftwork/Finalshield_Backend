package com.finalshield.FinalShield.Config;

import com.finalshield.FinalShield.Security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("*"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(false);
                    return corsConfig;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/.well-known/assetlinks.json").permitAll()
                        .requestMatchers(HttpMethod.HEAD, "/api/enlaces/*/validar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/biometrico-activo/{correo}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/registro", "/api/auth/habilitar-biometrico", "/api/auth/login-bio").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/enlaces/*/validar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/enlaces/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/enlaces/logo").permitAll()
                        .requestMatchers("/error", "/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                    response.sendError(
                                            HttpServletResponse.SC_UNAUTHORIZED,
                                            "No autorizado o token inválido"
                                    )
                        ));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
