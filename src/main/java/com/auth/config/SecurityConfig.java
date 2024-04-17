package com.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Autowired
    private DataSource dataSource;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   RememberMeServices rememberMeServices,
                                                   UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter,
                                                   RememberMeAuthenticationFilter rememberMeFilter,
                                                   PersistentTokenRepository persistentTokenRepository) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); //without this the client cannot enter login method.
        http.authorizeHttpRequests(authorize ->
                authorize.requestMatchers("/login").permitAll().anyRequest().authenticated()
        ).rememberMe(remember -> remember.rememberMeServices(rememberMeServices)
                .rememberMeParameter("remember-me")
                .tokenValiditySeconds(24 * 60 * 60)
                .tokenRepository(persistentTokenRepository));

        //remember filter
        http.addFilterBefore(usernamePasswordAuthenticationFilter, RememberMeAuthenticationFilter.class);
        http.addFilterAfter(rememberMeFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder,
                                                       RememberMeAuthenticationProvider rememberMeAuthenticationProvider){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(daoAuthenticationProvider, rememberMeAuthenticationProvider);
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder){
        UserDetails userDetails = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //remember me beans
    @Bean
    RememberMeAuthenticationFilter rememberMeFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
        return new RememberMeAuthenticationFilter(authenticationManager, rememberMeServices);
    }

    @Bean
    TokenBasedRememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        return new TokenBasedRememberMeServices("springRocks",userDetailsService);
    }
    @Bean
    RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
        return new RememberMeAuthenticationProvider("springRocks");
    }
    @Bean
    public UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter(RememberMeServices rememberMeServices,
                                                                                     AuthenticationManager authenticationManager){
        UsernamePasswordAuthenticationFilter filter = new UsernamePasswordAuthenticationFilter();
        filter.setRememberMeServices(rememberMeServices);
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

}
