package life.qbic.subscriptions.encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import life.qbic.subscriptions.subscriptions.CancellationRequest;
import org.junit.jupiter.api.Test;

class CancellationEnigmaTests {

  private final CancellationRequest expectedRequest =
      new CancellationRequest("QTEST", "sven.fillinger@qbic.uni-tuebingen.de");
  private final String secret = "G60B8IJ06H7S7YWL";
  private final String salt = "YY6WWQ2SLDH8CUXQWYBINQRTWXGTUEODNM";
  private final String encodedString = "For_lfbnS9iTi4Nmwnei4LA_f8SHga1Rdz4yw6aT8zz0V8PaHm1QEbKQTv1jGCEA";

  @Test
  void encode() {
    RequestEncrypter encoder = new CancellationEnigma(secret, salt);
    assertEquals(encodedString, encoder.encryptCancellationRequest(expectedRequest));
  }

  @Test
  void encodeAndDecodeAreInSync() {
    CancellationEnigma cancellationEnigma = new CancellationEnigma("1234567890123456", salt);
    assertEquals(
        expectedRequest,
        cancellationEnigma.decryptCancellationRequest(
            cancellationEnigma.encryptCancellationRequest(expectedRequest)));
  }

  @Test
  void decode() {
    RequestDecrypter decoder = new CancellationEnigma(secret, salt);
    assertEquals(expectedRequest, decoder.decryptCancellationRequest(encodedString));
  }

  /*Decode throws Exception for non-matching secret*/
  @Test
  void decodeThrowsExceptionForNonMatchingSecret() {
    RequestDecrypter decoder = new CancellationEnigma("1234567890123456", salt);
    assertThrows(
        DecryptionException.class, () -> decoder.decryptCancellationRequest(encodedString));
  }
}
