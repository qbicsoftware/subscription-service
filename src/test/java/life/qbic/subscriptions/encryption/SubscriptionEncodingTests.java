package life.qbic.subscriptions.encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import life.qbic.subscriptions.subscriptions.CancellationRequest;
import org.junit.jupiter.api.Test;

class SubscriptionEncodingTests {

  private final CancellationRequest expectedRequest =
      new CancellationRequest("QABCD", "test@user.id");
  private final String secret = "aesEncryptionKey";
  private final String salt = "123456789!1234567";
  private final String encodedString = "BStOJDfmn0ZyNceOPN3qU2xJw1mQfdbzY/a+uGt7Ae0=";

  @Test
  void encode() {
    RequestEncrypter encoder = new SubscriptionEncoding(secret, salt);
    assertEquals(encodedString, encoder.encryptCancellationRequest(expectedRequest));
  }

  @Test
  void encodeAndDecodeAreInSync() {
    SubscriptionEncoding subscriptionEncoding = new SubscriptionEncoding("1234567890123456", salt);
    assertEquals(
        expectedRequest,
        subscriptionEncoding.decryptCancellationRequest(
            subscriptionEncoding.encryptCancellationRequest(expectedRequest)));
  }

  @Test
  void decode() {
    RequestDecrypter decoder = new SubscriptionEncoding(secret, salt);
    assertEquals(expectedRequest, decoder.decryptCancellationRequest(encodedString));
  }

  /*Decode throws Exception for non-matching secret*/
  @Test
  void decodeThrowsExceptionForNonMatchingSecret() {
    RequestDecrypter decoder = new SubscriptionEncoding("1234567890123456", salt);
    assertThrows(
        EncryptionException.class, () -> decoder.decryptCancellationRequest(encodedString));
  }
}
