package life.qbic.subscriptions.encoding;

/**
 * Decodes a message of type {@link I} to a message of type {@link O}
 *
 * @param <I> encoded input type
 * @param <O> decoded output type
 */
public interface Decoder<I, O> {
  O decode(I encodedValue);
}
