package project.gamepage.configuration;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import project.gamepage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserService userService;

    @Autowired
    public SecurityConfiguration(UserService service) {
        userService = service;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/favicon.ico").permitAll()
                            .requestMatchers("/js/**", "/img/**", "/css/**").permitAll()
                            .requestMatchers(HttpMethod.POST).permitAll()
                            .requestMatchers("/register/**").permitAll()
                            .anyRequest().authenticated();
                        })
                .formLogin(login -> {
                    login
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/", true).permitAll();
                })
                .logout(logout -> {
                    logout
                            .invalidateHttpSession(true)
                            .clearAuthentication(true)
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                            .logoutSuccessUrl("/login?logout");
                });
        return http.build();
    }
}
