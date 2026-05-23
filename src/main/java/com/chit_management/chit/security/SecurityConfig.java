package com.chit_management.chit.security;

import com.chit_management.chit.exception.AccessDeniedHandlerImpl;
import com.chit_management.chit.jwt.JwtAuthEntryPoint;
import com.chit_management.chit.jwt.JwtFilter;
import com.chit_management.chit.serviceImpl.CustomUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;        // ✅ final added
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsServiceImpl userDetailsService;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())

                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/**"))

                .logout(logout -> logout.disable())
                .formLogin(form -> form.disable())

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth

                        // ── Public ────────────────────────────────
                        .requestMatchers(
                                "/login", "/login/",
                                "/error", "/error-page",
                                "/session-expired",
                                "/api/auth/login",
                                "/api/auth/logout",
                                "/css/**", "/js/**",
                                "/images/**", "/favicon.ico", "/webfonts/**"
                        ).permitAll()

                        // ── ADMIN only ────────────────────────────
                        // Chit plan create/update/delete
                        .requestMatchers(HttpMethod.POST,
                                "/chit-plans/new",
                                "/api/chit-plans/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/chit-plans/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/chit-plans/**",
                                "/api/customers/**").hasRole("ADMIN")

                        // Reports
                        .requestMatchers(
                                "/reports/**",
                                "/api/reports/**").hasRole("ADMIN")

                        // ── ADMIN + AGENT ─────────────────────────
                        // Customers
                        .requestMatchers(
                                "/customers/**",
                                "/api/customers/**").hasAnyRole("ADMIN", "AGENT")

                        // Payments
                        .requestMatchers(
                                "/payments/**",
                                "/api/payments/**").hasAnyRole("ADMIN", "AGENT")

                        // Enrollments
                        .requestMatchers(
                                "/enrollments/**",
                                "/api/enrollments/**").hasAnyRole("ADMIN", "AGENT")

                        // Dashboard
                        .requestMatchers("/", "/dashboard").hasAnyRole("ADMIN", "AGENT")

                        // ── Everything else needs authentication ──
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}