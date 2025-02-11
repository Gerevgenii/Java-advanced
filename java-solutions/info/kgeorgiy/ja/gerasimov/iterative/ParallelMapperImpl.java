package info.kgeorgiy.ja.gerasimov.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Maps function over lists.
 */

public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threads = new ArrayList<>();
    private final SyncQueue<Runnable> syncQueue = new SyncQueue<>();

    /**
     * The constructor that accepts the number of threads
     *
     * @param sizeOfThreads counts of threads
     */
    public ParallelMapperImpl(int sizeOfThreads) {
        for (int i = 0; i < sizeOfThreads; i++) {
            final Thread thread = new Thread(
                    () -> {
                        try {
                            while (!Thread.interrupted()) {
                                try {
                                    syncQueue.get().run();
                                } catch (RuntimeException ignored) {
                                }
                            }
                        } catch (InterruptedException ignored) {
                        }
                    }
            );
            threads.add(thread);
            thread.start();
        }
    }

    /**
     * Maps function {@code f} over specified {@code items}.
     * Mapping for each item is performed in parallel.
     *
     * @throws InterruptedException if calling thread was interrupted
     */
    @Override
    public <T, R> List<R> map(
            Function<? super T, ? extends R> f,
            List<? extends T> items
    ) throws InterruptedException {
        final int[] count = {0};
        final List<R> results = new ArrayList<>(Collections.nCopies(items.size(), null));
        launchResultFilling(f, items, results, count);
        synchronized (this) {
            while (count[0] != results.size()) {
                this.wait();
            }
        }
        return results;
    }

    private <T, R> void launchResultFilling(Function<? super T, ? extends R> f, List<? extends T> items, List<R> results, int[] count) {
        for (int i = 0; i < items.size(); i++) {
            final int finalI = i;
            syncQueue.add(() -> {
                results.set(finalI, f.apply(items.get(finalI)));
                synchronized (this) {
                    count[0]++;
                    this.notify();
                }
            });
        }
    }

    /**
     * Stops all threads. All unfinished mappings are left in undefined state.
     */
    @Override
    public void close() {
        threads.forEach(Thread::interrupt);
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException ignored) {
            }
        });
    }
}
