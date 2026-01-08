package client.i18n;

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
        return ResourceBundle.getBundle("i18n.messages", getLocale());
    }
}
