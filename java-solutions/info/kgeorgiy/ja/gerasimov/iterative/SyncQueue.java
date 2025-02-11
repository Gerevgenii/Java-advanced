package info.kgeorgiy.ja.gerasimov.iterative;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Queue with parallel processing
 * @param <T>
 */
public class SyncQueue<T> {
    private final Queue<T> queue;

    /**
     * The default constructor that creates an empty queue
     */
    public SyncQueue() {
        queue = new ArrayDeque<>();
    }

    /**
     * A function that issues an item from the queue according to the FIFO rule
     * @return an item from the queue
     * @throws InterruptedException
     */

    public synchronized T get() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return queue.poll();
    }

    /**
     * Add an item to the queue according to the FIFO rule
     * @param value The element to be added
     */
    public synchronized void add(T value) {
        queue.add(value);
        notify();
    }
}
