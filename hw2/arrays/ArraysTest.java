package arrays;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests the Arrays class
 *
 *  @author Farhad Alemi
 */
public class ArraysTest {

    /**
     * Tests the Arrays.catenate method
     */
    @Test
    public void catenateTest() {
        int[] test1A = {1, 2, 3, 3};
        int[] test1B = {3, 2, 3, 3, 3, 1};
        int[] test1C = {1, 2, 3, 3, 3, 2, 3, 3, 3, 1};
        assertTrue(Utils.equals(Arrays.catenate(test1A, test1B), test1C));

        int[] test2A = {3};
        int[] test2B = {};
        int[] test2C = {3};
        assertTrue(Utils.equals(Arrays.catenate(test2A, test2B), test2C));

        int[] test3A = {};
        int[] test3B = {};
        int[] test3C = {};
        assertTrue(Utils.equals(Arrays.catenate(test3A, test3B), test3C));
    }

    /**
     * Tests the Arrays.remove method
     */
    @Test
    public void removeTest() {
        int[] test1A = {1, 1, 3, 3};
        int[] test1B = {1, 1, 3, 3};
        assertTrue(Utils.equals(Arrays.remove(test1A,0, 0), test1B));

        int[] test2A = {3};
        int[] test2B = {};
        assertTrue(Utils.equals(Arrays.remove(test2A, 0, 1), test2B));

        int[] test3A = {};
        int[] test3B = {};
        assertTrue(Utils.equals(Arrays.remove(test3A, 0, 0), test3B));

        int[] test4A = {1, 2, 3, 3};
        int[] test4B = {1, 2};
        assertTrue(Utils.equals(Arrays.remove(test4A, 2, 2), test4B));

        int[] test5A = {1, 2, 3, 4, 5, 6, 7, 8};
        int[] test5B = {1, 2, 7, 8};
        assertTrue(Utils.equals(Arrays.remove(test5A, 2, 4), test5B));

    }

    /**
     * Tests the Arrays.naturalRuns method
     */
    @Test
    public void naturalRunsTest() {
        int[] test1A = {1, 3, 7, 5, 4, 6, 9, 10, 10, 11};
        int[][] test1B = Arrays.naturalRuns(test1A);
        int[][] test1C = {{1, 3, 7}, {5}, {4, 6, 9, 10}, {10, 11}};
        assertTrue(Utils.equals(test1B, test1C));

        int[] test2A = {9, 8, 7, 5, 4, 1, 3, 3, 3};
        int[][] test2B = Arrays.naturalRuns(test2A);
        int[][] test2C = {{9}, {8}, {7}, {5}, {4}, {1, 3}, {3}, {3}};
        assertTrue(Utils.equals(test2B, test2C));

        int[] test3A = {1, 3, 4, 6, 9, 10, 11};
        int[][] test3B = Arrays.naturalRuns(test3A);
        int[][] test3C = {{1, 3, 4, 6, 9, 10, 11}};
        assertTrue(Utils.equals(test3B, test3C));

        int[] test4A = {1};
        int[][] test4B = Arrays.naturalRuns(test4A);
        int[][] test4C = {{1}};
        assertTrue(Utils.equals(test4C, test4C));

        int[] test5A = {};
        int[][] test5B = Arrays.naturalRuns(test5A);
        int[][] test5C = {};
        assertTrue(Utils.equals(test5B, test5C));
    }
    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ArraysTest.class));
    }
}
