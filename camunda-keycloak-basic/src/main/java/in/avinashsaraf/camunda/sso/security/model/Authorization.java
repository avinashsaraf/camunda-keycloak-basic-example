package in.avinashsaraf.camunda.sso.security.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.camunda.bpm.engine.authorization.Permission;
import org.camunda.bpm.engine.authorization.Resources;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = Id.NAME, property = "resourceType", visible = true, defaultImpl = DefaultAuthorization.class)
@SuperBuilder
public abstract class Authorization<T extends Permission> {

  private Resources resourceType;
  private String resourceId;
  private List<T> permissions;
}
