package life.qbic.subscriptions.encryption;

import static org.apache.tomcat.util.codec.binary.Base64.encodeBase64String;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import life.qbic.subscriptions.subscriptions.CancellationRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.codec.binary.Base64;

/**
 * <b><short description></b>
 *
 * <p><detailed description>
 *
 * @since <version tag>
 */
public class SubscriptionEncoding implements RequestDecrypter, RequestEncrypter {

  private static final Logger log = LogManager.getLogger(SubscriptionEncoding.class);

  private final String SECRET;
  private final String SALT;
  private static final String DELIMITER = " ";

  private final Encrypter<CancellationRequest, String> encrypter = this::encrypt;
  private final Decrypter<String, CancellationRequest> decrypter = this::decrypt;

  public SubscriptionEncoding(String secret, String salt) {
    SECRET = secret;
    SALT = salt;
  }

  @Override
  public CancellationRequest decryptCancellationRequest(String hash) throws DecryptionException {
    return decrypter.decode(hash);
  }

  @Override
  public String encryptCancellationRequest(CancellationRequest cancellationRequest)
      throws EncryptionException {
    return encrypter.encode(cancellationRequest);
  }

  /**
   * {@inheritDoc}
   *
   * @throws EncryptionException in case the decoding could not be performed successfully
   */
  private CancellationRequest decrypt(String encodedValue) throws EncryptionException {
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.DECRYPT_MODE, secretKey(SECRET), initVector(SALT));
      byte[] original = cipher.doFinal(Base64.decodeBase64(encodedValue));
      String originalValue = new String(original);
      String[] components = originalValue.split(DELIMITER, 0);
      return new CancellationRequest(components[0], components[1]);
    } catch (InvalidAlgorithmParameterException
        | NoSuchPaddingException
        | IllegalBlockSizeException
        | NoSuchAlgorithmException
        | BadPaddingException
        | InvalidKeyException e) {
      log.error(e);
      throw new DecryptionException();
    }
  }

  private String encrypt(CancellationRequest input) {
    String joinedString = input.project() + DELIMITER + input.userId();
    try {
      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
      cipher.init(Cipher.ENCRYPT_MODE, secretKey(SECRET), initVector(SALT));
      byte[] encrypted = cipher.doFinal(joinedString.getBytes());
      return encodeBase64String(encrypted);
    } catch (InvalidAlgorithmParameterException
        | NoSuchPaddingException
        | IllegalBlockSizeException
        | NoSuchAlgorithmException
        | BadPaddingException
        | InvalidKeyException e) {
      log.error(e);
      throw new EncryptionException();
    }
  }

  private static AlgorithmParameterSpec initVector(String salt) {

    byte[] saltBytes = salt.getBytes(StandardCharsets.UTF_8);
    if (saltBytes.length < 16) {
      throw new RuntimeException("Salt is insufficient. I need at least 16 bytes");
    }
    byte[] usedBytes = new byte[16];
    System.arraycopy(saltBytes, 0, usedBytes, 0, usedBytes.length);
    return new IvParameterSpec(usedBytes);
  }

  private static Key secretKey(String key) {
    return new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
  }
}
