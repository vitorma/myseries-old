package mobi.myseries.gui.shared;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import mobi.myseries.shared.Validate;


public class DateFormats {
    private static final String PATTERN_FULL = "EEEE";
    private static final String PATTERN_SHORT = "EEE";


    public static DateFormat forShortWeekDay(Locale locale) {
        Validate.isNonNull(locale, "locale");

        return new SimpleDateFormat(PATTERN_SHORT, locale);
    }

    public static DateFormat forWeekDay(Locale locale) {
        Validate.isNonNull(locale, "locale");

        return new SimpleDateFormat(PATTERN_FULL, locale);
    }

    public static java.text.DateFormat forTime(DateFormat timeFormat) {
        // TODO Auto-generated method stub
        return null;
    }
}
