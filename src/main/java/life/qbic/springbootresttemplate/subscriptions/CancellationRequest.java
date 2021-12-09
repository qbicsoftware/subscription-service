package life.qbic.springbootresttemplate.subscriptions;

/**
 * <b>Record CancellationRequest</b>
 *
 * <p>Holds information about the user id and project, a person with a given user id
 * should be removed from project notification subscriptions.</p>
 */
public record CancellationRequest(String project, String userId) {}

