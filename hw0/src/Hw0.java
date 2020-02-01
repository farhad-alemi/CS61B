import static org.junit.Assert.*;

public class Hw0 {
    /** Returns the max element in an array. */
    public static int max(int[] a) {
        int maxVal = -1000;
        if (a == null) {
            return maxVal;
        }
        else {
            maxVal = a[0];
            for (int i = 1; i < a.length; ++i) {
                if (a[i] > maxVal) {
                    maxVal = a[i];
                }
            }
            return maxVal;
        }
    }

    /** Returns true if three array elements add up to 0 */
    public static boolean threeSum(int[] a) {
        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    if (a[i] + a[j] + a[k] == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Returns true if three distinct array elements add up to 0 */
    public static boolean threeSumDistinct(int[] a) {
        for (int i = 0; i < a.length; ++i) {
            for (int j = 0; j < a.length; ++j) {
                for (int k = 0; k < a.length; ++k) {
                    if ((i != j && i != k && j != k) && (a[i] + a[j] + a[k] == 0)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static void main(String[] args) {
        /** testing max */
        assertEquals(10, max(new int[] { 10, -52, 3, 0}));

        /** testing threeSum */
        assertTrue(threeSum(new int[]{-6, 2, 4}));
        assertFalse(threeSum(new int[]{-6, 2, 5}));
        assertTrue(threeSum(new int[]{-6, 3, 10, 200}));

        /** testing threeSumDistinct */
        assertTrue(threeSumDistinct(new int[]{-6, 2, 4}));
        assertFalse(threeSumDistinct(new int[]{-6, 2, 5}));
        assertFalse(threeSumDistinct(new int[]{-6, 3, 10, 200}));
    }
}
