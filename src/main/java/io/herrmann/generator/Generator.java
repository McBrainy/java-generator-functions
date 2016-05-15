package io.herrmann.generator;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This functional interface allows specifying Python generator-like sequences.
 * For examples, see the JUnit test case.
 *
 * The implementation uses a separate Thread to produce the sequence items. This
 * is certainly not as fast as eg. a for-loop, but not horribly slow either. On
 * a machine with a dual core i5 CPU @ 2.67 GHz, 1000 items can be produced in
 * &lt; 0.03s.
 *
 * By overriding finalize(), the underlying iterator takes care not to leave any
 * Threads running longer than necessary.
 */
@FunctionalInterface
public interface Generator<T> extends Iterable<T> {

	@Override
	public default Iterator<T> iterator() {
		return new GeneratorIterator<>(this);
	}

	public void run(GeneratorIterator<T> gen) throws InterruptedException;

	/**
	 * Returns an ordered {@link Spliterator} consisting of elements yielded by
	 * this {@link Generator}.
	 */
	@Override
	default Spliterator<T> spliterator() {
		return Spliterators.spliteratorUnknownSize(iterator(),
				Spliterator.ORDERED);
	}

	/**
	 * Creates a {@link Stream} from a {@link Generator}.
	 * @param g The generator
	 * @return An ordered, sequential (non-parallel) stream of elements yielded
	 * by the generator
	 */
	public static <T> Stream<T> stream(Generator<T> g) {
		return StreamSupport.stream(g.spliterator(), false);
	}

}
