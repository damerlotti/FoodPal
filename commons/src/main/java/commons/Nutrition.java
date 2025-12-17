package commons;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Objects;

@Embeddable
public class Nutrition {
    @Column(name = "fat", precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
    private BigDecimal fatPer100g;

    @Column(name = "protein", precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
    private BigDecimal proteinPer100g;

    @Column(name = "carbs", precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
    private BigDecimal carbsPer100g;

    @Column(name = "kcal",
            precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
    private BigDecimal kcalPer100g;

    /**
     * Empty constructor.
     */
    public Nutrition() { }

    /**
     * Creates a new Nutrition with the specified parameters.
     * Automatically computes kilo calories based on fat, protein and carbs.
     *
     * @param fatPer100g Amount of fat per 100 grams of ingredient.
     * @param proteinPer100g Amount of protein per 100 grams of ingredient.
     * @param carbsPer100g Amount of carbs per 100 grams of ingredient.
     */
    public Nutrition(BigDecimal fatPer100g, BigDecimal proteinPer100g, BigDecimal carbsPer100g) {
        this.fatPer100g = fatPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = computeKcalPer100g();
    }

    /**
     * Returns the fat / 100g.
     * @return The fat / 100g.
     */
    public BigDecimal getFatPer100g() {
        return fatPer100g;
    }

    /**
     * Sets the fat / 100g.
     * @param fatPer100g The fat / 100g.
     */
    public void setFatPer100g(BigDecimal fatPer100g) {
        this.fatPer100g = fatPer100g;
        kcalPer100g = computeKcalPer100g();
    }

    /**
     * Returns the protein / 100g.
     * @return The protein / 100g.
     */
    public BigDecimal getProteinPer100g() {
        return proteinPer100g;
    }

    /**
     * Sets the protein / 100g.
     * @param proteinPer100g The protein / 100g.
     */
    public void setProteinPer100g(BigDecimal proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
        kcalPer100g = computeKcalPer100g();
    }

    /**
     * Returns the carbs / 100g.
     * @return The carbs / 100g.
     */
    public BigDecimal getCarbsPer100g() {
        return carbsPer100g;
    }

    /**
     * Sets the carbs / 100g.
     * @param carbsPer100g The carbs / 100g.
     */
    public void setCarbsPer100g(BigDecimal carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
        kcalPer100g = computeKcalPer100g();
    }

    /**
     * Returns the kilocalories / 100g.
     * @return The kilocalories / 100g.
     */
    public BigDecimal getKcalPer100g() {
        return kcalPer100g;
    }

    /**
     * Sets the kilocalories / 100g.
     * @param kcalPer100g The kilocalories / 100g.
     */
    public void setKcalPer100g(BigDecimal kcalPer100g) {
        this.kcalPer100g = kcalPer100g;
    }

    /**
     * Computes the number of kilocalories / 100g based on the given fat, protein and carbs values.
     * @return The number of kilocalories / 100g.
     */
    public BigDecimal computeKcalPer100g() {
        BigDecimal fat = (fatPer100g != null) ? fatPer100g : BigDecimal.ZERO;
        BigDecimal protein = (proteinPer100g != null) ? proteinPer100g : BigDecimal.ZERO;
        BigDecimal carbs = (carbsPer100g != null) ? carbsPer100g : BigDecimal.ZERO;
        BigDecimal kcal = BigDecimal.ZERO;
        kcal = kcal.add(fat.multiply(BigDecimal.valueOf(9)));
        kcal = kcal.add(protein.multiply(BigDecimal.valueOf(4)));
        kcal = kcal.add(carbs.multiply(BigDecimal.valueOf(4)));
        return kcal;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Nutrition nutrition = (Nutrition) o;
        return Objects.equals(fatPer100g, nutrition.fatPer100g)
                && Objects.equals(proteinPer100g, nutrition.proteinPer100g)
                && Objects.equals(carbsPer100g, nutrition.carbsPer100g)
                && Objects.equals(kcalPer100g, nutrition.kcalPer100g);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fatPer100g, proteinPer100g, carbsPer100g, kcalPer100g);
    }
}
