package com.example.schoolgestapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login", 
                                "/api/auth/register", 
                                "/api/auth/forgot-password", 
                                "/api/auth/reset-password", 
                                "/api/auth/refresh",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers("/api/auth/me").authenticated()
                        .requestMatchers("/api/profils/**").authenticated()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/structure/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers("/api/structure/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/evaluations/student/**", "/api/evaluations/etudiant/**").hasAnyRole("ETUDIANT", "ADMIN")
                        .requestMatchers("/api/evaluations/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.GET, "/api/emploidutemps/**").authenticated()
                        .requestMatchers("/api/emploidutemps/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.GET, "/api/presences/stats/etudiant/**", "/api/presences/etudiant/**").authenticated()
                        .requestMatchers("/api/presences/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers("/api/travaux/**").authenticated()
                        .requestMatchers("/api/ressources/**").authenticated()
                        .requestMatchers("/api/communications/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/bulletins/etudiant/**").authenticated()
                        .requestMatchers("/api/bulletins/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/admin/dashboard/stats").hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/stockage/**").authenticated()
                        // --- Cahier de Texte ---
                        .requestMatchers(HttpMethod.GET, "/api/cahier-texte/classe/**").authenticated()
                        .requestMatchers("/api/cahier-texte/seances/**").hasAnyRole("ADMIN", "ENSEIGNANT")
                        .requestMatchers(HttpMethod.PUT, "/api/cahier-texte/*/archiver").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
