package life.qbic.springbootresttemplate.subscriptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Configuration {

  @Value("${databases.users.user.name}")
  public String user;
  @Value("${databases.users.user.password}")
  public String password;
  @Value("${databases.users.database.url}")
  public String url;
  @Value("${databases.users.database.name}")
  public String databaseName;
  @Value("${databases.users.database.dialect}")
  public String sqlDialect;
  @Value("${databases.users.database.driver}")
  public String driver;

}
