package client.utils.search;

import java.util.Objects;

public class NutritionFilter {
    private final NutritionField field;
    private final ComparisonOperator operator;
    private final int value;

    /**
     * Creates a new nutrition filter.
     * @param field    The nutrition field to filter on
     * @param operator The comparison operator
     * @param value    The value to compare against
     */
    public NutritionFilter(NutritionField field, ComparisonOperator operator, int value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Returns the nutrition field.
     * @return the nutrition field
     */
    public NutritionField getField() {
        return field;
    }

    /**
     * Returns the comparison operator.
     * @return the comparison operator
     */
    public ComparisonOperator getOperator() {
        return operator;
    }

    /**
     * Returns the value to compare against.
     * @return the value
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if the given actual value matches this filter.
     * @param actualValue the actual nutrition value
     * @return true if the value matches the filter
     */
    public boolean matches(int actualValue) {
        switch (operator) {
            case LESS_THAN:
                return actualValue < value;
            case EQUAL:
                return actualValue == value;
            case GREATER_THAN:
                return actualValue > value;
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NutritionFilter that = (NutritionFilter) o;
        return value == that.value && field == that.field && operator == that.operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, operator, value);
    }

    @Override
    public String toString() {
        return "NutritionFilter{"
                + "field=" + field
                + ", operator=" + operator
                + ", value=" + value
                + '}';
    }
}
