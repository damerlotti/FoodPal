package client.utils;

import commons.Ingredient;
import commons.Measurement;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.Optional;

/**
 * Custom CellFactory for the Shopping List ListView.
 * Defines how each shopping item in the list should look and behave(ish.)
 */
public class ShoppingListCellFactory implements Callback<ListView<ShoppingItem>,
        ListCell<ShoppingItem>> {

    /**
     * Creates a new Cell instance for the ListView.
     * @param param the ListView that will use this cell.
     * @return a new ShoppingListCell instance.
     */
    @Override
    public ListCell<ShoppingItem> call(ListView<ShoppingItem> param) {
        return new ShoppingListCell();
    }

    /**
     * Inner class representing the visual layout of a single row in the shopping list.
     * Extends ListCell to tap into JavaFX's cell recycling mechanism.
     */
    private static class ShoppingListCell extends ListCell<ShoppingItem> {
        private final HBox layout;
        private final CheckBox checkBox;
        private final Text nameText;
        private final Label sourceLabel;
        private final Button editButton;
        private final Button deleteButton;

        /**
         * Constructor: Initializes the UI components (checkbox, text, button etc)
         * and sets up the layout and event listeners.
         */
        public ShoppingListCell() {
            layout = new HBox(10);
            layout.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            checkBox = new CheckBox();
            nameText = new Text();
            sourceLabel = new Label();
            sourceLabel.setStyle("-fx-text-fill: gray; " +
                    "-fx-font-style: italic; " +
                    "-fx-font-size: 0.9em;"); //very cutely shows where the ingredient
            // comes from in the shopping list

            editButton = new Button("Edit");
            editButton.setOnAction(e -> handleEdit());

            deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-background-color: #ffcccc; -fx-text-fill: red;");
            deleteButton.setOnAction(e -> {
                ShoppingItem item = getItem();
                if (item != null) {
                    getListView().getItems().remove(item);
                }
            });

            layout.getChildren().addAll(checkBox, nameText, sourceLabel,
                    editButton, deleteButton);

            checkBox.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                if (getItem() != null) {
                    getItem().setChecked(isSelected);
                }
            });

            deleteButton.setOnAction(event -> {
                ShoppingItem item = getItem();
                if (item != null) {
                    getListView().getItems().remove(item);
                }
            });
        }

        private void handleEdit() {
            ShoppingItem item = getItem();
            if (item == null) {
                return;
            }

            Optional<Measurement> result = IngredientDialog.showEditAmount(item.getMeasurement());
            result.ifPresent(newMeasurement -> {
                item.setMeasurement(newMeasurement);
                updateItem(item, false);
            });
        }

        /**
         * Updates the content of the cell. Called automatically by JavaFX.
         * @param item the ShoppingItem object to display.
         * @param empty whether this cell is empty
         */
        @Override
        protected void updateItem(ShoppingItem item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                checkBox.setSelected(item.getChecked());

                Measurement measurement = item.getMeasurement();
                Ingredient ingredient = item.getIngredient();

                String amountStr = measurement.getValue() != null ?
                        measurement.getValue().stripTrailingZeros().toPlainString() : "";
                String unitStr = measurement.getUnit() != null ?
                        measurement.getUnit().getHumanReadableName() : "";
                String nameStr = ingredient.getName();

                nameText.setText(amountStr + " " + unitStr + " " + nameStr);

                long duplicateCount = getListView().getItems().stream()
                        .filter(i -> i.getIngredient().getName().equalsIgnoreCase(nameStr))
                        .count();

                if (duplicateCount > 1) {
                    String source = item.getSource() == null ? "" : item.getSource();
                    sourceLabel.setText("(" + source + ")");
                }
                else {
                    sourceLabel.setText("");
                }

                setGraphic(layout);
            }
        }
    }
}
