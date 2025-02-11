package info.kgeorgiy.ja.gerasimov.iterative;

import info.kgeorgiy.java.advanced.iterative.NewScalarIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.lang.Math.min;

/**
 * Scalar iterative parallelism support with sparse data.
 * <p>
 * Each method of this interface takes an extra positive integer {@code step}.
 * Only each {@code step}-th element of the input list should be used (counting from 0).
 */
public class IterativeParallelism implements NewScalarIP {
    private final ParallelMapper parallelMapper;

    /**
     * The default constructor of {@link IterativeParallelism}
     */
    public IterativeParallelism() {
        parallelMapper = null;
    }

    /**
     * The constructor that accepts {@link ParallelMapper}
     *
     * @param parallelMapper Thread pool
     */
    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    private static <A> List<A> getNthChunk(final List<A> list, final int chunksCount, final int i) {
        if (list.isEmpty())
            return list;
        final int chunkSize = list.size() / chunksCount;
        final int rangeStart = Math.min(i * chunkSize, list.size());
        return list.subList(
                rangeStart,
                i == chunksCount - 1 ?
                        list.size() :
                        Math.min(rangeStart + chunkSize, list.size())
        );
    }

    private <T, A, B> B fold(
            int threads,
            List<? extends T> values,
            Function<? super List<? extends T>, A> searchFunction,
            Function<? super List<? extends A>, B> resultedFunction
    ) throws InterruptedException {
        // :NOTE: ??
        //noinspection AssignmentToMethodParameter
        threads = min(threads, values.size());
        final int finalThreads = threads;


        if (parallelMapper != null) {
            final List<? extends List<? extends T>> sublistOfResult =
                    IntStream
                            .range(0, threads)
                            .mapToObj(
                                    (index) ->
                                            getNthChunk(values, finalThreads, index))
                            .toList();
            return resultedFunction.apply(parallelMapper.map(searchFunction, sublistOfResult));
        }


        final List<A> results = new ArrayList<>(Collections.nCopies(threads, null));

        final List<Thread> threadList =
                IntStream
                        .range(0, threads)
                        .mapToObj(
                                (index) -> {
                                    final Thread thread = new Thread(
                                            () -> results
                                                    .set(
                                                            index,
                                                            searchFunction.apply(
                                                                    getNthChunk(
                                                                            values,
                                                                            finalThreads,
                                                                            index
                                                                    ))),
                                            "thread" + index
                                    );
                                    // :NOTE: .peek()
                                    thread.start();
                                    return thread;
                                })
                        .toList();
        try {
            for (Thread thread : threadList) {
                // :NOTE: Если один сломается, остальные не будут заджоинены
                thread.join();
            }
        } catch (InterruptedException e) {
            threadList.forEach(Thread::interrupt);
        }
        return resultedFunction.apply(results);
    }

    /**
     * Returns maximum value.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to get maximum of.
     * @param comparator value comparator.
     * @param step       step size.
     * @param <T>        The type of item that is stored in the {@link List}
     * @return maximum of elements {@code T}
     * @throws InterruptedException if no values are given.
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        final Function<List<? extends T>, ? extends T> function = (newValues) -> Collections.max(newValues, comparator);
        return fold(
                threads,
                new MyList<>(values, step),
                function,
                function
        );
    }

    /**
     * Returns minimum value.
     *
     * @param threads    number of concurrent threads.
     * @param values     values to get minimum of.
     * @param comparator value comparator.
     * @param step       step size.
     * @param <T>        The type of item that is stored in the {@link List}
     * @return minimum of elements {@code T}
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return maximum(threads, values, comparator.reversed(), step);
    }

    /**
     * Returns whether all values satisfy predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param step      step size.
     * @param <T>       The type of item that is stored in the {@link List}
     * @return whether all values satisfy predicate or {@code true}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return !any(threads, values, predicate.negate(), step);
    }

    /**
     * Returns whether any of values satisfies predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param step      step size.
     * @param <T>       The type of item that is stored in the {@link List}
     * @return whether any value satisfies predicate or {@code false}, if no values are given.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return fold(
                threads,
                new MyList<>(values, step),
                (newValues) -> newValues.stream().anyMatch(predicate),
                (newValues) -> newValues.stream().anyMatch(Boolean::booleanValue)
        );
    }

    /**
     * Returns number of values satisfying predicate.
     *
     * @param threads   number of concurrent threads.
     * @param values    values to test.
     * @param predicate test predicate.
     * @param step      step size.
     * @param <T>       The type of item that is stored in the {@link List}
     * @return number of values satisfying predicate.
     * @throws InterruptedException if executing thread was interrupted.
     */
    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return fold(
                threads,
                new MyList<>(values, step),
                (newValues) -> newValues.stream().filter(predicate).count(),
                (newValues) -> newValues.stream().mapToInt(Long::intValue).sum()
        );
    }
}
