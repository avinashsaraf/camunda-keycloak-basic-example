package in.avinashsaraf.camunda.sso.keycloak;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.rest.security.auth.AuthenticationResult;
import org.camunda.bpm.engine.rest.security.auth.impl.ContainerBasedAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class KeycloakAuthenticationProvider extends ContainerBasedAuthenticationProvider {

  private final static Logger LOG = LoggerFactory.getLogger(KeycloakAuthenticationProvider.class);

  @Autowired
  private KeycloakRoleToCamundaGroupMapper keycloakRoleToCamundaGroupMapper;

  @Override
  public AuthenticationResult extractAuthenticatedUser(final HttpServletRequest request, final ProcessEngine engine) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    final OidcUser principal = (OidcUser) authentication.getPrincipal();

    final String userId = principal.getName();
    if (!StringUtils.hasLength(userId)) {
      LOG.error("Keycloak authentication failed");
      return AuthenticationResult.unsuccessful();
    }

    LOG.info("Keycloak authentication is successful for user id {}", userId);
    final AuthenticationResult authenticationResult = new AuthenticationResult(userId, true);
    final var oidcIdToken = principal.getIdToken();
    //One should never log token data in any environment - this for the demo purpose.
    LOG.info("Id token claims {}", oidcIdToken.getClaims());
    authenticationResult.setGroups(getUserGroups(oidcIdToken));

    return authenticationResult;
  }

  private List<String> getUserGroups(final OidcIdToken oidcIdToken) {
    final var claims = oidcIdToken.getClaims();
    final var keycloakUserRoles = (List<String>) claims.get("camunda-roles");
    final var camundaRoles = Optional.ofNullable(keycloakUserRoles).orElse(Collections.emptyList());
    return keycloakRoleToCamundaGroupMapper.mapRolesToCamundaGroups(camundaRoles);
  }

}
