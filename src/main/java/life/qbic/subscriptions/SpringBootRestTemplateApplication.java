package life.qbic.subscriptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootRestTemplateApplication {

  private static final Logger logger = LoggerFactory.getLogger(SpringBootRestTemplateApplication.class);

  public static void main(String[] args) {
    logger.info("Starting subscription service ...");
    SpringApplication.run(SpringBootRestTemplateApplication.class, args);
  }

}
