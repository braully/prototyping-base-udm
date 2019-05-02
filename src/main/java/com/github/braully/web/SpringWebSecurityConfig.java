package com.github.braully.web;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CompositeFilter;

/**
 *
 * @author braully
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
public class SpringWebSecurityConfig extends WebSecurityConfigurerAdapter {

    //TODO: 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/login").permitAll()
                .antMatchers("/*.jsp", "/**/*.jsp", "/**/*.html", "/assets/**",
                        "/javax.faces.resource/**", "/pkg/**",
                        "/login**", "/logout**", "/error**").permitAll()
                .antMatchers("/**/*.xhtml", "/**/*.jsf", "/inicio", "/app/**").authenticated()
                .and()
                .formLogin()
                .loginProcessingUrl("/login")
                .loginPage("/entrar")
                .defaultSuccessUrl("/index").permitAll().and()
                .logout().deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll();
        http.exceptionHandling().accessDeniedPage("/access-denied.jsp");
        http.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
    }

    /* Ouath2 -- Integration facebook, google  */
    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Bean
    public FilterRegistrationBean<OAuth2ClientContextFilter>
            oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean<OAuth2ClientContextFilter> registration
                = new FilterRegistrationBean<OAuth2ClientContextFilter>();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @Bean
    @ConfigurationProperties("spring.security.oauth2.client.registration.facebook")
    public SpringOAuth2ClientResources facebook() {
        return new SpringOAuth2ClientResources();
    }

    @Bean
    @ConfigurationProperties("spring.security.oauth2.client.registration.github")
    public SpringOAuth2ClientResources github() {
        return new SpringOAuth2ClientResources();
    }

    private Filter ssoFilter(SpringOAuth2ClientResources client, String path) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(path);
        OAuth2RestTemplate template = new OAuth2RestTemplate(client.getClient(), oauth2ClientContext);
        filter.setRestTemplate(template);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(
                client.getResource().getUserInfoUri(), client.getClient().getClientId());
        tokenServices.setRestTemplate(template);
        filter.setTokenServices(tokenServices);
        return filter;
    }

    public Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(ssoFilter(facebook(), "/login/facebook"));
        filters.add(ssoFilter(github(), "/login/github"));
        filter.setFilters(filters);
        return filter;
    }

}
