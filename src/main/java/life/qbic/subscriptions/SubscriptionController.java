package life.qbic.subscriptions;

import static org.slf4j.LoggerFactory.getLogger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import life.qbic.subscriptions.encryption.DecryptionException;
import life.qbic.subscriptions.encryption.EncryptionException;
import life.qbic.subscriptions.encryption.RequestDecrypter;
import life.qbic.subscriptions.encryption.RequestEncrypter;
import life.qbic.subscriptions.subscriptions.CancellationRequest;
import life.qbic.subscriptions.subscriptions.SubscriptionRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Subscription", description = "Subscription API")
@SecurityScheme(name = "basic", type = SecuritySchemeType.HTTP, scheme = "basic")
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionController {

  private static final Logger log = getLogger(SubscriptionController.class);

  RequestEncrypter requestEncrypter;

  RequestDecrypter requestDecrypter;

  SubscriptionRepository subscriptionRepository;

  @Autowired
  SubscriptionController(
      SubscriptionRepository subscriptionRepository,
      RequestDecrypter requestDecrypter,
      RequestEncrypter requestEncrypter) {
    this.subscriptionRepository = subscriptionRepository;
    this.requestDecrypter = requestDecrypter;
    this.requestEncrypter = requestEncrypter;
  }

  @Operation(summary = "Cancel a subscription",
      parameters = {
          @Parameter(name = "token",
              description = "The token of an encrypted cancel request.",
              example = "For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA", schema = @Schema(implementation = String.class))
      },
      responses = {
          @ApiResponse(responseCode = "204", description = "No content. Your subscription was cancelled.",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = CancellationRequest.class))
          ),
          @ApiResponse(responseCode = "400", description = "Bad request. Your cancellation request was not successful.",
              content = @Content(mediaType = "text/plain")
          ),
          @ApiResponse(responseCode = "422", description = "Unprocessable entity. Your cancellation request was not successful.",
              content = @Content(mediaType = "text/plain")
          )
      })
  @SecurityRequirement(name = "basic")
  @DeleteMapping(value = "/{token}")
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  public void cancelSubscriptionByToken(@PathVariable String token) {
    var cancellationRequest = requestDecrypter.decryptCancellationRequest(token);
    removeSubscription(cancellationRequest);
  }

  @Operation(summary = "Request a subscription cancel token",
      responses = {
          @ApiResponse(responseCode = "200", description = "Subscription cancel token",
              content = @Content(mediaType = "text/plain", schema = @Schema(example = "For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA"))
          ),
          @ApiResponse(responseCode = "400", description = "Bad request. Your cancellation request might not be correct.",
              content = @Content(mediaType = "text/plain")
          ),
          @ApiResponse(responseCode = "401", description = "Full authentication required.",
              content = @Content(mediaType = "text/plain")
          )
      })
  @SecurityRequirement(name = "basic")
  @PostMapping(value = "/tokens")
  public ResponseEntity<String> generateToken(@RequestBody CancellationRequest cancellationRequest) {
    validateRequest(cancellationRequest);
    var cancellationRequestToken = requestEncrypter.encryptCancellationRequest(cancellationRequest);
    return new ResponseEntity<>(cancellationRequestToken, HttpStatus.CREATED);
  }

  private void removeSubscription(CancellationRequest request) {
    try {
      subscriptionRepository.cancelSubscription(request);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
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
  private ResponseEntity<String> encryptionException() {
    return new ResponseEntity<>(
        "Cancellation request generation failed", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({DecryptionException.class})
  private ResponseEntity<String> decryptionException() {
    return new ResponseEntity<>(
        "Decryption of your request has failed", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({CancellationFailure.class})
  private ResponseEntity<String> cancellationFailure(CancellationFailure e) {
    return new ResponseEntity<>(
        "Subscription could not be cancelled. Reason: " + e.reason, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler({MissingPropertyException.class})
  private ResponseEntity<String> missingProperty(MissingPropertyException e) {
    return new ResponseEntity<>(
        "Missing content for required property: " + e.missingProperty, HttpStatus.BAD_REQUEST);
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
