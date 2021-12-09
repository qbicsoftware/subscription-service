package life.qbic.springbootresttemplate.subscriptions;

/**
 * <b>Interface SubscriptionRepository</b>
 *
 * <p>Enables interaction with project notification subscriptions.</p>
 */
public interface SubscriptionRepository {

  /**
   * Removes a subscription based on the {@link CancellationRequest}.
   * @param cancellationRequest the cancellation request
   * @since 1.0.0
   */
  void cancelSubscription(CancellationRequest cancellationRequest);
}
