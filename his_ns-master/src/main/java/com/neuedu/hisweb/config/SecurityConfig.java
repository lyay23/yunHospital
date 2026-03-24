package com.neuedu.hisweb.config;

import com.neuedu.hisweb.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/user/login", "/user/logout", "/customer/login", "/yunapp/**", "/upload/**", "/ai/**").permitAll()
                .requestMatchers("/user/**").hasRole("ADMIN")
                .requestMatchers("/scheduling/**", "/department/**", "/constantType/**", "/constantItem/**", "/rule/**",
                        "/registlevel/**", "/settlecategory/**", "/disecategory/**", "/disease/**", "/expenseclass/**",
                        "/fmeditem/**").hasAnyRole("ADMIN")
                .requestMatchers("/register/**", "/invoice/**", "/medicalcard/**", "/customer/**")
                    .hasAnyRole("ADMIN", "REGISTRAR", "CUSTOMER")
                .requestMatchers("/medicalrecord/**", "/medicaldisease/**", "/prescription/**", "/prescriptiondetailed/**",
                        "/drugstemplate/**")
                    .hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/drugs/**").hasAnyRole("ADMIN", "DOCTOR", "PHARMACIST")
                .requestMatchers("/patientcosts/**").hasAnyRole("ADMIN", "FINANCE", "REGISTRAR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
