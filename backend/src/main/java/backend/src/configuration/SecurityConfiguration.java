package backend.src.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import backend.src.configuration.jwt.AccessDeniedHandlerJwt;
import backend.src.configuration.jwt.AuthEntryPointJwt;
import backend.src.configuration.jwt.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final AccessDeniedHandlerJwt accessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(accessDeniedHandler))

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                    .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/signup").permitAll()
                        .requestMatchers("/api/v1/managers").hasRole("ADMIN")
                        .requestMatchers("/api/v1/profile/manager").hasRole("MANAGER")
                        .requestMatchers("/api/v1/auth/logout", "/api/v1/auth/me", "/api/v1/profile/password").authenticated()
                        // `/api/v1/teams/my` requires an authenticated manager. This must
                        // be checked before the generic `teams/**` permitAll rule.
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/teams/my").hasRole("MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/v1/teams/**").hasRole("MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/v1/teams/**").hasRole("MANAGER")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/teams/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/matches/**").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/players/**").permitAll()
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
