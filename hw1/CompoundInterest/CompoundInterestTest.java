import static org.junit.Assert.*;
import org.junit.Test;

public class CompoundInterestTest {

    @Test
    public void testNumYears() {
        assertEquals(CompoundInterest.numYears(2020), 0);
        assertEquals(CompoundInterest.numYears(2030), 10);
    }

    @Test
    public void testFutureValue() {
        double tolerance = 0.01;
        assertTrue(Math.abs(CompoundInterest.futureValue(10, 12, 2022) - 12.544)
                < tolerance);
        assertTrue(Math.abs(CompoundInterest.futureValue(0, 12, 2100) - 0.0)
                < tolerance);
        assertTrue(Math.abs(CompoundInterest.futureValue(100, -12, 2100)
                - 0.0036) < tolerance);
    }

    @Test
    public void testFutureValueReal() {
        double tolerance = 0.01;
        assertTrue(Math.abs(CompoundInterest.futureValueReal(
                10, 12, 2022, 3) - 11.8026496)
                < tolerance);
        assertTrue(Math.abs(CompoundInterest.futureValueReal(
                0, 12, 2100, 3) - 0.0)
                < tolerance);
        assertTrue(Math.abs(CompoundInterest.futureValueReal(
                1000000, -12, 2100, 1) - 16.19680449)
                < tolerance);
    }


    @Test
    public void testTotalSavings() {
        double tolerance = 0.01;
        assertTrue(Math.abs(CompoundInterest.totalSavings(5000, 2022, 10)
                - 16550) < tolerance);
    }

    @Test
    public void testTotalSavingsReal() {
        double tolerance = 0.01;
        assertTrue(Math.abs(CompoundInterest.totalSavingsReal(5000, 2022, 10,
                3) - 15571.895) < tolerance);
    }


    /* Run the unit tests in this file. */
    public static void main(String... args) {
        //System.exit(ucb.junit.textui.runClasses(CompoundInterestTest.class));
    }
}
