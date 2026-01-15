package client.scenes;

import client.i18n.I18n;
import client.utils.ServerUtils;
import commons.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class IngredientDialog {
    private static BigDecimal check(String text) {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    private static void setFieldsLocked(boolean locked, TextField... fields) {
        for (TextField f : fields) {
            f.setDisable(locked);
            if (locked) {
                f.setStyle("-fx-opacity: 0.7; -fx-background-color: #eee;");
            }
            else {
                f.setStyle("");
            }
        }
    }
    private static String removeZeros(BigDecimal number) {
        if (number == null) {
            return "0";
        }
        return number.stripTrailingZeros().toPlainString();
    }
    /**
     * Shows a customized dialog in which the user can configure the ingredient
     * @param allIngredients all our existing ingredients from which the user can choose
     * @param server injection of the server, to be able to save the newly created ingredient
     * @return a potential RecipeIngredient, if the user inputs correct values
     */
    public static Optional<RecipeIngredient> show(List<Ingredient> allIngredients,
                                                  ServerUtils server) {
        Dialog<RecipeIngredient> dialog = new Dialog<>();
        dialog.setTitle(I18n.get("dialog.addIngredient.title"));
        dialog.setHeaderText(I18n.get("dialog.addIngredient.header"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);  // gap between all the items in the dialog
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));  // padding, clockwise direction(top, right..)

        ComboBox<Ingredient> name = new ComboBox<>();
        name.setEditable(true);  // so we can also type sth
        name.getItems().addAll(allIngredients);
        grid.add(new Label(I18n.get("dialog.addIngredient.ingredient")), 0, 0);
        grid.add(name, 1, 0);

        TextField amount = new TextField();
        ComboBox<Unit> unit = new ComboBox<>();
        unit.getItems().addAll(Unit.values());
        unit.setValue(Unit.G);
        grid.add(new Label(I18n.get("dialog.addIngredient.amount")), 0, 1);
        grid.add(amount, 1, 1);
        grid.add(unit, 2, 1);

        TextField fatField = new TextField();
        TextField proteinField = new TextField();
        TextField carbsField = new TextField();
        grid.add(new Label(I18n.get("dialog.addIngredient.fat")), 0, 2);
        grid.add(fatField, 1, 2);
        grid.add(new Label(I18n.get("dialog.addIngredient.protein")), 0, 3);
        grid.add(proteinField, 1, 3);
        grid.add(new Label(I18n.get("dialog.addIngredient.carbs")), 0, 4);
        grid.add(carbsField, 1, 4);

        // this happens when anything changes in the name field
        name.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            // checking if what the user types matches sth from the list of existing ingredients
            Ingredient match = allIngredients.stream()
                    .filter(i -> i.getName().equalsIgnoreCase(newText.trim()))
                    .findFirst()
                    .orElse(null);
            if (match != null) {  // existing ingredient, so we lock the values in place
                fatField.setText(removeZeros(match.getNutrition().getFatPer100g()));
                proteinField.setText(removeZeros(match.getNutrition().getProteinPer100g()));
                carbsField.setText(removeZeros(match.getNutrition().getCarbsPer100g()));
                setFieldsLocked(true, fatField, proteinField, carbsField);
            }
            else {  // new ingredient
                if (fatField.isDisable()) {
                    fatField.clear();
                    proteinField.clear();
                    carbsField.clear();
                }
                setFieldsLocked(false, fatField, proteinField, carbsField);  // unlock them
            }
        });

        dialog.getDialogPane().setContent(grid);

        // this is returned when the user clicks on 'Add'
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {   // construct the recipe ingredient
                    String n = name.getEditor().getText();
                    Ingredient finalIng;
                    Ingredient existing = name.getItems().stream()
                            .filter(i -> i.getName().equalsIgnoreCase(n))
                            .findFirst()
                            .orElse(null);

                    if (existing != null) {  // existing ingredient
                        finalIng = existing;
                    }
                    else {
                        finalIng = new Ingredient();
                        finalIng.setName(n);

                        BigDecimal fat = check(fatField.getText());
                        BigDecimal protein = check(proteinField.getText());
                        BigDecimal carbs = check(carbsField.getText());

                        Nutrition nut = new Nutrition(fat, protein, carbs);
                        finalIng.setNutrition(nut);
                        finalIng = server.addIngredient(finalIng);
                    }
                    BigDecimal a = check(amount.getText());
                    Measurement m = new Measurement(a, unit.getValue());

                    RecipeIngredient ans = new RecipeIngredient();
                    ans.setIngredient(finalIng);
                    ans.setAmount(m);
                    return ans;
                } catch (Exception e) { return null; }
            }
            return null;
        });
        return dialog.showAndWait();
    }

    /**
     * Shows a simple dialog in which the user can edit the amount of an ingredient inside a recipe
     * @param currentAmount the current amount of that ingredient, for the user to know
     * @return the new measurement (value and unit)
     */
    public static Optional<Measurement> showEditAmount(Measurement currentAmount) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(I18n.get("dialog.editAmount.title"));
        dialog.setHeaderText(I18n.get("dialog.editAmount.header"));
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField amtField = new TextField();
        ComboBox<Unit> units = new ComboBox<>();
        units.getItems().addAll(Unit.values());

        if (currentAmount != null) {
            amtField.setText(currentAmount.getValue().stripTrailingZeros().toPlainString());
            units.setValue(currentAmount.getUnit());
        }

        grid.add(new Label(I18n.get("dialog.addIngredient.amount")), 0, 0);
        grid.add(amtField, 1, 0);
        grid.add(units, 2, 0);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> buttonClicked = dialog.showAndWait();

        if (buttonClicked.isPresent() && buttonClicked.get() == ButtonType.OK) {
            try {
                BigDecimal val = check(amtField.getText());
                Unit unit = units.getValue();
                return Optional.of(new Measurement(val, unit));
            } catch (Exception e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
