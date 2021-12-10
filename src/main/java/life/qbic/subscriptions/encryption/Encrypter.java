package life.qbic.subscriptions.encryption;

import java.util.function.Function;

/**
 * Encrypts a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> input type
 * @param <O> encoded output type
 */
@FunctionalInterface
public interface Encrypter<I, O> extends Function<I, O> {

  /**
   * Encrypts a message of type {@link I} to a message of type {@link O}
   *
   * @param input message to be encrypted
   * @return encrypted message
   */
  O encrypt(I input);

  @Override
  default O apply(I i) {
    return encrypt(i);
  }
}
