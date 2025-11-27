package commons;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MeasurementTest {
    @Test
    void testMassConversionFromGramToAllMassUnits() {
        // 1000 g → various mass units
        Measurement grams = new Measurement(new BigDecimal("1000.0"), Unit.G);

        Measurement mg = grams.convert(Unit.MG);
        Measurement kg = grams.convert(Unit.KG);
        Measurement oz = grams.convert(Unit.OZ);
        Measurement lb = grams.convert(Unit.LB);

        assertEquals(new BigDecimal("1000000.00000000"), mg.getValue());   // mg
        assertEquals(new BigDecimal("1.00000000"),       kg.getValue());   // kg
        assertEquals(new BigDecimal("35.27396195"),      oz.getValue());   // oz
        assertEquals(new BigDecimal("2.20462262"),       lb.getValue());   // lb

        assertEquals(Unit.MG, mg.getUnit());
        assertEquals(Unit.KG, kg.getUnit());
        assertEquals(Unit.OZ, oz.getUnit());
        assertEquals(Unit.LB, lb.getUnit());
    }

    @Test
    void testVolumeConversionFromMilliliterToAllVolumeUnits() {
        // 1000 ml → various volume units
        Measurement ml = new Measurement(new BigDecimal("1000.0"), Unit.ML);

        Measurement l    = ml.convert(Unit.L);
        Measurement tsp  = ml.convert(Unit.TSP);
        Measurement tbsp = ml.convert(Unit.TBSP);
        Measurement cup  = ml.convert(Unit.CUP);

        assertEquals(new BigDecimal("1.00000000"),       l.getValue());       // L
        assertEquals(new BigDecimal("202.88413621"),     tsp.getValue());     // tsp
        assertEquals(new BigDecimal("67.62804540"),      tbsp.getValue());    // tbsp
        assertEquals(new BigDecimal("4.16666667"),       cup.getValue());     // cup

        assertEquals(Unit.L,    l.getUnit());
        assertEquals(Unit.TSP,  tsp.getUnit());
        assertEquals(Unit.TBSP, tbsp.getUnit());
        assertEquals(Unit.CUP,  cup.getUnit());
    }

    @Test
    void testConvertToSameUnitIsNoOp() {
        Measurement original = new Measurement(new BigDecimal("123.45600000"), Unit.ML);

        Measurement converted = original.convert(Unit.ML);

        assertEquals(original.getValue(), converted.getValue());
        assertEquals(original.getUnit(),  converted.getUnit());
    }

    @Test
    void testConvertDifferentDimensionsThrows() {
        Measurement grams = new Measurement(new BigDecimal("1000.0"), Unit.G);

        assertThrows(IllegalArgumentException.class, () -> grams.convert(Unit.ML));
    }

    // TODO(calco): Should probably be testing `toString` functionality and display, but meh
}
