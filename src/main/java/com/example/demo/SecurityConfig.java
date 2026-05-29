package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("entered into securityconfig");
        http
                 //Disable CSRF for REST APIs (use with caution in production. This is standard practice for stateless REST APIs
                // (e.g., APIs using JWTs or HTTP Basic Auth where there are no session cookies).
                // However, as the comment in the code suggests, you should keep CSRF enabled
                // if your API relies on browser-managed session cookies.)
    .csrf(csrf -> csrf.disable())

                // Enable CORS and link it to the corsConfigurationSource bean automatically
                .cors(cors -> {}).httpBasic(Customizer.withDefaults())

                // Define URL authorization rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/admin").hasRole("ADMIN")
                        .requestMatchers("/api/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/greet").permitAll()
                        .anyRequest().authenticated()
                );

//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/delete/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(Customizer.withDefaults());

        return http.build();
    }


    // 1. Define custom users and roles
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
                .username("admin")
                // Hashes the password "admin123"
                .password(passwordEncoder.encode("admin123"))
                .roles("USER","ADMIN")
                .build();

        UserDetails user = User.builder()
                .username("user111")
                // Hashes the password "user123"
                .password(passwordEncoder.encode("user123"))
                .roles("USER")
                .build();
        System.out.println("in userDetailsService method");

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("in passwordEncoder method"); return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow your frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));

        // HTTP Methods allowed
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow cookies and authentication headers
        configuration.setAllowCredentials(true);

        // Apply this CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
