package in.avinashsaraf.camunda.sso.security.config;

import in.avinashsaraf.camunda.sso.keycloak.CamundaGroupAuthorizationService;
import in.avinashsaraf.camunda.sso.keycloak.KeycloakContainerBasedAuthenticationFilter;
import in.avinashsaraf.camunda.sso.keycloak.KeycloakAuthenticationProvider;
import java.util.Collections;
import org.camunda.bpm.extension.keycloak.plugin.KeycloakIdentityProviderPlugin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakSecurityConfig {

  private static final String[] SSO_URL_PATTERNS = {"/camunda/app/**","/camunda/app/*", "/camunda/api/**", "/camunda/api/*"};

  @Bean
  @ConfigurationProperties(prefix = "security.sso-login.keycloak-config")
  public KeycloakIdentityProviderPlugin keycloakIdentityProviderPlugin() {
    return new KeycloakIdentityProviderPlugin();
  }

  @Bean
  public FilterRegistrationBean<?> containerBasedAuthenticationFilter(
      final KeycloakAuthenticationProvider keycloakAuthenticationProvider,
      final CamundaGroupAuthorizationService camundaGroupAuthorizationService) {
    final var filterRegistration = new FilterRegistrationBean<>();
    filterRegistration.setFilter(new KeycloakContainerBasedAuthenticationFilter(keycloakAuthenticationProvider, camundaGroupAuthorizationService));
    filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider", KeycloakAuthenticationProvider.class.getName()));
    filterRegistration.setOrder(101);//set it after spring security filter chain
    filterRegistration.addUrlPatterns(SSO_URL_PATTERNS);
    return filterRegistration;
  }

}
