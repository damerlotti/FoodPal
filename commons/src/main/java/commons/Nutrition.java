package commons;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Embeddable
public class Nutrition {
    @Enumerated(EnumType.STRING)
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(
            name = "fat_value",
            precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
        ),
        @AttributeOverride(name = "unit", column = @Column(
            name = "fat_unit",
            length = Measurement.MEASUREMENT_UNIT_LENGTH)
        ),
    })
    private Measurement fatPer100g;

    @Enumerated(EnumType.STRING)
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(
            name = "protein_value",
            precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
        ),
        @AttributeOverride(name = "unit", column = @Column(
            name = "protein_unit",
            length = Measurement.MEASUREMENT_UNIT_LENGTH)
        ),
    })
    private Measurement proteinPer100g;

    @Enumerated(EnumType.STRING)
    @AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(
            name = "carbs_value",
            precision = Measurement.MEASUREMENT_PRECISION,
            scale = Measurement.MEASUREMENT_SCALE)
        ),
        @AttributeOverride(name = "unit", column = @Column(
            name = "carbs_unit",
            length = Measurement.MEASUREMENT_UNIT_LENGTH)
        ),
    })
    private Measurement carbsPer100g;

    @Column(name = "kcal_per_100g",
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
    public Nutrition(Measurement fatPer100g, Measurement proteinPer100g, Measurement carbsPer100g) {
        this.fatPer100g = fatPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = computeKcalPer100gIfMissing();
    }

    /**
     * Creates a new Nutrition with the specified parameters.
     * Hard sets the kilocalories.
     *
     * @param fatPer100g Amount of fat per 100 grams of ingredient.
     * @param proteinPer100g Amount of protein per 100 grams of ingredient.
     * @param carbsPer100g Amount of carbs per 100 grams of ingredient.
     * @param kcalPer100g Amount of kilocalories per 100 grams of ingredient.
     */
    public Nutrition(Measurement fatPer100g, Measurement proteinPer100g, Measurement carbsPer100g,
                     BigDecimal kcalPer100g) {
        this.fatPer100g = fatPer100g;
        this.proteinPer100g = proteinPer100g;
        this.carbsPer100g = carbsPer100g;
        this.kcalPer100g = kcalPer100g;
    }

    /**
     * Returns the fat / 100g.
     * @return The fat / 100g.
     */
    public Measurement getFatPer100g() {
        return fatPer100g;
    }

    /**
     * Sets the fat / 100g.
     * @param fatPer100g The fat / 100g.
     */
    public void setFatPer100g(Measurement fatPer100g) {
        this.fatPer100g = fatPer100g;
    }

    /**
     * Returns the protein / 100g.
     * @return The protein / 100g.
     */
    public Measurement getProteinPer100g() {
        return proteinPer100g;
    }

    /**
     * Sets the protein / 100g.
     * @param proteinPer100g The protein / 100g.
     */
    public void setProteinPer100g(Measurement proteinPer100g) {
        this.proteinPer100g = proteinPer100g;
    }

    /**
     * Returns the carbs / 100g.
     * @return The carbs / 100g.
     */
    public Measurement getCarbsPer100g() {
        return carbsPer100g;
    }

    /**
     * Sets the carbs / 100g.
     * @param carbsPer100g The carbs / 100g.
     */
    public void setCarbsPer100g(Measurement carbsPer100g) {
        this.carbsPer100g = carbsPer100g;
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
    // TODO(calco): Actually look into how to implement this lol
    public BigDecimal computeKcalPer100gIfMissing() {
//        if (kcalPer100g != null) {
//            return kcalPer100g;
//        }
//        BigDecimal fat = fatPer100g.getBaseUnitSafeValue();
//        BigDecimal protein = proteinPer100g.getBaseUnitSafeValue();
//        BigDecimal carbs = carbsPer100g.getSafeValue();
//        if (fat == null && protein == null && carbs == null) {
//            return null;
//        }
//        BigDecimal kcal = BigDecimal.ZERO;
//        if (fat != null) kcal = kcal.add(fat.multiply(BigDecimal.valueOf(9)));
//        if (protein != null) kcal = kcal.add(protein.multiply(BigDecimal.valueOf(4)));
//        if (carbs != null) kcal = kcal.add(carbs.multiply(BigDecimal.valueOf(4)));
//        return kcal;
        return BigDecimal.ZERO;
    }
}
