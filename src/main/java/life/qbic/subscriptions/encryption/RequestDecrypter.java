package life.qbic.subscriptions.encryption;

import life.qbic.subscriptions.subscriptions.CancellationRequest;

public interface RequestDecrypter {

  CancellationRequest decryptCancellationRequest(String hash) throws DecryptionException;
}
