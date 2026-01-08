package client.scenes;

import client.i18n.I18n;
import client.i18n.I18nConfig;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainLayoutCtrl implements Initializable {

    private final PrimaryCtrl primaryCtrl;

    @FXML
    private StackPane contentArea;
    
    @FXML
    private ComboBox<Locale> languageCombo;

    @FXML
    private Button recipesButton;

    @FXML
    private Button ingredientsButton;

    /**
     * Constructor which injects the PrimaryCtrl, to which we'll tell what to display
     * @param primaryCtrl
     */
    @Inject
    public MainLayoutCtrl(PrimaryCtrl primaryCtrl) {
        this.primaryCtrl = primaryCtrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind button text to i18n
        recipesButton.textProperty().bind(I18n.createStringBinding("nav.recipes"));
        ingredientsButton.textProperty().bind(I18n.createStringBinding("nav.ingredients"));

        // Initialise combo box
        languageCombo.getItems().setAll(I18nConfig.SUPPORTED);
        languageCombo.setCellFactory(_lv -> createFlagCell());
        
        languageCombo.getSelectionModel().select(I18n.getLocale());

        languageCombo.valueProperty().addListener((obs, oldL, newL) -> {
            if (newL != null) {
                I18n.setLocale(newL);
            }
        });

        // show language name in its own language
        languageCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Locale locale) {
                if (locale == null) {
                    return "";
                }
                return capitalize(locale.getDisplayName(locale));
            }
            @Override public Locale fromString(String s) {
                return null;
            }
        });
//        
//        languageCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
//            @Override protected void updateItem(Locale item, boolean empty) {
//                super.updateItem(item, empty);
//                setText(empty || item == null ? null : capitalize(item.getDisplayName(item)));
//            }
//        });
//        
        languageCombo.setButtonCell(languageCombo.getCellFactory().call(null));
    }

    /**
     * Helper method to replace the center content
     * @param content the actual visuals
     */
    public void setCenter(Parent content) {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    /**
     * Targetting the View -> Recipes menuitem, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showRecipes() {
        primaryCtrl.showRecipes();
    }

    /**
     * Targetting the View -> Ingredients menu item, so we delegate responsibility to primaryCtrl
     */
    @FXML
    public void showIngredients() {
        primaryCtrl.showIngredients();
    }

    /**
     * Returns a string capitalized.
     * @param s The language string
     * @return The properly capitalised string
     */
    private static String capitalize(String s) {
        if (s == null || s.isBlank()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * @return The 
     */
    private ListCell<Locale> createFlagCell() {
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

                String path = client.i18n.Flags.flagPathFor(locale);
                if (path != null) {
                    Image img = new Image(Objects.requireNonNull(
                            getClass().getClassLoader().getResourceAsStream(path),
                            "Missing flag resource: " + path
                    ));
                    iv.setImage(img);
                    setGraphic(iv);
                } else {
                    setGraphic(null); // fallback if missing image
                }

                setText(null); // flags only
                setTooltip(new javafx.scene.control.Tooltip(locale.getDisplayName(locale)));
            }
        };
    }
}