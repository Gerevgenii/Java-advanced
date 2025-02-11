package info.kgeorgiy.ja.gerasimov.i18n;

import java.util.ListResourceBundle;

public class MyBundle_ru_RU
        extends ListResourceBundle {
    private final Object[][] CONTENTS = {
            {"Sentence", "Статистика по предложениям"},
            {"countSentence", "Число предложений"},
            {"specialCountOne", "различное"},
            {"count1", "различное"},
            {"count2", "различных"},
            {"minSentence", "Минимальное предложение"},
            {"maxSentence", "Максимальное предложение"},
            {"minLengthSentence", "Минимальная длина предложения"},
            {"maxLengthSentence", "Максимальная длина предложения"},
            {"betweenSentence", "Средняя длина предложения"},
            {"Words", "Статистика по словам"},
            {"countWords", "Число слов"},
            {"minWords", "Минимальное слово"},
            {"maxWords", "Максимальное слово"},
            {"minLengthWords", "Минимальная длина слова"},
            {"maxLengthWords", "Максимальная длина слова"},
            {"betweenWords", "Средняя длина слова"},
            {"Numbers", "Статистика по числам"},
            {"countNumbers", "Число чисел"},
            {"minNumbers", "Минимальное число"},
            {"maxNumbers", "Максимальное число"},
            {"minLengthNumbers", "Минимальная длина числа"},
            {"maxLengthNumbers", "Максимальная длина числа"},
            {"betweenNumbers", "Среднее число"},
            {"Currencies", "Статистика по суммам денег"},
            {"countCurrencies", "Число сумм"},
            {"minCurrencies", "Минимальная сумма"},
            {"maxCurrencies", "Максимальная сумма"},
            {"minLengthCurrencies", "Минимальная длина суммы"},
            {"maxLengthCurrencies", "Максимальная длина суммы"},
            {"betweenCurrencies", "Средняя сумма"},
            {"Dates", "Статистика по датам"},
            {"countDates", "Число дат"},
            {"minDates", "Минимальная дата"},
            {"maxDates", "Максимальная дата"},
            {"minLengthDates", "Минимальная длина даты"},
            {"maxLengthDates", "Максимальная длина даты"},
            {"betweenDates", "Средняя дата"},
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}