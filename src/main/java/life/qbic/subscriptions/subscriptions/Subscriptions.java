package life.qbic.springbootresttemplate.subscriptions;

import java.util.Properties;
import javax.annotation.PostConstruct;
import life.qbic.springbootresttemplate.subscriptions.entities.Person;
import life.qbic.springbootresttemplate.subscriptions.entities.Subscription;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <b>Class Subscriptions</b>
 *
 * <p>Implementation of the the {@link SubscriptionRepository} interface, that enables the
 * client to interact with project subscriptions.</p>
 */
@Component
class Subscriptions implements SubscriptionRepository {

  private final Configuration configuration;

  private SessionFactory sessionFactory;

  @Autowired
  Subscriptions(Configuration config) {
    this.configuration = config;
  }

  @PostConstruct
  void init() {
    var config = new org.hibernate.cfg.Configuration();
    var properties = new Properties();
    config.setProperty(Environment.DRIVER, configuration.driver);
    config.setProperty(Environment.URL, configuration.url);
    config.setProperty(Environment.USER, configuration.user);
    config.setProperty(Environment.PASS, configuration.password);
    config.setProperty(Environment.POOL_SIZE, "1");
    config.setProperty(Environment.DIALECT, configuration.sqlDialect);
    config.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    config.setProperties(properties);
    sessionFactory = config
        .addAnnotatedClass(Person.class)
        .addAnnotatedClass(Subscription.class)
        .buildSessionFactory();
  }

  /**
   * @inheritDocs
   */
  @Override
  public void cancelSubscription(CancellationRequest cancellationRequest) {

  }
}
