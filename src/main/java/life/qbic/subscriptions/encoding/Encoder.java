package life.qbic.subscriptions.encoding;

/**
 * Encodes a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> input type
 * @param <O> encoded output type
 */
@FunctionalInterface
public interface Encoder<I, O> {
  O encode(I input);
}
