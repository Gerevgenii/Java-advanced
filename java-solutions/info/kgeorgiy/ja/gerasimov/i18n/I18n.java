package info.kgeorgiy.ja.gerasimov.i18n;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.*;
import java.util.*;

@SuppressWarnings({"TypeMayBeWeakened", "UseOfObsoleteDateTimeApi"})
public class I18n {

    public static void main(String... args) throws IOException {
        if (args.length != 4 || args[0] == null ||
            args[1] == null || args[2] == null || args[3] == null) {
            System.err.println("Incorrect input values");
            return;
        }
//        try {
        parseArguments(args);
//        } catch (Throwable ignored) {
//            System.err.println("UncheckedException" + ignored.getMessage());
//        }
    }

    private static void parseArguments(String[] args) throws IOException {
        final Locale textLocale = getLocale(args[0]);
        final Locale outLocale = getLocale(args[1]);
        final String fileString = Files.readString(Path.of(args[2]));
        final Path path = Path.of(args[3]);
        getStatistics(textLocale, fileString, outLocale, path);
    }

    record Statistics<T, S>(
            int count,
            int specialCount,
            T min,
            T max,
            String minLength,
            String maxLength,
            S between
    ) {
    }

    static Statistics<String, Double> getLettersStatistics(Locale textLocale, String fileString, BreakIterator breakIterator) {
        final List<String> sentences = split(fileString, breakIterator);
        if (sentences.isEmpty()) {
            return new Statistics<>(
                    0,
                    0,
                    "",
                    "",
                    "",
                    "",
                    0.0d
            );
        }
        final TreeSet<String> specialStrings = new TreeSet<>(Collator.getInstance(textLocale));
        specialStrings.addAll(sentences);
        return new Statistics<>(
                sentences.size(),
                specialStrings.size(),
                specialStrings.first(),
                specialStrings.last(),
                sentences.stream().min(Comparator.comparing(String::length)).orElseThrow(),
                sentences.stream().max(Comparator.comparing(String::length)).orElseThrow(),
                sentences.stream().mapToInt(String::length).average().orElseThrow()
        );
    }

    record SpecialNumber<T>(
            T myValue,
            String thisDouble
    ) {

    }

    static Statistics<Double, Double> getNumberStatistics(String fileString, NumberFormat numberFormat) {
        final ParsePosition parsePosition = new ParsePosition(0);
        final TreeSet<SpecialNumber<Double>> numbers = new TreeSet<>(Comparator.comparing(SpecialNumber::myValue));
        int count = 0;
        while (parsePosition.getIndex() < fileString.length()) {
            final int position = parsePosition.getIndex();
            final Number value = numberFormat.parse(fileString, parsePosition);
            if (value == null) {
                int index = parsePosition.getIndex() + 1;
                parsePosition.setIndex(index);
                continue;
            }
            final double okDouble = value.doubleValue();
            count++;
            numbers.add(new SpecialNumber<>(
                    okDouble,
                    fileString.substring(position, parsePosition.getIndex())
            ));
        }

        if (numbers.isEmpty()) {
            return new Statistics<>(
                    count,
                    0,
                    0.0d,
                    0.0d,
                    "",
                    "",
                    0.0d
            );
        }
        return new Statistics<>(
                count,
                numbers.size(),
                numbers.first().myValue,
                numbers.last().myValue,
                numbers.stream().map(SpecialNumber::thisDouble).min(Comparator.comparing(String::length)).orElseThrow(),
                numbers.stream().map(SpecialNumber::thisDouble).max(Comparator.comparing(String::length)).orElseThrow(),
                numbers.stream().mapToDouble(SpecialNumber::myValue).average().orElseThrow()
        );
    }

    static Statistics<Date, Date> getDateStatistics(Locale textLocale, String fileString) {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, textLocale);

        final ParsePosition parsePosition = new ParsePosition(0);
        final TreeSet<SpecialNumber<Date>> numbers = new TreeSet<>(Comparator.comparing(SpecialNumber::myValue));
        int count = 0;

        while (parsePosition.getIndex() < fileString.length()) {
            final int position = parsePosition.getIndex();
            final Date value = dateFormat.parse(fileString, parsePosition);
            if (value == null) {
                parsePosition.setIndex(parsePosition.getIndex() + 1);
                continue;
            }
            count++;
            numbers.add(new SpecialNumber<>(
                    value,
                    fileString.substring(position, parsePosition.getIndex())
            ));
        }
        if (numbers.isEmpty()) {
            return new Statistics<>(
                    count,
                    0,
                    null,
                    null,
                    "",
                    "",
                    null
            );
        }
        return new Statistics<>(
                count,
                numbers.size(),
                numbers.first().myValue,
                numbers.last().myValue,
                numbers.stream().map(SpecialNumber::thisDouble).min(Comparator.comparing(String::length)).orElseThrow(),
                numbers.stream().map(SpecialNumber::thisDouble).max(Comparator.comparing(String::length)).orElseThrow(),
                new Date((long) numbers.stream().mapToLong(curDate -> curDate.myValue.getTime()).average().orElseThrow())
        );
    }

    private static void getStatistics(Locale textLocale, String fileString, Locale outLocale, Path path) throws IOException {
        final Statistics<String, Double> sentenceStatistic = getLettersStatistics(textLocale, fileString, BreakIterator.getSentenceInstance(textLocale));
        final Statistics<String, Double> wordStatistic = getLettersStatistics(textLocale, fileString, BreakIterator.getWordInstance(textLocale));
        final Statistics<Double, Double> numberStatistic = getNumberStatistics(fileString, NumberFormat.getNumberInstance(textLocale));
        final Statistics<Double, Double> moneyStatistic = getNumberStatistics(fileString, NumberFormat.getCurrencyInstance(textLocale));
        final Statistics<Date, Date> dateStatistic = getDateStatistics(textLocale, fileString);
        printStatistics(sentenceStatistic, wordStatistic, numberStatistic, moneyStatistic, dateStatistic, outLocale, path);
    }

    private static String getEnd(int value) {
        final double[] limits = {1, 5};
        final String[] strings = {
                "count1",
                "count2"
        };
        return new ChoiceFormat(limits, strings).format(value);
    }

    private static String printSpecialStatistic(
            String option,
            Statistics<String, Double> statistics,
            Locale outLocale,
            ResourceBundle bundle
    ) {

        return String.format(
                outLocale,
                """
                        %s
                            %s: %d (%d %s).
                            %s: "%s".
                            %s: "%s".
                            %s: %d ("%s").
                            %s: %d ("%s").
                            %s: %f.
                        """,
                bundle.getString(option),
                bundle.getString("count" + option),
                statistics.count(),
                statistics.specialCount(),
                bundle.getString(getEnd(statistics.specialCount() == 1 ? 1 : 5)),
                bundle.getString("min" + option),
                statistics.min(),
                bundle.getString("max" + option),
                statistics.max(),
                bundle.getString("minLength" + option),
                statistics.minLength().length(),
                statistics.minLength(),
                bundle.getString("maxLength" + option),
                statistics.maxLength().length(),
                statistics.maxLength(),
                bundle.getString("between" + option),
                statistics.between()
        );
    }

    private static String printSpecialStatistic(
            String option,
            Statistics<?, ?> statistics,
            Locale outLocale,
            ResourceBundle bundle,
            Format numberFormat
    ) {
        return String.format(
                outLocale,
                """
                        %s
                            %s: %d (%d %s).
                            %s: "%s".
                            %s: "%s".
                            %s: %d ("%s").
                            %s: %d ("%s").
                            %s: %s.
                        """,
                bundle.getString(option),
                bundle.getString("count" + option),
                statistics.count(),
                statistics.specialCount(),
                bundle.getString(getEnd(statistics.specialCount() == 1 ? 1 : 5)),
                bundle.getString("min" + option),
                numberFormat.format(statistics.min()),
                bundle.getString("max" + option),
                numberFormat.format(statistics.max()),
                bundle.getString("minLength" + option),
                statistics.minLength().length(),
                statistics.minLength(),
                bundle.getString("maxLength" + option),
                statistics.maxLength().length(),
                statistics.maxLength(),
                bundle.getString("between" + option),
                numberFormat.format(statistics.between())
        );
    }

    private static String printNullStatistic(
            String option,
            Locale outLocale,
            ResourceBundle bundle
    ) {
        return String.format(
                outLocale,
                """
                        %s
                            %s: 0 (%s 0).
                            No one data
                        """,
                bundle.getString(option),
                bundle.getString("count" + option),
                bundle.getString("count1")
        );
    }

    private static void printSentenceStatistic(
            Statistics<String, Double> sentenceStatistic,
            BufferedWriter bufferedWriter,
            Locale outLocale,
            ResourceBundle bundle
    ) throws IOException {
        bufferedWriter.write(printSpecialStatistic(
                "Sentence",
                sentenceStatistic,
                outLocale,
                bundle
        ));
    }

    private static void printWordStatistic(
            Statistics<String, Double> wordStatistic,
            BufferedWriter bufferedWriter,
            Locale outLocale,
            ResourceBundle bundle
    ) throws IOException {
        bufferedWriter.write(printSpecialStatistic(
                "Words",
                wordStatistic,
                outLocale,
                bundle
        ));
    }

    private static void printNumberStatistic(
            Statistics<Double, Double> numberStatistic,
            BufferedWriter bufferedWriter,
            Locale outLocale,
            ResourceBundle bundle
    ) throws IOException {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(outLocale);
        bufferedWriter.write(
                printSpecialStatistic(
                        "Numbers",
                        numberStatistic,
                        outLocale,
                        bundle,
                        numberFormat
                ));
    }

    private static void printMoneyStatistic(
            Statistics<Double, Double> moneyStatistic,
            BufferedWriter bufferedWriter,
            Locale outLocale,
            ResourceBundle bundle
    ) throws IOException {
        final NumberFormat moneyFormat = NumberFormat.getCurrencyInstance(outLocale);
        bufferedWriter.write(
                printSpecialStatistic(
                        "Currencies",
                        moneyStatistic,
                        outLocale,
                        bundle,
                        moneyFormat
                ));
    }

    private static void printDateStatistic(
            Statistics<Date, Date> dateStatistic,
            BufferedWriter bufferedWriter,
            Locale outLocale,
            ResourceBundle bundle
    ) throws IOException {
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, outLocale);
        bufferedWriter.write(

                dateStatistic.between != null ?
                        printSpecialStatistic(
                                "Dates",
                                dateStatistic,
                                outLocale,
                                bundle,
                                dateFormat
                        ) :
                        printNullStatistic(
                        "Dates",
                        outLocale,
                        bundle));
    }

    private static void printStatistics(
            Statistics<String, Double> sentenceStatistic,
            Statistics<String, Double> wordStatistic,
            Statistics<Double, Double> numberStatistic,
            Statistics<Double, Double> moneyStatistic,
            Statistics<Date, Date> dateStatistic,
            Locale outLocale,
            Path path
    ) throws IOException {
        final ResourceBundle bundle =
                ResourceBundle.getBundle("info.kgeorgiy.ja.gerasimov.i18n.MyBundle", outLocale);
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path)) {
            printSentenceStatistic(
                    sentenceStatistic,
                    bufferedWriter,
                    outLocale,
                    bundle
            );
            printWordStatistic(
                    wordStatistic,
                    bufferedWriter,
                    outLocale,
                    bundle
            );
            printNumberStatistic(
                    numberStatistic,
                    bufferedWriter,
                    outLocale,
                    bundle
            );
            printMoneyStatistic(
                    moneyStatistic,
                    bufferedWriter,
                    outLocale,
                    bundle
            );
            printDateStatistic(
                    dateStatistic,
                    bufferedWriter,
                    outLocale,
                    bundle
            );
        }
    }

    private static Locale getLocale(String arg) {
        return Locale
                .availableLocales()
                .filter(locale -> locale.toLanguageTag().equals(arg))
                .findFirst()
                .orElseThrow();
    }

    /**
     * @author Georgiy Korneev
     */
    private static List<String> split(final String text, final BreakIterator boundary) {
        boundary.setText(text);
        final List<String> parts = new ArrayList<>();
        for (
                int begin = boundary.first(), end = boundary.next();
                end != BreakIterator.DONE;
                begin = end, end = boundary.next()
        ) {
            String substring = text.substring(begin, end).stripTrailing();
            if (substring.endsWith(System.lineSeparator()))
                substring = substring.substring(0, substring.length() - System.lineSeparator().length());

            parts.add(substring);
        }
        return parts;
    }
}
