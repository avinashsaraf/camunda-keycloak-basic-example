package in.avinashsaraf.camunda.sso.security.model;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.camunda.bpm.engine.authorization.Permissions;

@SuperBuilder
@NoArgsConstructor
public class DefaultAuthorization extends Authorization<Permissions> {

}
