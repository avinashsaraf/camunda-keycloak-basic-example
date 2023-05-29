package in.avinashsaraf.camunda.sso.keycloak;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response.Status;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.webapp.impl.security.auth.Authentication;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.camunda.bpm.webapp.impl.security.auth.UserAuthentication;

@RequiredArgsConstructor
public class KeycloakContainerBasedAuthenticationFilter extends ContainerBasedAuthenticationFilter {

  private final KeycloakAuthenticationProvider keycloakAuthenticationProvider;
  private final CamundaGroupAuthorizationService camundaGroupAuthorizationService;

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
    final var httpServletRequest = (HttpServletRequest) request;
    final var httpServletResponse = (HttpServletResponse) response;
    final var engineName = extractEngineName(httpServletRequest);
    if (Objects.isNull(engineName)) {
      chain.doFilter(request, response);
      return;
    }

    final var engine = getAddressedEngine(engineName);
    if (engine == null) {
      httpServletResponse.sendError(404, String.format("Process engine %s not available", engineName));
      return;
    }

    final var authenticationResult = keycloakAuthenticationProvider.extractAuthenticatedUser(httpServletRequest, engine);
    if (!authenticationResult.isAuthenticated()) {
      httpServletResponse.setStatus(Status.UNAUTHORIZED.getStatusCode());
      keycloakAuthenticationProvider.augmentResponseByAuthenticationChallenge(httpServletResponse, engine);
      return;
    }

    final var authentications = Authentications.getFromSession(httpServletRequest.getSession());
    final var authenticatedUser = authenticationResult.getAuthenticatedUser();
    if (!existisAuthentication(authentications, engineName, authenticatedUser)) {
      final var authentication = createUserAuthentication(authenticatedUser, engine, authenticationResult.getGroups());
      //this is the authentication that goes in camunda session for all camunda request auth
      authentications.addAuthentication(authentication);
    }
    chain.doFilter(request, response);
  }

  private Authentication createUserAuthentication(final String userId, final ProcessEngine processEngine, final List<String> groupIds) {
    final var userAuthentication = new UserAuthentication(userId, processEngine.getName());
    userAuthentication.setGroupIds(groupIds);
    final var authorizedApps = camundaGroupAuthorizationService.getAuthorizedApps(userId, groupIds, processEngine);
    userAuthentication.setAuthorizedApps(authorizedApps);
    return userAuthentication;
  }

}
