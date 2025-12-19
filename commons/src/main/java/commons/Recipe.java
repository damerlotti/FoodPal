package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Table(name = "Recipes")
public class Recipe {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference("recipe-ingredients")
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @JsonManagedReference("recipe-steps")
    private List<RecipeStep> steps = new ArrayList<>();

    @Column(name = "language_tag", nullable = false, length = 35)
    private String languageTag = Locale.ENGLISH.toLanguageTag();

    /**
     * Parameter-less constructor.
     */
    public Recipe() { }

    /**
     * Creates a new Recipe.
     * @param id The id of the Recipe, used for DB indexing.
     * @param name Display name of the recipe.
     * @param languageTag The BCP-47 language tag of the recipe.
     * @param ingredients Ordered list of ingredients used by the recipe.
     * @param steps Ordered list of steps to prepare the recipe.
     */
    public Recipe(long id, String name, String languageTag,
                  List<RecipeIngredient> ingredients, List<RecipeStep> steps) {
        this.id = id;
        this.name = name;
        // We assume English as a default language if the tag is null or blank
        this.languageTag = languageTag == null || languageTag.isBlank()
                ? Locale.ENGLISH.toLanguageTag()
                : languageTag;
        this.ingredients = ingredients;
        this.steps = steps;
    }

    /**
     * Returns the id of the recipe.
     * @return The id of the recipe.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the id of the recipe.
     * @param id The id of the recipe.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Returns the name of the recipe.
     * @return The name of the recipe.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the recipe.
     * @param name The name of the recipe.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the list of recipe ingredients.
     * @return The list of recipe ingredients.
     */
    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    /**
     * Sets the list of recipe ingredients, by reference.
     * Null value will be converted to an empty list to avoid NPEs.
     * @param ingredients The list of recipe ingredients.
     */
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = (ingredients == null) ? new ArrayList<>() : ingredients;
    }

    /**
     * Returns the preparation steps of the recipe.
     * @return The preparation steps of the recipe.
     */
    public List<RecipeStep> getSteps() {
        return steps;
    }

    /**
     * Set the preparation steps of the recipe, by reference.
     * Null value will be converted to an empty list to avoid NPEs.
     * @param steps The preparation steps of the recipe
     */
    public void setSteps(List<RecipeStep> steps) {
        this.steps = (steps == null) ? new ArrayList<>() : steps;
    }

    /**
     * Returns the BCP-47 language tag of the recipe (e.g. "en", "ro-RO").
     * @return language tag
     */
    public String getLanguageTag() {
        return languageTag;
    }

    /**
     * Sets the BCP-47 language tag of the recipe.
     * If the provided tag is null/blank we fallback to English rather than throwing.
     * @param languageTag language tag (e.g. "en", "ro-RO")
     */
    public void setLanguageTag(String languageTag) {
        if (languageTag == null || languageTag.isBlank()) {
            this.languageTag = Locale.ENGLISH.toLanguageTag();
            return;
        }
        this.languageTag = languageTag;
    }

    /**
     * Convenience getter to work with Locale in code.
     * @return locale for the stored language tag
     */
    @JsonIgnore
    public Locale getLocale() {
        return Locale.forLanguageTag(languageTag);
    }

    /**
     * Convenience setter to work with Locale in code.
     * If null is provided we fallback to English.
     * @param locale locale to store
     */
    @JsonIgnore
    public void setLocale(Locale locale) {
        if (locale == null) {
            this.languageTag = Locale.ENGLISH.toLanguageTag();
            return;
        }
        this.languageTag = locale.toLanguageTag();
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a string representing all information about the recipe in a Markdown format.
     * @return The markdown string.
     */
    public String toExportableMarkdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("# ").append(name).append("\n\n");

        sb.append("## Ingredients\n");
        for (RecipeIngredient ingredient : ingredients) {
            sb.append("- ") .append(ingredient.toString()).append("\n");
        }
        sb.append("\n");

        sb.append("## Steps\n");
        for (RecipeStep step : steps) {
            sb.append(step.toString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Returns a string representing all information about the recipe in a plaintext format.
     * @return The plaintext string
     */
    public String toExportablePlaintext() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n\n");

        sb.append("Ingredients\n");
        for (RecipeIngredient ingredient : ingredients) {
            sb.append("- ") .append(ingredient.toString()).append("\n");
        }
        sb.append("\n");

        sb.append("Steps\n");
        for (RecipeStep step : steps) {
            sb.append(step.toString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Clones the recipe, creating a fully new copy, without any ties to the original
     * @return The duplicated recipe
     */
    public Recipe cloneRecipe() {
        Recipe r = new Recipe();
        r.setName(name);

        r.setLanguageTag(this.languageTag);

        List<RecipeStep> rSteps = new ArrayList<>();
        for (RecipeStep step : steps) {
            RecipeStep rStep = new RecipeStep();
            rStep.setText(step.getText());
            rStep.setOrderIndex(step.getOrderIndex());
            rStep.setRecipe(r);
            rSteps.add(rStep);
        }
        r.setSteps(rSteps);

        List<RecipeIngredient> rIngredients = new ArrayList<>();
        for  (RecipeIngredient ingredient : ingredients) {
            RecipeIngredient rIngredient = new RecipeIngredient();
            rIngredient.setAmount(ingredient.getAmount());
            rIngredient.setIngredient(ingredient.getIngredient());
            rIngredient.setRecipe(r);
            rIngredients.add(rIngredient);
        }
        r.setIngredients(rIngredients);

        return r;
    }

    /**
     * Estimates the amount of kcal per 100g of recipe
     * @return the amount of kcal as BigDecimal
     */
    public BigDecimal estimatedRecipeKcal() {
        BigDecimal totalKcal = BigDecimal.ZERO;
        BigDecimal totalGrams = BigDecimal.ZERO;

        for (RecipeIngredient ingredient : ingredients) {
            BigDecimal kcal = ingredient.calculateKcal();
            BigDecimal grams = ingredient.getAmount().toBaseUnit();
            totalKcal = totalKcal.add(kcal);
            totalGrams = totalGrams.add(grams);
        }

        if (totalGrams.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalKcal.multiply(BigDecimal.valueOf(100))
                .divide(totalGrams, 0, RoundingMode.DOWN);
    }
}
