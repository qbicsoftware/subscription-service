package life.qbic.subscriptions.subscriptions;

import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;
import life.qbic.subscriptions.subscriptions.entities.Person;
import life.qbic.subscriptions.subscriptions.entities.Subscription;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
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
    properties.setProperty(Environment.URL, configuration.url);
    properties.setProperty(Environment.USER, configuration.user);
    properties.setProperty(Environment.PASS, configuration.password);
    properties.setProperty(Environment.POOL_SIZE, "1");
    properties.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
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
  @SuppressWarnings("unchecked")
  public void cancelSubscription(CancellationRequest cancellationRequest) {
    try(Session session = sessionFactory.getCurrentSession()) {
      session.beginTransaction();
      // We set the query to filter for subscriptions with the project first
      Query<Subscription> query = session.createQuery("FROM Subscription s WHERE s.projectCode=:projectCode");
      query.setParameter("projectCode", cancellationRequest.project());
      // Then we submit the query
      List<Subscription> subscriptionCandidates = query.getResultList();
      // And delete all matching subscriptions
      subscriptionCandidates.stream()
          .filter( s -> s.getPerson().getUserId().equals(cancellationRequest.userId()))
          .forEach(session::delete);

      session.getTransaction().commit();
    }
  }
}
