package backend.src.configuration;

import backend.src.configuration.jwt.AuthEntryPointJwt;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String ADMIN = "ADMIN";
    private static final String MANAGER = "MANAGER";

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
                .exceptionHandling((exepciontHandling) -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))

                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(request -> request.getRequestURI().matches("/")).permitAll()
                        .requestMatchers(request -> request.getRequestURI().startsWith("/api/v1/auth")).permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher("^(?!api|public|css|js|images|swagger|v3|h2-console)[^\\.]*")).permitAll()
                        .requestMatchers(RegexRequestMatcher.regexMatcher("(.*\\..+)$")).permitAll()
                );

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
