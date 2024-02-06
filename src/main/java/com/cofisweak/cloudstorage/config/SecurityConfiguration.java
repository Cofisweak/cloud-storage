package com.cofisweak.cloudstorage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(configurer ->
                        configurer
                                .requestMatchers("/auth/register").permitAll()
                                .requestMatchers("/css/**", "/images/**", "/js/**", "favicon.ico").permitAll()
                                .requestMatchers("robots.txt").denyAll()
                                .anyRequest().authenticated()
                )
                .formLogin(configurer ->
                        configurer
                                .loginPage("/auth/login")
                                .defaultSuccessUrl("/", true)
                                .permitAll()
                )
                .logout(configurer ->
                        configurer
                                .logoutUrl("/auth/logout")
                                .logoutSuccessUrl("/auth/login")
                                .permitAll()
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .build();
    }
}
