package life.qbic.subscriptions.encryption;

/**
 * Encrypts a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> input type
 * @param <O> encoded output type
 */
@FunctionalInterface
public interface Encrypter<I, O> {
  /**
   * Encrypts a message of type {@link I} to a message of type {@link O}
   *
   * @param input message to be encrypted
   * @return encrypted message
   */
  O encrypt(I input);
}
