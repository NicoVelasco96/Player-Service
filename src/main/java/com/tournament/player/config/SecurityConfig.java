package com.tournament.player.config;

import com.tournament.player.repository.IPlayerRepository;
import com.tournament.player.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private IPlayerRepository playerRepository;

    @Autowired
    private JWTService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/players/register",
                                "/api/players/login",
                                "/api/docs/**",
                                "/api/docs",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter(),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthFilter() {
        JWTService service = this.jwtService;
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain)
                    throws ServletException, IOException {

                String header = req.getHeader("Authorization");
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);
                    if (service.isValid(token)) {
                        String username = service.extractUsername(token);
                        var auth = new UsernamePasswordAuthenticationToken(
                                username, null, Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
                chain.doFilter(req, res);
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> playerRepository.findByUsername(username)
                .map(p -> User.withUsername(p.getUsername())
                        .password(p.getPasswordHash())
                        .authorities("ROLE_USER")
                        .build())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}