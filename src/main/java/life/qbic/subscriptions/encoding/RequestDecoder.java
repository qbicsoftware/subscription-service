package life.qbic.subscriptions.encoding;

import life.qbic.subscriptions.subscriptions.CancellationRequest;

public interface RequestDecoder {

  CancellationRequest decodeCancellationRequest(String hash) throws DecodingException;

}
