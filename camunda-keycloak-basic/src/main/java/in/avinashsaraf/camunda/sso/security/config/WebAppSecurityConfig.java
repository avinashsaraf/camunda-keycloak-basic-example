package in.avinashsaraf.camunda.sso.security.config;

import java.util.Collections;
import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER - 10)
public class WebAppSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf().ignoringAntMatchers("/api/**")
        .and()
        .authorizeRequests(authorizeRequests ->
            authorizeRequests
                .antMatchers("/**")
                .authenticated()
                .anyRequest()
                .permitAll()
        )
        .oauth2Login();
    return http.build();
  }

//  @SuppressWarnings({"rawtypes", "unchecked"})
//  @Bean
//  public FilterRegistrationBean containerBasedAuthenticationFilter() {
//
//    FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
//    filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
//    filterRegistration.setInitParameters(
//        Collections.singletonMap("authentication-provider", "in.avinashsaraf.camunda.sso.keycloak.KeycloakAuthenticationProvider"));
//    filterRegistration.setOrder(101); // make sure the filter is registered after the Spring Security Filter Chain
//    filterRegistration.addUrlPatterns("/camunda/app/*");
//    return filterRegistration;
//  }

}
