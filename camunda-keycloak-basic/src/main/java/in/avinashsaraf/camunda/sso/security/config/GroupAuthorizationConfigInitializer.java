package in.avinashsaraf.camunda.sso.security.config;

import in.avinashsaraf.camunda.sso.keycloak.CamundaGroupAuthorizationService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GroupAuthorizationConfigInitializer {

  private final GroupAuthorizationConfigReader groupAuthorizationConfigReader;
  private final CamundaGroupAuthorizationService camundaGroupAuthorizationService;

  @Transactional
  @EventListener(ApplicationStartedEvent.class)
  public void recreateGroupAuthorizations() throws IOException {
    camundaGroupAuthorizationService.deleteCamundaGroupAuthorizations();
    groupAuthorizationConfigReader.readGroupAuthorizationSets().forEach(camundaGroupAuthorizationService::saveGroupAuthorizationSet);
  }

}
