package client.i18n;

import java.util.Locale;
import java.util.Map;

public final class Flags {
    public static final String FLAG_DIR = "client/flags/";
    
    public static final Map<String, String> LOCALE_TO_FLAG = Map.of(
            "en", "gb.png",
            "ro-RO", "ro.png",
            "nl-NL", "nl.png",
            "fr-FR", "fr.png"
    );

    private Flags() { }

    /**
     * Returns the proper resource path for the given locale flag.
     * @param locale The locale to get the path for
     * @return The path
     */
    public static String flagPathFor(Locale locale) {
        if (locale == null) {
            return null;
        }

        // Try exact match first (ro-RO)
        String exact = LOCALE_TO_FLAG.get(locale.toLanguageTag());
        if (exact != null) {
            return FLAG_DIR + exact;
        }

        // Fallback to language only (en)
        return FLAG_DIR + LOCALE_TO_FLAG.get(locale.getLanguage());
    }
}
