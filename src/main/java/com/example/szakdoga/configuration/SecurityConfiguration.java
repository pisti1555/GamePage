package com.example.szakdoga.configuration;

import com.example.szakdoga.service.UserService;
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
                .csrf((csrf) -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/js/**", "/img/**", "/css/**").permitAll()
                            .requestMatchers(HttpMethod.POST).permitAll()
                            .requestMatchers("/").permitAll()
                            .requestMatchers("/ez").authenticated()
                            .requestMatchers("/user/**").authenticated()
                            .requestMatchers("/{id}").permitAll()
                            .requestMatchers("/register").permitAll()
                            .requestMatchers("/lobby/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/lobby/invite").permitAll()
                            .anyRequest().permitAll();
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
