package com.example.myapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RoleBasedAuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler;

    SecurityConfig(RoleBasedAuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler) {
        this.roleBasedAuthenticationSuccessHandler = roleBasedAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/register", "/css/**", "/images/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/students/register").permitAll()

                .requestMatchers("/register").permitAll()
.requestMatchers(org.springframework.http.HttpMethod.POST, "/api/students/register").permitAll()
               
                .requestMatchers(HttpMethod.PATCH, "/api/users/me/password").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/users/staff-contacts").hasAnyRole("COUNSELLOR", "ADMIN")

                // /api/users/register now requires ADMIN — used from Manage Users to
                // create Counsellor accounts. Students self-register via /register instead.
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/admin/users").hasRole("ADMIN")

                // Any logged-in user manages their own password and can send feedback.
                .requestMatchers("/settings", "/feedback").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/feedback").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/feedback").hasRole("ADMIN")

                // Page-level access: students only see their own dashboard/submission form;
                // the dashboard and case-detail view are for Counsellors and Admins.
                .requestMatchers("/student-request-form", "/student-dashboard").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers("/dashboard", "/support-request-form").hasAnyRole("COUNSELLOR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/support-requests/stats/monthly-issues").hasAnyRole("COUNSELLOR", "ADMIN")

                // Server-side enforcement too, so this can't be reached by calling the
                // API directly even without the button visible on the student page.
                // Note: /schedule is intentionally left open to any authenticated user —
                // students use "Request Schedule", counsellors use "Follow Up" — both
                // hit this same endpoint.
                .requestMatchers(HttpMethod.PATCH, "/api/support-requests/*/notes").hasAnyRole("COUNSELLOR", "ADMIN")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(roleBasedAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout.permitAll())
        
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}

