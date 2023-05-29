package in.avinashsaraf.camunda.sso.keycloak;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.camunda.bpm.engine.authorization.Authorization.AUTH_TYPE_GRANT;
import static org.camunda.bpm.engine.authorization.Permissions.ACCESS;
import static org.camunda.bpm.engine.authorization.Resources.APPLICATION;

import in.avinashsaraf.camunda.sso.security.model.GroupAuthorizationSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Resources;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CamundaGroupAuthorizationService {

  public static final String GROUP_ID_PREFIX = "CAMUNDA_ACCESS_";
  private static final String WELCOME_APP = "welcome";
  private static final Set<String> APPS = Set.of("cockpit", "tasklist", "admin");

  private AuthorizationService authorizationService;

  public Set<String> getAuthorizedApps(final String userId, final List<String> groupIds, final ProcessEngine processEngine) {
    processEngine.getProcessEngineConfiguration().setAuthorizationEnabled(true);
    final var authorizedApps = APPS.stream()
        .filter(application -> authorizationService.isUserAuthorized(userId, groupIds, ACCESS, APPLICATION, application))
        .collect(Collectors.toSet());
    authorizedApps.add(WELCOME_APP);
    return authorizedApps;
  }

  public void deleteCamundaGroupAuthorizations() {
    authorizationService.createAuthorizationQuery()
        .list().stream()
        .filter(authorization -> startsWith(authorization.getGroupId(), GROUP_ID_PREFIX))
        .map(Authorization::getId)
        .forEach(authorizationService::deleteAuthorization);
    log.info("Deleted camunda group authorizations");
  }

  public void saveGroupAuthorizationSet(final GroupAuthorizationSet groupAuthorizationSets) {
    mapToCamundaAuthorizations(groupAuthorizationSets).forEach(authorizationService::saveAuthorization);
    log.info("Successfully created authorizations");
  }

  private Stream<Authorization> mapToCamundaAuthorizations(final GroupAuthorizationSet groupAuthorizationSet) {
    return groupAuthorizationSet.authorizations().stream().map(auth -> {
          final var camundaAuthorization = authorizationService.createNewAuthorization(AUTH_TYPE_GRANT);
          auth.getPermissions().forEach(camundaAuthorization::addPermission);
          camundaAuthorization.setGroupId(groupAuthorizationSet.groupId());
          camundaAuthorization.setResource(auth.getResourceType());
          camundaAuthorization.setResourceId(auth.getResourceId());
          return camundaAuthorization;
        }
    );
  }

}
