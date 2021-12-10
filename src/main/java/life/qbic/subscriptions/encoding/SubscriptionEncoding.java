package life.qbic.subscriptions.encoding;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import life.qbic.subscriptions.subscriptions.CancellationRequest;

/**
 * <b><short description></b>
 *
 * <p><detailed description>
 *
 * @since <version tag>
 */
public class SubscriptionEncoding
    implements RequestDecoder,
        Encoder<CancellationRequest, String>,
        Decoder<String, CancellationRequest> {

  private final String SECRET;
  private static final String DELIMITER = " ";

  public SubscriptionEncoding(String secret) {
    SECRET = secret;
  }

  @Override
  public CancellationRequest decodeCancellationRequest(String hash) throws DecodingException {
    return decode(hash);
  }

  @Override
  public CancellationRequest decode(String encodedValue) {
    String decodedMessage = new String(getDecoder().decode(encodedValue));
    String[] components = decodedMessage.split(DELIMITER, 0);
    if (!components[2].equals(SECRET)) {
      throw new RuntimeException("Wrong decoding detected.");
    }
    return new CancellationRequest(components[0], components[1]);
  }

  @Override
  public String encode(CancellationRequest input) {
    String joinedString = String.join(DELIMITER, List.of(input.project(), input.userId(), SECRET));
    return getEncoder().encodeToString(joinedString.getBytes(StandardCharsets.UTF_8));
  }
}
