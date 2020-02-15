package lists;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Performs test of Lists class
 *
 *  @author Farhad Alemi
 */
public class ListsTest {

    /**
     * Tests naturalRuns
     */
    @Test
    public void naturalRunsTest() {
        int[] test1D1 = {1, 3, 7, 5, 4, 6, 9, 10, 10, 11};
        IntListList test1A = Lists.naturalRuns(Utils.toList(test1D1));
        int[][] test1D2 = {{1, 3, 7}, {5}, {4, 6, 9, 10}, {10, 11}};
        IntListList test1B = Utils.toList(test1D2);
        assertTrue(Utils.equals(test1A, test1B));

        int[] test2D1 = {9, 8, 7, 5, 4, 1, 3, 3, 3};
        IntListList test2A = Lists.naturalRuns(Utils.toList(test2D1));
        int[][] test2D2 = {{9}, {8}, {7}, {5}, {4}, {1, 3}, {3}, {3}};
        IntListList test2B = Utils.toList(test2D2);
        assertTrue(Utils.equals(test2A, test2B));

        int[] test3D1 = {1, 3, 4, 6, 9, 10, 11};
        IntListList test3A = Lists.naturalRuns(Utils.toList(test3D1));
        int[][] test3D2 = {{1, 3, 4, 6, 9, 10, 11}};
        IntListList test3B = Utils.toList(test3D2);
        assertTrue(Utils.equals(test3A, test3B));

        int[] test4D1 = {1};
        IntListList test4A = Lists.naturalRuns(Utils.toList(test4D1));
        int[][] test4D2 = {{1}};
        IntListList test4B = Utils.toList(test4D2);
        assertTrue(Utils.equals(test4A, test4B));

        int[] test5D1 = {};
        IntListList test5A = Lists.naturalRuns(Utils.toList(test5D1));
        int[][] test5D2 = {};
        IntListList test5B = Utils.toList(test5D2);
        assertTrue(Utils.equals(test5A, test5B));
    }

    public static void main(String[] args) {
        System.exit(ucb.junit.textui.runClasses(ListsTest.class));
    }
}
