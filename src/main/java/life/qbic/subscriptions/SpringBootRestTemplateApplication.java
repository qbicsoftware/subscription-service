package life.qbic.subscriptions;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "Subscription Service",
        version = "1.0.0",
        description = "Cancel project update subscriptions and request cancel tokens.",
        contact = @Contact(name = "Sven Fillinger", email = "sven.fillinger@qbic.uni-tuebingen.de")
    )
)
@SpringBootApplication
public class SpringBootRestTemplateApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootRestTemplateApplication.class, args);
  }

}
