package in.avinashsaraf.camunda.sso.security.model;

import java.util.List;
import org.camunda.bpm.engine.authorization.Permission;

public record GroupAuthorizationSet(String groupId, List<Authorization<? extends Permission>> authorizations) {

}
