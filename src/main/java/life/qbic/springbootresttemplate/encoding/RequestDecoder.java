package life.qbic.springbootresttemplate.encoding;

import life.qbic.springbootresttemplate.subscriptions.CancellationRequest;

public interface RequestDecoder {

  CancellationRequest decodeCancellationRequest(String hash) throws DecodingException;

}
