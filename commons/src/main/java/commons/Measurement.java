package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Embeddable
public class Measurement {
    public static final int MEASUREMENT_PRECISION = 19;
    public static final int MEASUREMENT_SCALE = 8;
    public static final int MEASUREMENT_UNIT_LENGTH = 55;

    @Column(name = "value", precision = MEASUREMENT_PRECISION, scale = MEASUREMENT_SCALE)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", length = MEASUREMENT_UNIT_LENGTH)
    private Unit unit;

    /**
     * Empty constructor.
     */
    public Measurement() {}

    /**
     * Creates a new Measurement, with given value and unit.
     * @param value The amount of UNIT to hold.
     * @param unit The unit to store the measurement.
     */
    public Measurement(BigDecimal value, Unit unit) {
        this.value = value;
        this.unit = unit;
    }

    /**
     * Returns whether the unit used for measurement is informal.
     * @return whether the unit used for measurement is informal.
     */
    // NOTE(calco): Jackson automatically treats any `is/get...` as properties, hence exporting them
    // I don't think this is desired behaviour, as these should be computed with the actual class.
    // Might be worth actually doing this once we transition to clearly defined DTOs & Mappers.
    @JsonIgnore
    public boolean isInformal() {
        return this.unit.isInformal();
    }

    /**
     * Returns the display text to be used for UI / pretty printing.
     * @return the display text to be used for UI / pretty printing.
     */
    @JsonIgnore
    public String getDisplayText() {
        // TODO(calco): Actually return display text lmao.
        return "";
    }

    // TODO(calco):
    //  We can't really seamlessly convert from all given units, we'd need density data or some bs
    /**
     * Converts the current unit to its base value.
     * @return The converted value from current unit to base unit.
     */
    public BigDecimal toBaseUnit() {
        double factor = unit.getConversionFactorToBase();
        BigDecimal base = value.multiply(BigDecimal.valueOf(factor));
        return base.setScale(MEASUREMENT_SCALE, RoundingMode.HALF_UP);
    }


    /**
     * Converts this measurement into another unit of the same dimension.
     * @param targetUnit The unit to convert into.
     * @return A new Measurement converted to the given unit.
     * @throws IllegalArgumentException if units have different dimensions.
     */
    public Measurement convert(Unit targetUnit) {
        if (targetUnit == null) {
            throw new IllegalArgumentException("targetUnit cannot be null");
        }

        if (this.unit.getDimension() != targetUnit.getDimension()) {
            throw new IllegalArgumentException("Cannot convert from "
                    + this.unit.getDimension() + " to " + targetUnit.getDimension());
        }

        // Step 1: convert to base unit (grams/ml/count)
        BigDecimal baseValue = this.toBaseUnit();

        // Step 2: convert base → target
        BigDecimal factor = BigDecimal.valueOf(targetUnit.getConversionFactorToBase());
        BigDecimal converted = baseValue.divide(factor, MEASUREMENT_SCALE, RoundingMode.HALF_UP);

        return new Measurement(converted, targetUnit);
    }

    /**
     * Returns the value.
     * @return The value.
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value.
     * @param value The value.
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Returns the unit of measurement.
     * @return The unit of measurement.
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets the unit of measurement.
     * @param unit The unit of measurement.
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return (value != null ? value.stripTrailingZeros().toPlainString() : "null")
                + " " + (unit != null ? unit.name() : "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Measurement that = (Measurement) o;
        return Objects.equals(value, that.value) &&
                unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, unit);
    }
}
