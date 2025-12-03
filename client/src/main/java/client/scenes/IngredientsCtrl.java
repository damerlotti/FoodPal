package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Ingredient;
import commons.Measurement;
import commons.Nutrition;
import commons.Unit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.fxml.Initializable;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

public class IngredientsCtrl implements Initializable {

    private final ServerUtils server;
    private ObservableList<Ingredient> data;

    @FXML
    private TextField nameIngredient;
    @FXML
    private TextField fatIngredient;
    @FXML
    private TextField proteinIngredient;
    @FXML
    private TextField carbsIngredient;
    @FXML
    private TextField kCalIngredient;

    @FXML
    private ListView<Ingredient> ingredientsList;

    /**
     * Constructor for the controller with Guice, which gives us the only ServerUtils
     * @param server the server utils
     */
    @Inject
    public IngredientsCtrl(ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // special javafx list that updates the UI automatically
        data = FXCollections.observableArrayList();
        ingredientsList.setItems(data);
        refresh();
    }

    /**
     * Loads all the ingredients in the ListView of Ingredients
     */
    public void refresh() {
        var list = server.getIngredients();
        data.clear();
        data.addAll(list);
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
        String kcal = kCalIngredient.getText();
        //verify they are not empty or null
        if (name == null || name.trim().isEmpty() ||
                fat == null || fat.trim().isEmpty() ||
                protein == null || protein.trim().isEmpty() ||
                carbs == null || carbs.trim().isEmpty() ||
                kcal == null || kcal.trim().isEmpty()) {
            return;
        }
        //Check if they are all valid
        if (!(isStringBigDecimal(fat) && isStringBigDecimal(protein) &&
                isStringBigDecimal(protein) && isStringBigDecimal(carbs) &&
                isStringBigDecimal(kcal))) {
            return;
        }
        Unit unitAll = Unit.G;
        Nutrition nutrition = new Nutrition(new Measurement(new BigDecimal(fat), unitAll),
                new Measurement(new BigDecimal(protein), unitAll),
                new Measurement(new BigDecimal(carbs), unitAll),
                new BigDecimal(kcal));
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setNutrition(nutrition);
        server.addIngredient(i);
        clearFields();
        refresh();
    }

    /**
     * Removes a certain ingredient from the database
     */
    public void removeIngredient() {
        var selected = ingredientsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Select an item to delete!");
            alert.showAndWait();
            return;
        }
        server.deleteIngredient(selected);
        refresh();
    }

    /**
     * Helper methode to clear all the nutrients and ingredient name fields
     */
    private void clearFields() {
        nameIngredient.clear();
        fatIngredient.clear();
        proteinIngredient.clear();
        carbsIngredient.clear();
        kCalIngredient.clear();
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
