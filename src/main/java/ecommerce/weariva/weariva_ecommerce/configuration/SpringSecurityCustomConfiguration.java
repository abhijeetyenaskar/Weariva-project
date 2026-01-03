package ecommerce.weariva.weariva_ecommerce.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import ecommerce.weariva.weariva_ecommerce.services.CustomAuthenticator;
import ecommerce.weariva.weariva_ecommerce.services.CustomFailureAuthenticationHandler;
import ecommerce.weariva.weariva_ecommerce.services.CustomLogoutSuccessHandler;
import ecommerce.weariva.weariva_ecommerce.services.userdetailservices.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableAsync
public class SpringSecurityCustomConfiguration {

    private final CustomAuthenticator customAuthenticator;
    private final CustomFailureAuthenticationHandler customFailureAuthenticationHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final CustomUserDetailsService customUserDetailsService;

    // It is used to encrypt passwords.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // It is used by Authentication Manager for authentication.
    @Bean
    AuthenticationProvider authenticationProvider(UserDetailsService customUserSercvice) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserSercvice);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    // Custom Security Filter Layers for users.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.requestMatchers("/user/**", "/api/user/**")
                        .hasAnyAuthority("USER").requestMatchers("/admin/**", "/api/admin/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/api/commonadminboy/**").hasAnyAuthority("ADMIN", "DELIVERY")
                        .requestMatchers("/boy/**", "/api/boy/**").hasAnyAuthority("DELIVERY").requestMatchers("/**")
                        .permitAll());
        httpSecurity.authenticationProvider(authenticationProvider(customUserDetailsService));

        httpSecurity.formLogin(
                form -> form.loginPage("/signin")
                        .loginProcessingUrl("/login")
                        .failureHandler(customFailureAuthenticationHandler)
                        .successHandler(customAuthenticator));
        httpSecurity.logout(logout -> logout.logoutUrl("/logout").logoutSuccessHandler(customLogoutSuccessHandler)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID"));

        httpSecurity.exceptionHandling(ex -> ex.accessDeniedPage("/unAuthenticated"));
        return httpSecurity.build();
    }
}
