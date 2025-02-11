package info.kgeorgiy.ja.gerasimov.crawler;

@FunctionalInterface
interface ThrowableConsumer<T, E extends Throwable> {
    void run(T t) throws E;
}
