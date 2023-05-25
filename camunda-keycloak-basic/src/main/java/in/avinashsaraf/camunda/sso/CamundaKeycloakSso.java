package in.avinashsaraf.camunda.sso;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableProcessApplication
public class CamundaKeycloakSso {

  public static void main(final String... args) {
    SpringApplication.run(CamundaKeycloakSso.class, args);
  }

}
