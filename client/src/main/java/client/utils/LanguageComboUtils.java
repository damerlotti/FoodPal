package client.utils;

import client.i18n.Flags;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Locale;
import java.util.Objects;

public class LanguageComboUtils {
    private LanguageComboUtils() {
    }

    /**
     * creates the flag locale
     * @return a ListCell<Locale> which shows the flag
     */
    public static ListCell<Locale> createFlagCell() {
        return new ListCell<>() {

            private final ImageView iv = new ImageView();

            {
                iv.setFitWidth(20);
                iv.setFitHeight(14);
                iv.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(Locale locale, boolean empty) {
                super.updateItem(locale, empty);

                if (empty || locale == null) {
                    setText(null);
                    setGraphic(null);
                    setTooltip(null);
                    return;
                }

                String path = Flags.flagPathFor(locale);
                if (path != null) {
                    Image img = new Image(Objects.requireNonNull(
                            getClass().getClassLoader().getResourceAsStream(path),
                            "Missing flag resource: " + path
                    ));
                    iv.setImage(img);
                    setGraphic(iv);
                } else {
                    setGraphic(null);
                }

                setText(null);
                setTooltip(new javafx.scene.control.Tooltip(locale.getDisplayName(locale)));
            }
        };
    }
}
