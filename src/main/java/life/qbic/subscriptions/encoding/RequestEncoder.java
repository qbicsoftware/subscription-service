package life.qbic.subscriptions.encoding;

import life.qbic.subscriptions.subscriptions.CancellationRequest;

public interface RequestEncoder {

  String encodeCancellationRequest(CancellationRequest cancellationRequest)
      throws DecodingException;
}
