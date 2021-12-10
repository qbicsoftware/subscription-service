package life.qbic.subscriptions.encryption;

import java.util.function.Function;

/**
 * Decrypts a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> encoded input type
 * @param <O> decoded output type
 */
public interface Decrypter<I, O> extends Function<I, O> {

  /**
   * Decrypts a message of type {@link I} to a message of type {@link O}
   *
   * @param message encrypted message
   * @return original value
   */
  O decrypt(I message);

  @Override
  default O apply(I i) {
    return decrypt(i);
  }
}
