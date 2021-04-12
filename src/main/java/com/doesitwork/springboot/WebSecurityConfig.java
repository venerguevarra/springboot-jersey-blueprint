package com.doesitwork.springboot;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.doesitwork.springboot.filter.EndpointFilter;
import com.doesitwork.springboot.security.JwtAuthenticationEntryPoint;
import com.doesitwork.springboot.security.JwtAuthenticationFilter;
import com.google.common.collect.ImmutableList;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource(name = "customUserDetailsService")
    private UserDetailsService userDetailsService;

    @Resource(name = "endpointFilter")
    private EndpointFilter endpointFilter;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder());
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ImmutableList.of("*"));
        configuration.setAllowedMethods(ImmutableList.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(ImmutableList.of("X-Frame-Options", "Origin", "Accept", "Access-Control-Request-Headers", "Access-Control-Request-Method", "Content-Type", "Authorization", "Content-Length", "X-Requested-With", "x-api-key", "x-token-key", "x-client-key", "*"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().sameOrigin();
        http.cors()
                .and()
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers("/__gtg",
                             "/authenticate/**",
                             "/register/**",
                             "/__api",
                             "/api.raml", 
                             "/actuator/*", 
                             "/web/**",
                             "/web/raml/**", 
                             "/web/raml/styles/**", 
                             "/web/raml/fonts/**",
                             "/operations/**")
                             .permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/",
                             "/favicon.ico",
                             "/**/*.png",
                             "/**/*.gif",
                             "/**/*.svg",
                             "/**/*.jpg",
                             "/**/*.html",
                             "/**/*.css",
                             "/**/*.js")
                             .permitAll()
            .anyRequest()
                .fullyAuthenticated()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(endpointFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
