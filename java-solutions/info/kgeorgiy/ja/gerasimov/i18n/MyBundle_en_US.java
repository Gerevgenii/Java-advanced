package info.kgeorgiy.ja.gerasimov.i18n;

import java.util.*;

public class MyBundle_en_US
        extends ListResourceBundle {
    private final Object[][] CONTENTS = {
            {"Sentence", "Sentence statistics"},
            {"countSentence", "Number of sentences:"},
            {"сount1", "various:"},
            {"сount2", "various:"},
            {"minSentence", "Minimum sentence:"},
            {"maxSentence", "Maximum offer:"},
            {"minLengthSentence", "Minimum sentence length:"},
            {"maxLengthSentence", "Maximum sentence length:"},
            {"betweenSentence", "Average sentence length:"},
            {"Words", "Statistics by words"},
            {"countWords", "Number of words:"},
            {"minWords", "Minimum word:"},
            {"maxWords", "Maximum word:"},
            {"minLengthWords", "Minimum word length:"},
            {"maxLengthWords", "Maximum word length:"},
            {"betweenWords", "Average word length:"},
            {"Numbers", "Statistics by numbers"},
            {"countNumbers", "Number of numbers:"},
            {"minNumbers", "Minimum number:"},
            {"maxNumbers", "Maximum number:"},
            {"minLengthNumbers", "Minimum number length:"},
            {"maxLengthNumbers", "Maximum number length:"},
            {"betweenNumbers", "Average number:"},
            {"Currencies", "Statistics on amounts of money"},
            {"countCurrencies", "Number of amounts:"},
            {"minCurrencies", "Minimum amount:"},
            {"maxCurrencies", "Maximum amount:"},
            {"minLengthCurrencies", "Minimum amount length:"},
            {"maxLengthCurrencies", "Maximum amount length:"},
            {"betweenCurrencies", "Average amount:"},
            {"Dates", "Date statistics"},
            {"Counts", "Number of dates:"},
            {"minDates", "Minimum date:"},
            {"maxDates", "Maximum date:"},
            {"minLengthDates", "Minimum date length:"},
            {"maxLengthDates", "Maximum date length:"},
            {"betweenDates", "Average date:"},
    };

    @Override
    protected Object[][] getContents() {
        return CONTENTS;
    }
}