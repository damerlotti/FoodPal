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
import java.net.URL;
import java.util.ResourceBundle;

import java.math.BigDecimal;

import static client.scenes.AddIngredientCtrl.isStringBigDecimal;

public class EditIngredientCtrl implements Initializable {

    private final ServerUtils server;
    private final PrimaryCtrl primaryCtrl;
    private Ingredient ingredientToEdit;

    @FXML
    private TextField nameField;
    @FXML
    private TextField fatField;
    @FXML
    private TextField proteinField;
    @FXML
    private TextField carbsField;
    @FXML
    private TextField sugarField;
    @FXML
    private TextField sodiumField;
    @FXML
    private Label titleLabel;
    @FXML
    private Label fatLabel;
    @FXML
    private Label proteinLabel;
    @FXML
    private Label carbsLabel;
    @FXML
    private Label sugarLabel;
    @FXML
    private Label sodiumLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Button doneButton;

    /**
     * Constructs an EditIngredientCtrl with the necessary server utilities and primary controller.
     * Guice automatically injects the singleton instances of these dependencies.
     * @param server The server utilities used for communicating with the backend database.
     * @param primaryCtrl The primary controller used for scene switching and UI management.
     */
    @Inject
    public EditIngredientCtrl(ServerUtils server, PrimaryCtrl primaryCtrl) {
        this.server = server;
        this.primaryCtrl = primaryCtrl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        titleLabel.textProperty().bind(I18n.createStringBinding("editIngredient.title"));
        nameLabel.textProperty().bind(I18n.createStringBinding("editIngredient.name"));
        fatLabel.textProperty().bind(I18n.createStringBinding("editIngredient.fat"));
        proteinLabel.textProperty().bind(I18n.createStringBinding("editIngredient.protein"));
        carbsLabel.textProperty().bind(I18n.createStringBinding("editIngredient.carbs"));
        sugarLabel.textProperty().bind(I18n.createStringBinding("editIngredient.sugar"));
        sodiumLabel.textProperty().bind(I18n.createStringBinding("editIngredient.sodium"));
        doneButton.textProperty().bind(I18n.createStringBinding("editIngredient.done"));
    }

    /**
     * Pre-fills the input fields with the data of the ingredient to be edited.
     * It also stores the ingredient instance to maintain a reference for the update process
     * @param ingredient the ingredient object containing the data to be displayed in th fieldz
     */
    public void setIngredient(Ingredient ingredient) {
        this.ingredientToEdit = ingredient;

        nameField.setText(ingredient.getName());
        if(ingredient.getNutrition() != null) {
            fatField.setText(safeString(ingredient.getNutrition().getFatPer100g()));
            proteinField.setText(safeString(ingredient.getNutrition().getProteinPer100g()));
            carbsField.setText(safeString(ingredient.getNutrition().getCarbsPer100g()));
            sugarField.setText(safeString(ingredient.getNutrition().getSugarPer100g()));
            sodiumField.setText(safeString(ingredient.getNutrition().getSodiumPer100g()));
        }
    }


    /**
     * Updates the existing ingredient based on the fields
     */
    public void updateIngredient() {
        if(ingredientToEdit == null) {
            return;
        }
        String name = nameField.getText();
        String fat = fatField.getText();
        String protein = proteinField.getText();
        String carbs = carbsField.getText();
        String sugar = sugarField.getText();
        String sodium = sodiumField.getText();

        if (name == null || name.trim().isEmpty()) {
            return;
        }
        if(!(isStringBigDecimal(fat) && isStringBigDecimal(protein) &&
                isStringBigDecimal(carbs))){
            return;
        }

        ingredientToEdit.setName(name);

        if(ingredientToEdit.getNutrition() == null) {
            ingredientToEdit.setNutrition(new Nutrition(BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));
        }

        Nutrition nutrition = ingredientToEdit.getNutrition();

        nutrition.setFatPer100g(parseBigDec(fat));
        nutrition.setProteinPer100g(parseBigDec(protein));
        nutrition.setCarbsPer100g(parseBigDec(carbs));
        nutrition.setSugarPer100g(parseBigDec(sugar));
        nutrition.setSodiumPer100g(parseBigDec(sodium));

        BigDecimal kCal = nutrition.computeKcalPer100g();
        nutrition.setKcalPer100g(kCal);

        server.addIngredient(ingredientToEdit);

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private String safeString(BigDecimal val) {
        if (val == null) {
            return "0";
        }
        return val.stripTrailingZeros().toPlainString();
    }

    private BigDecimal parseBigDec(String str) {
        if (isStringBigDecimal(str)) {
            return new BigDecimal(str);
        }
        return BigDecimal.ZERO;
    }

    /**
     * Parses a string into a BigDecimal to verify its validity.
     * Checks if the string is a valid representation of BigDecimal
     * @param string the string containing a potential bigdecimal value
     * @return true if the string can be parsed into a bigdecimal, false otherwise
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
