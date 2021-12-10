package life.qbic.subscriptions;

import io.swagger.v3.oas.annotations.tags.Tag;
import life.qbic.subscriptions.encoding.RequestDecoder;
import life.qbic.subscriptions.subscriptions.CancellationRequest;
import life.qbic.subscriptions.subscriptions.SubscriptionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subscription", description = "Subscription controller API")
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {

  private static final Logger log = LogManager.getLogger(SubscriptionController.class);

  RequestDecoder requestDecoder;

  SubscriptionRepository subscriptionRepository;

  @Autowired
  SubscriptionController(SubscriptionRepository subscriptionRepository) {
    this.subscriptionRepository = subscriptionRepository;
  }

  @RequestMapping(value = "/cancel/request", method = RequestMethod.GET)
  public ResponseEntity<String> getCancellationRequestHash(
      @RequestBody CancellationRequest cancellationRequest) {
    validateRequest(cancellationRequest);
    //TODO Implement encryption
    return new ResponseEntity<>("dkdkdjjj-jjjj", HttpStatus.OK);
  }

  @RequestMapping(value = "/cancel/{hash}", method = RequestMethod.POST)
  public ResponseEntity<CancellationRequest> cancelSubscription(
      @PathVariable(value = "hash") String requestHash) {
    var cancellationRequest = decryptRequest(requestHash);
    removeSubscription(new CancellationRequest("QHOME", "sven.fillinger@qbic.uni-tuebingen.de"));
    return new ResponseEntity<>(cancellationRequest, HttpStatus.ACCEPTED);
  }

  private void removeSubscription(CancellationRequest request) {
    try {
      subscriptionRepository.cancelSubscription(request);
    } catch (Exception e) {
      log.error(e);
      throw new CancellationFailure("Unexpected failure.");
    }
  }

  private CancellationRequest decryptRequest(String hash) {
    // TODO implement decoding
    return new CancellationRequest("", "");
  }

  private void validateRequest(CancellationRequest cancellationRequest) {
    var project = cancellationRequest.project();
    var userId = cancellationRequest.userId();
    if (project == null || project.isBlank()) {
      throw new MissingPropertyException("project");
    }
    if (userId == null || userId.isBlank()) {
      throw new MissingPropertyException("userId");
    }
  }

  @ExceptionHandler({CancellationFailure.class})
  private ResponseEntity<String> cancellationFailure(CancellationFailure e) {
    return new ResponseEntity<>(
        "Subscription could not be cancelled. Reason: " + e.reason, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({MissingPropertyException.class})
  private ResponseEntity<String> missingProperty(MissingPropertyException e) {
    return new ResponseEntity<>(
        "Missing content for required property: " + e.missingProperty, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({InvalidRequestHashException.class})
  private ResponseEntity<String> invalidHash(InvalidRequestHashException e) {
    return new ResponseEntity<>("Invalid request hash.", HttpStatus.BAD_REQUEST);
  }

  /**
   * Helper exception class, to indicate invalid hashes
   */
  public static class InvalidRequestHashException extends RuntimeException {
    public InvalidRequestHashException() {
      super();
    }
  }

  /**
   * Helper exception class, to indicate failing subscription cancellations requests
   */
  public static class CancellationFailure extends RuntimeException {

    final String reason;

    public CancellationFailure(String reason) {
      super();
      this.reason = reason;
    }
  }

  /**
   * Helper exception class, to indicate missing request properties
   */
  public static class MissingPropertyException extends RuntimeException {

    final String missingProperty;

    public MissingPropertyException(String missingProperty) {
      super("Missing property '" + missingProperty + "'");
      this.missingProperty = missingProperty;
    }
  }

}
