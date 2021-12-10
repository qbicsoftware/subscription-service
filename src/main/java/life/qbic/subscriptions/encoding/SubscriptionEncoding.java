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
public class SubscriptionEncoding implements RequestDecoder, RequestEncoder {

  private final String SECRET;
  private static final String DELIMITER = " ";

  private final Encoder<CancellationRequest, String> encoder = this::encode;
  private final Decoder<String, CancellationRequest> decoder = this::decode;

  public SubscriptionEncoding(String secret) {
    SECRET = secret;
  }

  @Override
  public CancellationRequest decodeCancellationRequest(String hash) throws DecodingException {
    return decoder.decode(hash);
  }

  /**
   * {@inheritDoc}
   *
   * @throws DecodingException in case the decoding could not be performed successfully
   */
  private CancellationRequest decode(String encodedValue) throws DecodingException {
    String decodedMessage = new String(getDecoder().decode(encodedValue));
    String[] components = decodedMessage.split(DELIMITER, 0);
    if (!components[2].equals(SECRET)) {
      throw new DecodingException();
    }
    return new CancellationRequest(components[0], components[1]);
  }

  private String encode(CancellationRequest input) {
    String joinedString = String.join(DELIMITER, List.of(input.project(), input.userId(), SECRET));
    return getEncoder().encodeToString(joinedString.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String encodeCancellationRequest(CancellationRequest cancellationRequest)
      throws DecodingException {
    return encoder.encode(cancellationRequest);
  }
}
