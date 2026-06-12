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
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> {}).httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/admin").hasRole("ADMIN")
                .requestMatchers("/api/delete/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/pdf/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/api/greet").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("USER", "ADMIN")
            .build();

        UserDetails user = User.builder()
            .username("user111")
            .password(passwordEncoder.encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
   /**
 * Configures CORS (Cross-Origin Resource Sharing) settings for the application.
 *
 * This method defines which external origins are allowed to access the API,
 * what HTTP methods are permitted, which headers can be used, and whether
 * credentials (cookies, authorization headers) can be included in requests.
 *
 * The configuration is applied to all endpoints (/**) in the application.
 *
 * @return CorsConfigurationSource configured with allowed origins, methods, headers, and credentials
 */
public CorsConfigurationSource corsConfigurationSource() {
    // Create a new CORS configuration object
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow requests only from the specified frontend origin (localhost:3000)
    // This prevents unauthorized cross-origin requests from other domains
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));

    // Allow these HTTP methods to be used in cross-origin requests
    // GET: retrieve data, POST: create data, PUT: update data, DELETE: remove data, OPTIONS: preflight requests
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

    // Allow any headers to be sent in requests
    // The "*" wildcard permits all standard and custom headers
    configuration.setAllowedHeaders(List.of("*"));

    // Allow credentials (cookies, authorization headers) to be sent with cross-origin requests
    // This is necessary if the frontend needs to send authentication tokens or cookies
    configuration.setAllowCredentials(true);

    // Create a URL-based CORS configuration source
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    // Register the CORS configuration for all application endpoints
    // The "/**" pattern matches all paths in the application
    source.registerCorsConfiguration("/**", configuration);

    return source;
}
}