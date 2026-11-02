package fiap.com.br.vetflow.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final VetFlowUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // NOVO — expõe o AuthenticationManager como bean, necessário pro
    // AuthApiController conseguir autenticar manualmente no /api/auth/login.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // API REST usa autenticação stateless por sessão simples (via login form),
                // então CSRF fica desabilitado só para as rotas /api/** (chamadas programáticas).
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Recursos públicos
                        .requestMatchers("/login", "/css/**", "/js/**", "/webjars/**", "/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/h2-console/**").permitAll()

                        // NOVO — libera login/registro via JSON para o app mobile.
                        // Só os 4 endpoints abaixo ficam públicos; todo o resto de /api/** continua exigindo login.
                        .requestMatchers("/api/auth/**").permitAll()

                        // Área exclusiva do veterinário/clínica
                        .requestMatchers("/web/clinics/**", "/web/appointments/manage/**").hasRole("VET")

                        // Área que tutor e vet podem acessar (cada um vê o que é seu)
                        .requestMatchers("/web/**").hasAnyRole("TUTOR", "VET")
                        .requestMatchers("/api/**").hasAnyRole("TUTOR", "VET")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/web/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // Necessário para o H2 Console renderizar em <frame>
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}