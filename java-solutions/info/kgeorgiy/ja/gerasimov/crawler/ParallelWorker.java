package info.kgeorgiy.ja.gerasimov.crawler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.function.Consumer;

abstract class ParallelWorker<I, R> implements AutoCloseable {
    protected final ExecutorService executorService;

    protected ParallelWorker(ExecutorService executorService) {
        this.executorService = executorService;
    }

    protected abstract R work(I item) throws IOException;

    void startParallelWork(Phaser phaser,
                           I item,
                           ThrowableConsumer<? super R, ? extends IOException> function,
                           Consumer<? super IOException> errorsFunction) {
        phaser.register();
        startParallelWork(
                item,
                function,
                errorsFunction,
                phaser::arriveAndDeregister
        );
    }

    private void startParallelWork(I item,
                                   ThrowableConsumer<? super R, ? extends IOException> function,
                                   Consumer<? super IOException> errorsFunction,
                                   Runnable stepBack) {
        executorService.submit(() -> {
            try {
                function.run(work(item));
            } catch (IOException e) {
                errorsFunction.accept(e);
            } finally {
                stepBack.run();
            }

        });
    }

    @Override
    public void close() {
        executorService.close();
    }
}
