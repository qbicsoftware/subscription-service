package life.qbic.subscriptions.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import life.qbic.subscriptions.subscriptions.CancellationRequest;
import org.junit.jupiter.api.Test;

class SubscriptionEncodingTests {

  private final CancellationRequest expectedRequest =
      new CancellationRequest("QABCD", "test@user.id");
  private final String secret = "ABCD1234";
  private final String encodedString = "UUFCQ0QgdGVzdEB1c2VyLmlkIEFCQ0QxMjM0";

  @Test
  void decode() {
    Decoder<String, CancellationRequest> decoder = new SubscriptionEncoding(secret);
    assertEquals(expectedRequest, decoder.decode(encodedString));
  }

  /*Decode throws Exception for non-matching secret*/
  @Test
  void decodeThrowsExceptionForNonMatchingSecret() {
    Decoder<String, CancellationRequest> decoder = new SubscriptionEncoding(secret + "ABCD");
    assertThrows(DecodingException.class, () -> decoder.decode(encodedString));
  }

  @Test
  void encode() {
    Encoder<CancellationRequest, String> encoder = new SubscriptionEncoding(secret);
    assertEquals(encodedString, encoder.encode(expectedRequest));
  }

  @Test
  void encodeAndDecodeAreInSync() {
    SubscriptionEncoding subscriptionEncoding = new SubscriptionEncoding("ZXM!I");
    assertEquals(
        expectedRequest, subscriptionEncoding.decode(subscriptionEncoding.encode(expectedRequest)));
  }
}
