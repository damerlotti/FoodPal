package client.i18n;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
    private I18n() {}

    private static final ObjectProperty<Locale> locale =
            new SimpleObjectProperty<>(I18nConfig.DEFAULT);

    public static ObjectProperty<Locale> localeProperty() { return locale; }
    public static Locale getLocale() { return locale.get(); }
    public static void setLocale(Locale l) { locale.set(l); }

    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle("i18n.messages", getLocale());
    }
}
