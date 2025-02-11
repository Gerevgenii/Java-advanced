package info.kgeorgiy.ja.gerasimov.i18n;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.BreakIterator;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import static info.kgeorgiy.ja.gerasimov.i18n.I18n.*;

public class I18nTests {
    final Locale locale = Locale.of("ru", "RU");
    String file = Files.readString(Path.of("input.txt"));

    public I18nTests() throws IOException {
    }

    @Test
    void testMain() throws IOException {
        Assertions.assertDoesNotThrow(() -> I18n.main(
                Locale.of("ru", "RU").toLanguageTag(),
                "ru-RU",
                "input.txt"
        ));
        Assertions.assertDoesNotThrow(() -> I18n.main(
                Locale.of("ru", "RU").toLanguageTag(),
                "ru-RU"
        ));
        Assertions.assertDoesNotThrow(() -> I18n.main(Locale.of("ru", "RU").toLanguageTag()));
        Assertions.assertDoesNotThrow(() -> I18n.main());
        /*Assertions.assertDoesNotThrow(() ->
                I18n.main(
                        Locale.of("ru", "RU").toLanguageTag(),
                        "ru-RU",
                        "asdfasdfasdf.txt",
                        "outputFile.txt"
                ));*/
        I18n.main(
                Locale.of("ru", "RU").toLanguageTag(),
                "ru-RU",
                "input.txt",
                "outputFile.txt"
        );
    }

    @Test
    void testSentenceStatistics() {
        final I18n.Statistics<String, Double> sentenceStatistic =
                getLettersStatistics(
                        locale,
                        file,
                        BreakIterator.getSentenceInstance(locale)
                );
        Assertions.assertEquals(30, sentenceStatistic.count());
        Assertions.assertEquals(30, sentenceStatistic.specialCount());
        Assertions.assertEquals(38.333333333333336, sentenceStatistic.between());
    }

    @Test
    void testWordStatistics() {
        final I18n.Statistics<String, Double> wordStatistic =
                getLettersStatistics(
                        locale,
                        file,
                        BreakIterator.getWordInstance(locale)
                );

        Assertions.assertEquals(446, wordStatistic.count());
        Assertions.assertEquals(78, wordStatistic.specialCount());
        Assertions.assertEquals(2.2443946188340806, wordStatistic.between());
        Assertions.assertEquals(15, wordStatistic.maxLength().length());
    }

    @Test
    void testNumberStatistics() {
        final I18n.Statistics<Double, Double> numberStatistic =
                getNumberStatistics(
                        file,
                        NumberFormat.getNumberInstance(locale)
                );
        Assertions.assertEquals(35, numberStatistic.count());
        Assertions.assertEquals(21, numberStatistic.specialCount());
        Assertions.assertEquals(1, numberStatistic.minLength().length());
        Assertions.assertEquals(9, numberStatistic.maxLength().length());
        Assertions.assertEquals(160.96766666666667, numberStatistic.between());
    }

    @Test
    void testMoneyStatistics() {
        final I18n.Statistics<Double, Double> moneyStatistic =
                getNumberStatistics(
                        file,
                        NumberFormat.getCurrencyInstance(locale)
                );
        Assertions.assertEquals(3, moneyStatistic.count());
        Assertions.assertEquals(3, moneyStatistic.specialCount());
        Assertions.assertEquals(8, moneyStatistic.minLength().length());
        Assertions.assertEquals(8, moneyStatistic.maxLength().length());
        Assertions.assertEquals(222.83333333333334, moneyStatistic.between());
    }

    @Test
    void testDateStatistics() {
        final I18n.Statistics<Date, Date> dateStatistic =
                getDateStatistics(
                        locale,
                        file
                );
        Assertions.assertEquals(3, dateStatistic.count());
        Assertions.assertEquals(2, dateStatistic.specialCount());
        Assertions.assertEquals(15, dateStatistic.minLength().length());
        Assertions.assertEquals(16, dateStatistic.maxLength().length());
        Assertions.assertEquals("Fri Jun 11 12:00:00 MSD 1993", dateStatistic.between().toString());
    }

    @Test
    void myTestSentences() {
        final I18n.Statistics<String, Double> sentenceStatistic =
                getLettersStatistics(
                        locale,
                        """
                                Привет! Меня не зовут, я сам прихожу.
                                Однако я не любля когда меня не приглашают 5 раз за 200 ₽.
                                Я бы хотел быть там 21 мая 1993 г..
                                Однако можете меня пригласить 33 июня 1993 г. за 1000 ₽.
                                Я бы хотел быть там 21 мая 1993 г..
                                """,
                        BreakIterator.getSentenceInstance(locale)
                );
        Assertions.assertEquals(6, sentenceStatistic.count());
        Assertions.assertEquals(5, sentenceStatistic.specialCount());
        Assertions.assertEquals("Меня не зовут, я сам прихожу.", sentenceStatistic.min());
        Assertions.assertEquals("Я бы хотел быть там 21 мая 1993 г..", sentenceStatistic.max());
        Assertions.assertEquals(7, sentenceStatistic.minLength().length());
        Assertions.assertEquals(58, sentenceStatistic.maxLength().length());
    }

    @Test
    void myTestWord() {
        final I18n.Statistics<String, Double> wordStatistic =
                getLettersStatistics(
                        locale,
                        """
                                Привет! Меня не зовут, я сам прихожу.
                                Однако я не любля когда меня не приглашают 5 раз за 200 ₽.
                                Я бы хотел быть там 21 мая 1993 г..
                                Однако можете меня пригласить 33 июня 1993 г. за 1000 ₽.
                                Я бы хотел быть там 21 мая 1993 г..
                                """,
                        BreakIterator.getWordInstance(locale)
                );
        Assertions.assertEquals(108, wordStatistic.count());
        Assertions.assertEquals(37, wordStatistic.specialCount());
        Assertions.assertEquals("", wordStatistic.min());
        Assertions.assertEquals("₽", wordStatistic.max());
        Assertions.assertEquals(0, wordStatistic.minLength().length());
        Assertions.assertEquals(10, wordStatistic.maxLength().length());
    }

    @Test
    void myTestNumber() {
        final I18n.Statistics<Double, Double> numberStatistic =
                getNumberStatistics(
                        """
                                Привет! Меня не зовут, я 13 сам прихожу.
                                Однако я не любля когда меня не приглашают 5 раз за 200 ₽.
                                Я бы хотел быть там 21 мая 1993 г..
                                Однако можете меня пригласить 33 июня 1993 г. за 1000 ₽.
                                Я бы хотел быть там 21 мая 1993 г..
                                """,
                        NumberFormat.getNumberInstance(locale)
                );

        Assertions.assertEquals(10, numberStatistic.count());
        Assertions.assertEquals(7, numberStatistic.specialCount());
        Assertions.assertEquals(5.0, numberStatistic.min());
        Assertions.assertEquals(1993.0, numberStatistic.max());
        Assertions.assertEquals(1, numberStatistic.minLength().length());
        Assertions.assertEquals(4, numberStatistic.maxLength().length());
        Assertions.assertEquals(466.42857142857144, numberStatistic.between());
    }

    @Test
    void myTestMoney() {
        final I18n.Statistics<Double, Double> moneyStatistic =
                getNumberStatistics(
                        """
                                Привет! Меня не зовут, я 13 сам прихожу.
                                Однако я не любля когда меня не приглашают 5 раз за 200 ₽.
                                Я бы хотел быть 300 ₽ там 21 мая 1993 г..
                                Однако можете меня пригласить 33 июня 1993 г. за 1000 ₽.
                                Я бы 300 ₽ хотел быть там 21 мая 1993 г..
                                """,
                        NumberFormat.getCurrencyInstance(locale)
                );
        Assertions.assertEquals(4, moneyStatistic.count());
        Assertions.assertEquals(3, moneyStatistic.specialCount());
        Assertions.assertEquals(200, moneyStatistic.min());
        Assertions.assertEquals(1000, moneyStatistic.max());
        Assertions.assertEquals(5, moneyStatistic.minLength().length());
        Assertions.assertEquals(6, moneyStatistic.maxLength().length());
        Assertions.assertEquals(500, moneyStatistic.between());
    }

    @Test
    void myTestDate() {
        final I18n.Statistics<Date, Date> dateStatistic =
                getDateStatistics(
                        locale,
                        """
                                Привет! Меня не зовут, я 13 сам прихожу.
                                Однако я не любля когда меня не приглашают 5 раз за 200 ₽.
                                Я бы хотел быть 300 ₽ там 21 мая 1993 г..
                                Однако можете меня пригласить 30 июня 1993 г. за 1000 ₽.
                                Я бы 300 ₽ хотел быть там 21 мая 1993 г..
                                """
                );
        Assertions.assertEquals(3, dateStatistic.count());
        Assertions.assertEquals(2, dateStatistic.specialCount());
        Assertions.assertEquals("Fri May 21 00:00:00 MSD 1993", dateStatistic.min().toString());
        Assertions.assertEquals("Wed Jun 30 00:00:00 MSD 1993", dateStatistic.max().toString());
        Assertions.assertEquals(15, dateStatistic.minLength().length());
        Assertions.assertEquals(16, dateStatistic.maxLength().length());
        Assertions.assertEquals("Thu Jun 10 00:00:00 MSD 1993", dateStatistic.between().toString());
    }
}
