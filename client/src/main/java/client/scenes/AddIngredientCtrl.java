package client.scenes;

import client.i18n.I18n;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Nutrition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class AddIngredientCtrl implements Initializable {

    private final ServerUtils server;

    @FXML
    private TextField nameIngredient;
    @FXML
    private TextField fatIngredient;
    @FXML
    private TextField proteinIngredient;
    @FXML
    private TextField carbsIngredient;
    @FXML
    private Label titleLabel;
    @FXML
    private Label fatLabel;
    @FXML
    private Label proteinLabel;
    @FXML
    private Label carbsLabel;
    @FXML
    private Button addButton;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param server the server utils
     */
    @Inject
    public AddIngredientCtrl(ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind labels and buttons to i18n
        titleLabel.textProperty().bind(I18n.createStringBinding("addIngredient.title"));
        fatLabel.textProperty().bind(I18n.createStringBinding("addIngredient.fat"));
        proteinLabel.textProperty().bind(I18n.createStringBinding("addIngredient.protein"));
        carbsLabel.textProperty().bind(I18n.createStringBinding("addIngredient.carbs"));
        addButton.textProperty().bind(I18n.createStringBinding("addIngredient.add"));
    }

    /**
     * Adds an ingredient based on the name in the TextField
     * should probably do a separate window to add an ingredient
     */
    public void addIngredient() {
        //grab all the ingredients components
        String name = nameIngredient.getText();
        String fat = fatIngredient.getText();
        String protein = proteinIngredient.getText();
        String carbs = carbsIngredient.getText();

        //verify they are not empty or null
        if (name == null || name.trim().isEmpty() ||
                fat == null || fat.trim().isEmpty() ||
                protein == null || protein.trim().isEmpty() ||
                carbs == null || carbs.trim().isEmpty()) {
            return;
        }
        //Check if they are all valid
        if (!(isStringBigDecimal(fat) && isStringBigDecimal(protein) &&
                isStringBigDecimal(protein) && isStringBigDecimal(carbs))) {
            return;
        }
        Nutrition nutrition = new Nutrition(
                new BigDecimal(fat),
                new BigDecimal(protein),
                new BigDecimal(carbs));
        BigDecimal kCal = nutrition.computeKcalPer100g();
        nutrition.setKcalPer100g(kCal);

        Ingredient i = new Ingredient();
        i.setName(name);
        i.setNutrition(nutrition);
        server.addIngredient(i);
        clearFields();
        Stage stage = (Stage) nameIngredient.getScene().getWindow();
        stage.close();
    }

    /**
     * Helper methode to clear all the nutrients and ingredient name fields
     */
    private void clearFields() {
        nameIngredient.clear();
        fatIngredient.clear();
        proteinIngredient.clear();
        carbsIngredient.clear();
    }

    /**
     * parses a string into a big decimal
     * in case of a valid big decimal, return true
     * @param string string containing a big decimal
     * @return true if it contains Big Decimal, false otherwise
     */
    public static boolean isStringBigDecimal(String string){
        if (string == null || string.trim().isEmpty()) {
            return false;
        }
        try {
            new BigDecimal(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
