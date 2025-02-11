package info.kgeorgiy.ja.gerasimov.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

class ParallelExtractor extends ParallelWorker<Document, List<String>> {

    ParallelExtractor(ExecutorService executorService) {
        super(executorService);
    }

    void startParallelWork(Phaser phaser,
                                  Map<String, IOException> errors,
                                  String url,
                                  Document item,
                                  ThrowableConsumer<? super List<String>, ? extends IOException> function) {
        startParallelWork(
                phaser,
                item,
                function,
                (e) -> errors.put(url, e)
        );
    }

    @Override
    protected List<String> work(Document item) throws IOException {
        return item.extractLinks();
    }
}
