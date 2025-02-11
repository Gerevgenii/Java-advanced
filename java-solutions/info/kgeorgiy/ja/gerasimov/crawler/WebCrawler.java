package info.kgeorgiy.ja.gerasimov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.function.Predicate;

/**
 * A class that parses sites and extracts links from them
 */
public class WebCrawler implements NewCrawler {
    private final ParallelDownloader downloader;
    private final ParallelExtractor extractor;

    /**
     * The constructor that accepts the {@link Downloader},
     * the number of streams to download, the number of streams to extract, and the ignored value
     *
     * @param downloader  {@link Downloader}
     * @param downloaders the number of streams to download
     * @param extractors  the number of streams to extract
     * @param perHost     the maximum number of downloads from a single host
     */

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = new ParallelDownloader(downloader, Executors.newFixedThreadPool(downloaders));
        this.extractor = new ParallelExtractor(Executors.newFixedThreadPool(extractors));
    }

    /**
     * Downloads website up to specified depth.
     *
     * @param url   start <a href="http://tools.ietf.org/html/rfc3986">URL</a>.
     * @param depth download depth.
     * @return download result.
     */
    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        if (depth < 1) throw new IllegalArgumentException("Your depth is incorrect");
        if (excludes.stream().anyMatch(url::contains)) return new Result(List.of(), Map.of());
        final Set<String> currentLinks = ConcurrentHashMap.newKeySet();
        currentLinks.add(url);
        final Map<String, IOException> errors = new ConcurrentHashMap<>();
        final Set<String> urls = ConcurrentHashMap.newKeySet();
        for (int i = 0; i < depth; i++) {
            final Phaser phaser = new Phaser(1);
            final Set<String> newSetOfURLS = ConcurrentHashMap.newKeySet();
            final Predicate<String> stringPredicate = currentURL -> !urls.contains(currentURL) && !errors.containsKey(currentURL) && excludes.stream().noneMatch(currentURL::contains);
            currentLinks.stream()
                    .filter(stringPredicate)
                    .forEach(currentURL -> downloader.startParallelWork(phaser, errors, currentURL, (document) -> {
                                urls.add(currentURL);
                                extractor.startParallelWork(phaser, errors, url, document, (currentNewLinks) ->
                                        currentNewLinks.stream()
                                                .filter(stringPredicate)
                                                .forEach(newSetOfURLS::add)
                                );
                            }
                    ));
            phaser.arriveAndAwaitAdvance();
            currentLinks.clear();
            currentLinks.addAll(newSetOfURLS);
        }
        return new Result(List.copyOf(urls), errors);

    }

    /**
     * Stops parallel executable threads ({@code downloader}, {@code extractor})
     */
    @Override
    public void close() {
        downloader.close();
        extractor.close();
    }

    private static int getOrDefault(int index, String[] args, int defaultValue) {
        return args.length < index ? defaultValue : Integer.parseInt(args[index]);
    }

    /**
     * the main method, which takes from 1 to 5 arguments
     *
     * @param args an array of arguments of the form url [depth [downloads [extractors [perHost]]]]
     *             Namely:
     *             {@code url}: the initial downloadable url
     *             {@code depth}: the depth of downloads from websites
     *             {@code downloads}: the number of streams to download
     *             {@code extractors}: the number of streams to extract
     *             {@code perHost}: the maximum number of downloads from a single host
     */
    public static void main(String[] args) {
        if (args.length > 5 || args.length < 1) {
            System.err.println(("Your input value is incorrect"));
            return;
        }
        final int downloaders = getOrDefault(2, args, 1);
        try (Crawler webCrawler = new WebCrawler(
                new CachingDownloader(1),
                downloaders,
                getOrDefault(3, args, 1),
                getOrDefault(4, args, downloaders))) {
            webCrawler.download(args[0], getOrDefault(1, args, 1));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
