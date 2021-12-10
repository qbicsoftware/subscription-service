package life.qbic.subscriptions.subscriptions;

import java.util.Properties;
import javax.annotation.PostConstruct;
import life.qbic.subscriptions.subscriptions.entities.Person;
import life.qbic.subscriptions.subscriptions.entities.Subscription;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
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

  private final DBConfiguration configuration;

  private SessionFactory sessionFactory;

  @Autowired
  Subscriptions(DBConfiguration config) {
    this.configuration = config;
  }

  @PostConstruct
  void init() {
    Configuration config = new Configuration();
    Properties properties = new Properties();
    System.out.println(configuration);
    //properties.setProperty(Environment.DRIVER, configuration.driver);
    properties.setProperty(Environment.URL, configuration.url);
    properties.setProperty(Environment.USER, configuration.user);
    properties.setProperty(Environment.PASS, configuration.password);
    properties.setProperty(Environment.POOL_SIZE, "1");
    properties.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    System.out.println(properties);
    config.setProperties(properties);
    sessionFactory = config
        .addAnnotatedClass(Person.class)
        .addAnnotatedClass(Subscription.class)
        .buildSessionFactory();

    try(Session session = sessionFactory.getCurrentSession()) {
      session.beginTransaction();
      Subscription subscription = session.get(Subscription.class, 1);
      System.out.println(subscription);
      System.out.println(subscription.getPerson());
      session.getTransaction().commit();
    }
  }

  /**
   * @inheritDocs
   */
  @Override
  public void cancelSubscription(CancellationRequest cancellationRequest) {

  }
}
