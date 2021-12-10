package life.qbic.subscriptions.encryption;

import life.qbic.subscriptions.subscriptions.CancellationRequest;

public interface RequestEncrypter {

  String encryptCancellationRequest(CancellationRequest cancellationRequest)
      throws EncryptionException;
}
