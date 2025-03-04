package com.example.canvassing.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/response").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/household").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/household").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/questionnaire").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf().disable()
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        List<UserDetails> userDetails = new ArrayList<>();
        UserDetails canvasserDetails = User.withDefaultPasswordEncoder()
                .username("joe_canvasser")
                .password("password")
                .roles("CANVASSER")
                .build();
        UserDetails adminDetails = User.withDefaultPasswordEncoder()
                .username("susan_admin")
                .password("password")
                .roles("CANVASSER","ADMIN")
                .build();
        userDetails.add(canvasserDetails);
        userDetails.add(adminDetails);

        return new InMemoryUserDetailsManager(userDetails);
    }

}