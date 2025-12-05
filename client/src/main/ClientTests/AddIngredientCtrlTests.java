import client.scenes.AddIngredientCtrl;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddIngredientCtrlTests {
    /**
     * tests weather the function returns false if given good string
     */
    @Test
    public void isStringBigDecimalTestTrue() {
        String string1 = "12412";
        assertTrue(AddIngredientCtrl.isStringBigDecimal(string1));
    }

    /**
     * tests weather the function returns false if given bad string
     */
    @Test
    public void isStringBigDecimalTestFalse() {
        String string1 = "hehe 12412";
        assertFalse(AddIngredientCtrl.isStringBigDecimal(string1));
    }

}