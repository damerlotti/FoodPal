package commons;

/// Unit enum for Measurement. Each unit knows its dimension and conversion
/// factor to/from the base unit of that dimension (grams for MASS, milliliters for VOLUME).
public enum Unit {
    MG(Dimension.MASS, 0.001),
    G(Dimension.MASS, 1.0),
    KG(Dimension.MASS, 1000.0),
    OZ(Dimension.MASS, 28.349523125),
    LB(Dimension.MASS, 453.59237),

    ML(Dimension.VOLUME, 1.0),
    L(Dimension.VOLUME, 1000.0),
    TSP(Dimension.VOLUME, 4.92892159375),
    TBSP(Dimension.VOLUME, 14.78676478125),
    CUP(Dimension.VOLUME, 240.0),

    PIECE(Dimension.COUNT, 1.0),

    INFORMAL(Dimension.INFORMAL, 1.0);

    public enum Dimension {
        MASS, VOLUME, COUNT, INFORMAL
    }

    private final Dimension dimension;
    private final double conversionFactorToBase;

    Unit(Dimension dimension, double conversionFactorToBase) {
        this.dimension = dimension;
        this.conversionFactorToBase = conversionFactorToBase;
    }

    /**
     * @return The dimension measured by the Unit.
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * @return The multiplier to convert from current Unit to base Unit of same Dimension.
     */
    public double getConversionFactorToBase() {
        return conversionFactorToBase;
    }

    /**
     * @return Whether the unit measures mass.
     */
    public boolean isMass() {
        return this.dimension == Dimension.MASS;
    }

    /**
     * @return Whether the unit measures volume.
     */
    public boolean isVolume() {
        return this.dimension == Dimension.VOLUME;
    }

    /**
     * @return Whether the unit measures a specific count of items.
     */
    public boolean isCount() {
        return this.dimension == Dimension.COUNT;
    }

    /**
     * @return Whether the unit is informal.
     */
    public boolean isInformal() {
        return this.dimension == Dimension.INFORMAL;
    }
}