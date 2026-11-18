package bj.inforepair.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/login", "/error").permitAll()
                .requestMatchers("/utilisateurs/**", "/parametres/entreprise").hasRole("ADMIN")
                .requestMatchers("/clients/**", "/paiements/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers("/materiels/nouveau", "/materiels/enregistrer", "/materiels/*/modifier", "/materiels/*/etat", "/materiels/*/supprimer").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers("/devis/nouveau", "/devis/enregistrer", "/devis/*/modifier", "/devis/*/accepter", "/devis/*/refuser", "/devis/*/expirer", "/devis/*/nouvelle-version").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers("/reparations/nouveau", "/reparations/demarrer", "/reparations/*/remettre").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers("/plaintes/nouveau", "/plaintes/enregistrer", "/plaintes/*/modifier", "/plaintes/*/traiter").hasAnyRole("ADMIN", "AGENT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("motDePasse")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?erreur=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?deconnexion=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/acces-refuse"));

        return http.build();
    }
}
