package in.avinashsaraf.camunda.sso.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.avinashsaraf.camunda.sso.security.model.GroupAuthorizationSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class GroupAuthorizationConfigReader {

  public static final String AUTHORIZATION_FILES_PATTERN = "classpath:authorization-config/*";
  final ObjectMapper objectMapper = new ObjectMapper();
  final PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();

  public List<GroupAuthorizationSet> readGroupAuthorizationSets() throws IOException {
    final var resources = pathMatchingResourcePatternResolver.getResources(AUTHORIZATION_FILES_PATTERN);
    return Arrays.stream(resources)
        .map(this::parseGroupAuthorizationSetFile)
        .toList();
  }

  private GroupAuthorizationSet parseGroupAuthorizationSetFile(final Resource resource) {
    try {
      return objectMapper.readValue(resource.getInputStream(), GroupAuthorizationSet.class);
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

}
