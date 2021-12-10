package life.qbic.subscriptions.encryption;

/**
 * Decrypts a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> encoded input type
 * @param <O> decoded output type
 */
public interface Decrypter<I, O> {

  /**
   * Decrypts a message of type {@link I} to a message of type {@link O}
   *
   * @param encodedValue encoded input type
   * @return decoded output type
   */
  O decode(I encodedValue);
}
