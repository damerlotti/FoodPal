package client.scenes;

import client.i18n.I18n;
import client.i18n.I18nConfig;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainLayoutCtrl implements Initializable {

    private final PrimaryCtrl primaryCtrl;

    @FXML
    private StackPane contentArea;
    
    @FXML
    private ComboBox<Locale> languageCombo;

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
        // Initialise combo box
        
//        languageCombo.setItems(FXCollections.observableArrayList());
        languageCombo.getItems().setAll(I18nConfig.SUPPORTED);
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
        
        languageCombo.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : capitalize(item.getDisplayName(item)));
            }
        });
        
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
}