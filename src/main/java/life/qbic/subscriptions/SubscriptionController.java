package life.qbic.subscriptions;

import io.swagger.v3.oas.annotations.tags.Tag;
import life.qbic.subscriptions.encryption.DecryptionException;
import life.qbic.subscriptions.encryption.EncryptionException;
import life.qbic.subscriptions.encryption.RequestDecrypter;
import life.qbic.subscriptions.encryption.RequestEncrypter;
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

  RequestEncrypter requestEncrypter;

  RequestDecrypter requestDecrypter;

  SubscriptionRepository subscriptionRepository;

  @Autowired
  SubscriptionController(
      SubscriptionRepository subscriptionRepository,
      RequestDecrypter requestDecrypter,
      RequestEncrypter requestEncrypter
      ) {
    this.subscriptionRepository = subscriptionRepository;
    this.requestDecrypter = requestDecrypter;
    this.requestEncrypter = requestEncrypter;
  }

  @RequestMapping(value = "/cancel", method = RequestMethod.GET)
  public ResponseEntity<String> getCancellationRequestHash(
      @RequestBody CancellationRequest cancellationRequest) {
    validateRequest(cancellationRequest);
    var cancellationRequestHash = requestEncrypter.encryptCancellationRequest(cancellationRequest);
    return new ResponseEntity<>(cancellationRequestHash, HttpStatus.OK);
  }

  @RequestMapping(value = "/cancel/{hash}", method = RequestMethod.POST)
  public ResponseEntity<CancellationRequest> cancelSubscription(
      @PathVariable(value = "hash") String requestHash) {
    var cancellationRequest = requestDecrypter.decryptCancellationRequest(requestHash);
    removeSubscription(cancellationRequest);
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

  @ExceptionHandler({EncryptionException.class})
  private ResponseEntity<String> encryptionException(EncryptionException e) {
    return new ResponseEntity<>(
        "Cancellation request generation failed", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({DecryptionException.class})
  private ResponseEntity<String> decryptionException(DecryptionException e) {
    return new ResponseEntity<>(
        "Decryption of your request has failed", HttpStatus.BAD_REQUEST);
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
