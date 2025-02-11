package info.kgeorgiy.ja.gerasimov.crawler;

import info.kgeorgiy.java.advanced.crawler.Document;
import info.kgeorgiy.java.advanced.crawler.Downloader;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;

class ParallelDownloader extends ParallelWorker<String, Document> {
    private final Downloader downloader;

    ParallelDownloader(Downloader downloader, ExecutorService executorService) {
        super(executorService);
        this.downloader = downloader;
    }

    void startParallelWork(Phaser phaser,
                                  Map<String, IOException> errors,
                                  String url,
                                  ThrowableConsumer<? super Document, ? extends IOException> function) {
        startParallelWork(
                phaser,
                url,
                function,
                (e) -> errors.put(url, e)
        );
    }

    @Override
    protected Document work(String item) throws IOException {
        return downloader.download(item);
    }

}
