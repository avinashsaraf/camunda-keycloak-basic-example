package in.avinashsaraf.camunda.sso.keycloak;

import static in.avinashsaraf.camunda.sso.keycloak.CamundaGroupAuthorizationService.GROUP_ID_PREFIX;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeycloakRoleToCamundaGroupMapper {

  @Value("${security.sso-login.keycloak-config.camundaGroupPrefix:camunda}")
  private String camundaRolePrefix;

  public List<String> mapRolesToCamundaGroups(final Collection<String> roles) {
    final var prefixString = String.format("%s-", camundaRolePrefix);
    return roles.stream()
        .filter(role -> role.startsWith(prefixString))
        .map(role -> role.replaceFirst(prefixString, GROUP_ID_PREFIX))
        .map(String::toUpperCase)
        .toList();
  }

}
