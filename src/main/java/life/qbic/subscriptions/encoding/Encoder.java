package life.qbic.subscriptions.encoding;

/**
 * Encodes a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> input type
 * @param <O> encoded output type
 */
@FunctionalInterface
public interface Encoder<I, O> {
  /**
   * Encodes a message of type {@link I} to a message of type {@link O}
   *
   * @param input to be encoded
   * @return encoded output
   */
  O encode(I input);
}
