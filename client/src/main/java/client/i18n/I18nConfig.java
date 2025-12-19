package client.i18n;

import java.util.List;
import java.util.Locale;

public final class I18nConfig {
    private I18nConfig() {}

    public static final Locale DEFAULT = Locale.ENGLISH;

    public static final List<Locale> SUPPORTED = List.of(
            Locale.ENGLISH,
            Locale.forLanguageTag("ro-RO"),
            Locale.forLanguageTag("nl-NL")
    );
}
