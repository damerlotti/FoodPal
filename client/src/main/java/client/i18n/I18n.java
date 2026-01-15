package client.i18n;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
    private static final ObjectProperty<Locale> locale =
            new SimpleObjectProperty<>(I18nConfig.DEFAULT);


    private I18n() {}

    /**
     * Returns the currently selected locale as a property
     * @return The currently selected locale as a property
     */
    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    /**
     * Returns the current selected locale
     * @return The current selected locale
     */
    public static Locale getLocale() {
        return locale.get();
    }

    /**
     * Sets the current selected locale
     * @param l The locale to set it to
     */
    public static void setLocale(Locale l) {
        locale.set(l);
    }

    /**
     * Returns the bundle containing language information
     * @return The bundle containing language information
     */
    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle("client.i18n.messages", getLocale());
    }

    /**
     * Gets the translated string for the given key
     * @param key The translation key
     * @return The translated string
     */
    public static String get(String key) {
        return bundle().getString(key);
    }

    /**
     * Creates a StringBinding that automatically updates when the locale changes
     * @param key The translation key
     * @return A StringBinding that provides the translated value
     */
    public static StringBinding createStringBinding(String key) {
        return Bindings.createStringBinding(() -> get(key), locale);
    }

    /**
     * Creates a StringBinding with format arguments that updates when locale changes
     * @param key The translation key
     * @param args The format arguments
     * @return A StringBinding that provides the translated and formatted value
     */
    public static StringBinding createStringBinding(String key, Object... args) {
        return Bindings.createStringBinding(
                () -> String.format(get(key), args), locale);
    }
}
