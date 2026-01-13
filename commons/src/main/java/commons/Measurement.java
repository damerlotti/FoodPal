package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
     * Scales a measurement by some factor
     * @param factor the factor to scale
     * @return the new measurement
     */
    public Measurement scale(double factor) {
        if (this.value == null) {
            return new Measurement(java.math.BigDecimal.ZERO, this.unit);
        }

        java.math.BigDecimal newVal = this.value.multiply(java.math.BigDecimal.valueOf(factor));
        return new Measurement(newVal, this.unit);
    }
    /**
     * Normalizes measurements (e.g 1000g becomes 1kg)
     * @return the new measurement
     */
    public Measurement normalizeMeasurement() {
        if (unit == null) {
            return this;
        }
        if (unit == Unit.MG || unit == Unit.G || unit == Unit.KG) {
            Measurement base = this.convert(Unit.G);
            BigDecimal g = base.getValue();
            if (g.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                return this.convert(Unit.KG);
            }
            else if (g.compareTo(BigDecimal.ONE) >= 0) {
                return this.convert(Unit.G);
            }
            else {
                return this.convert(Unit.MG);
            }
        }
        if (unit == Unit.ML || unit == Unit.L) {
            Measurement base = this.convert(Unit.ML);
            BigDecimal ml = base.getValue();
            if (ml.compareTo(BigDecimal.valueOf(1000)) >= 0) {
                return this.convert(Unit.L);
            }
            else {
                return this.convert(Unit.ML);
            }
        }
        if (unit == Unit.TSP || unit == Unit.TBSP || unit == Unit.CUP) {
            if (unit == Unit.TSP && value.compareTo(BigDecimal.valueOf(3)) >= 0) {
                return this.convert(Unit.TBSP);
            }
            if (unit == Unit.TBSP && value.compareTo(BigDecimal.valueOf(4)) >= 0) {
                return this.convert(Unit.CUP);
            }
        }
        return this;
    }

    /**
     * Returns the value.
     * @return The value.
     */
    @JsonProperty("value")
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
    @JsonProperty("unit")
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
