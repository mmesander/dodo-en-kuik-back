package nl.dodo_en_kuik.backend.security.config;

import nl.dodo_en_kuik.backend.security.filter.JwtRequestFilter;
import nl.dodo_en_kuik.backend.services.CustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {
    private final CustomUserDetailService customUserDetailService;
    private final JwtRequestFilter jwtRequestFilter;

    public SpringSecurityConfig(
            CustomUserDetailService customUserDetailService,
            JwtRequestFilter jwtRequestFilter
    ) {
        this.customUserDetailService = customUserDetailService;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        var auth = new DaoAuthenticationProvider();
        auth.setPasswordEncoder(passwordEncoder);
        auth.setUserDetailsService(customUserDetailService);
        return new ProviderManager(auth);
    }

    @Bean
    protected SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/users/auth/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/users/auth/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/users/auth/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PATCH, "/users/auth/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/users/auth/**").hasAnyRole("ADMIN", "USER")

                        .requestMatchers(HttpMethod.GET, "/users/{username}/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users/{username}/authorities").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/{username}/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/{username}/**").hasRole("ADMIN")

                        .requestMatchers("/authenticated").authenticated()
                        .requestMatchers("/users/authenticate").permitAll()
                        .requestMatchers("users/register").permitAll()
                        .anyRequest().denyAll()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}