package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTest {
    private static final double DELTA = 1e-9;

    @Test
    void testDimensions() {
        // MASS
        assertEquals(Unit.Dimension.MASS, Unit.MG.getDimension());
        assertEquals(Unit.Dimension.MASS, Unit.G.getDimension());
        assertEquals(Unit.Dimension.MASS, Unit.KG.getDimension());
        assertEquals(Unit.Dimension.MASS, Unit.OZ.getDimension());
        assertEquals(Unit.Dimension.MASS, Unit.LB.getDimension());

        // VOLUME
        assertEquals(Unit.Dimension.VOLUME, Unit.ML.getDimension());
        assertEquals(Unit.Dimension.VOLUME, Unit.L.getDimension());
        assertEquals(Unit.Dimension.VOLUME, Unit.TSP.getDimension());
        assertEquals(Unit.Dimension.VOLUME, Unit.TBSP.getDimension());
        assertEquals(Unit.Dimension.VOLUME, Unit.CUP.getDimension());

        // COUNT
        assertEquals(Unit.Dimension.COUNT, Unit.PIECE.getDimension());

        // INFORMAL
        assertEquals(Unit.Dimension.INFORMAL, Unit.INFORMAL.getDimension());
    }

    @Test
    void testConversionFactorsMass() {
        assertEquals(0.001, Unit.MG.getConversionFactorToBase(), DELTA);
        assertEquals(1.0,   Unit.G.getConversionFactorToBase(), DELTA);
        assertEquals(1000.0, Unit.KG.getConversionFactorToBase(), DELTA);
        assertEquals(28.349523125, Unit.OZ.getConversionFactorToBase(), DELTA);
        assertEquals(453.59237, Unit.LB.getConversionFactorToBase(), DELTA);
    }

    @Test
    void testConversionFactorsVolume() {
        assertEquals(1.0, Unit.ML.getConversionFactorToBase(), DELTA);
        assertEquals(1000.0, Unit.L.getConversionFactorToBase(), DELTA);
        assertEquals(4.92892159375, Unit.TSP.getConversionFactorToBase(), DELTA);
        assertEquals(14.78676478125, Unit.TBSP.getConversionFactorToBase(), DELTA);
        assertEquals(240.0, Unit.CUP.getConversionFactorToBase(), DELTA);
    }

    @Test
    void testConversionFactorsOther() {
        assertEquals(1.0, Unit.PIECE.getConversionFactorToBase(), DELTA);
        assertEquals(1.0, Unit.INFORMAL.getConversionFactorToBase(), DELTA);
    }

    @Test
    void testIsMass() {
        // MASS units
        assertTrue(Unit.MG.isMass());
        assertTrue(Unit.G.isMass());
        assertTrue(Unit.KG.isMass());
        assertTrue(Unit.OZ.isMass());
        assertTrue(Unit.LB.isMass());

        // Non-mass units
        assertFalse(Unit.ML.isMass());
        assertFalse(Unit.L.isMass());
        assertFalse(Unit.TSP.isMass());
        assertFalse(Unit.TBSP.isMass());
        assertFalse(Unit.CUP.isMass());
        assertFalse(Unit.PIECE.isMass());
        assertFalse(Unit.INFORMAL.isMass());
    }

    @Test
    void testIsVolume() {
        assertTrue(Unit.ML.isVolume());
        assertTrue(Unit.L.isVolume());
        assertTrue(Unit.TSP.isVolume());
        assertTrue(Unit.TBSP.isVolume());
        assertTrue(Unit.CUP.isVolume());

        assertFalse(Unit.MG.isVolume());
        assertFalse(Unit.G.isVolume());
        assertFalse(Unit.KG.isVolume());
        assertFalse(Unit.OZ.isVolume());
        assertFalse(Unit.LB.isVolume());
        assertFalse(Unit.PIECE.isVolume());
        assertFalse(Unit.INFORMAL.isVolume());
    }

    @Test
    void testIsCount() {
        assertTrue(Unit.PIECE.isCount());

        assertFalse(Unit.MG.isCount());
        assertFalse(Unit.G.isCount());
        assertFalse(Unit.KG.isCount());
        assertFalse(Unit.OZ.isCount());
        assertFalse(Unit.LB.isCount());
        assertFalse(Unit.ML.isCount());
        assertFalse(Unit.L.isCount());
        assertFalse(Unit.TSP.isCount());
        assertFalse(Unit.TBSP.isCount());
        assertFalse(Unit.CUP.isCount());
        assertFalse(Unit.INFORMAL.isCount());
    }

    @Test
    void testIsInformal() {
        assertTrue(Unit.INFORMAL.isInformal());

        assertFalse(Unit.MG.isInformal());
        assertFalse(Unit.G.isInformal());
        assertFalse(Unit.KG.isInformal());
        assertFalse(Unit.OZ.isInformal());
        assertFalse(Unit.LB.isInformal());
        assertFalse(Unit.ML.isInformal());
        assertFalse(Unit.L.isInformal());
        assertFalse(Unit.TSP.isInformal());
        assertFalse(Unit.TBSP.isInformal());
        assertFalse(Unit.CUP.isInformal());
        assertFalse(Unit.PIECE.isInformal());
    }
}
