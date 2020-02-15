package image;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests the MatrixUtils class
 *  @author Farhad Alemi
 */

public class MatrixUtilsTest {

    /**
     * Tests MaxUtils.accumulateVertical
     */
    @Test
    public void accumulateVerticalTest() {
        double[][] test1A = {{1000000, 1000000, 1000000, 1000000},
                {1000000, 75990, 30003, 1000000},
                {1000000, 30002, 103046, 1000000},
                {1000000, 29515, 38273, 1000000},
                {1000000, 73403, 35399, 1000000},
                {1000000, 1000000, 1000000, 1000000}};

        double[][] test1B = {{1000000, 1000000, 1000000, 1000000},
                {2000000, 1075990, 1030003, 2000000},
                {2075990, 1060005, 1133049, 2030003},
                {2060005, 1089520, 1098278, 2133049},
                {2089520, 1162923, 1124919, 2098278},
                {2162923, 2124919, 2124919, 2124919}};

        double[][] test1C = MatrixUtils.accumulateVertical(test1A);
        assertTrue(Arrays.deepEquals(test1B, test1C));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(MatrixUtilsTest.class));
    }
}
