package life.qbic.subscriptions.subscriptions;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DBConfiguration {

  @Value("${databases.users.user.name}")
  public String user;
  @Value("${databases.users.user.password}")
  public String password;
  @Value("${databases.users.database.url}")
  public String url;
  @Value("${databases.users.database.dialect}")
  public String sqlDialect;
  @Value("${databases.users.database.driver}")
  public String driver;

  @Override
  public String toString() {
    return "DBConfiguration{" +
        "user='" + user + '\'' +
        ", password='" + password + '\'' +
        ", url='" + url + '\'' +
        ", sqlDialect='" + sqlDialect + '\'' +
        ", driver='" + driver + '\'' +
        '}';
  }
}
