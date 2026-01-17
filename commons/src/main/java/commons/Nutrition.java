package commons;

import jakarta.persistence.*;

import java.math.BigDecimal;

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

    @Column(name = "sugar", precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
    private BigDecimal sugarPer100g;

    @Column(name = "sodium", precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
    private BigDecimal sodiumPer100g;

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
     * @param sugarPer100g Amount of sugar per 100 grams of ingredient.
     * @param sodiumPer100g Amount of sodium per 100 grams of ingredient.
     */
    public Nutrition(BigDecimal fatPer100g, BigDecimal proteinPer100g, BigDecimal carbsPer100g,
                     BigDecimal sugarPer100g, BigDecimal sodiumPer100g) {
        this.fatPer100g = fatPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.sugarPer100g = sugarPer100g;
        this.sodiumPer100g = sodiumPer100g;
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
     * Returns the sugar / 100g.
     * @return the sugar / 100g.
     */
    public BigDecimal getSugarPer100g() {
        return sugarPer100g;
    }

    /**
     * Sets the sugar / 100g.
     * @param sugarPer100g the sugar / 100g.
     */
    public void setSugarPer100g(BigDecimal sugarPer100g) {
        this.sugarPer100g = sugarPer100g;
        kcalPer100g = computeKcalPer100g();
    }

    /**
     * Returns the sodium / 100g.
     *
     * @return the sodium / 100g.
     */
    public BigDecimal getSodiumPer100g() {
        return sodiumPer100g;
    }

    /**
     * Sets the sodium / 100g.
     * @param sodiumPer100g the sodium / 100g.
     */
    public void setSodiumPer100g(BigDecimal sodiumPer100g) {
        this.sodiumPer100g = sodiumPer100g;
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
     * Computes the number of kilocalories / 100g based on the given
     * fat, protein, carbs sugar and sodium values.
     * @return The number of kilocalories / 100g.
     */
    public BigDecimal computeKcalPer100g() {
        BigDecimal fat = (fatPer100g != null) ? fatPer100g : BigDecimal.ZERO;
        BigDecimal protein = (proteinPer100g != null) ? proteinPer100g : BigDecimal.ZERO;
        BigDecimal carbs = (carbsPer100g != null) ? carbsPer100g : BigDecimal.ZERO;
        BigDecimal sugar = (sugarPer100g != null) ? sugarPer100g : BigDecimal.ZERO;
        BigDecimal sodium = (sodiumPer100g != null) ? sodiumPer100g : BigDecimal.ZERO;
        BigDecimal kcal = BigDecimal.ZERO;
        kcal = kcal.add(fat.multiply(BigDecimal.valueOf(9)));
        kcal = kcal.add(protein.multiply(BigDecimal.valueOf(4)));
        kcal = kcal.add(carbs.multiply(BigDecimal.valueOf(4)));
        return kcal;
    }
}
