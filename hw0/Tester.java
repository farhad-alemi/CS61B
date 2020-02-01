import org.junit.Test;
import static org.junit.Assert.*;

import ucb.junit.textui;

/** Tests for Hw0. 
 *  @author Farhad Alemi
 */
public class Tester {

    /* Feel free to add your own tests.  For now, you can just follow
     * the pattern you see here.  We'll look into the details of JUnit
     * testing later.
     *
     * To actually run the tests, just use
     *      java Tester 
     * (after first compiling your files).
     *
     * DON'T put your HW0 solutions here!  Put them in a separate
     * class and figure out how to call them from here.  You'll have
     * to modify the calls to max, threeSum, and threeSumDistinct to
     * get them to work, but it's all good practice! */

    @Test
    public void maxTest() {
        assertEquals(14, Hw0.max(new int[] { 0, -5, 2, 14, 10 }));
        assertEquals(0, Hw0.max(new int[] { 0, -5, -2, -14, -10 }));
        assertEquals(0, Hw0.max(new int[] { 0 }));
    }

    @Test
    public void threeSumTest() {
        assertTrue(Hw0.threeSum(new int[] { -6, 3, 10, 200 }));
        assertTrue(Hw0.threeSum(new int[] { 8, 2, -1, 15 }));
        assertTrue(Hw0.threeSum(new int[] { 8, 2, -1, -1, 15 }));
        assertTrue(Hw0.threeSum(new int[] { 5, 1, 0, 3, 6 }));
    }

    @Test
    public void threeSumDistinctTest() {
        assertFalse(Hw0.threeSumDistinct(new int[] { -6, 3, 10, 200 }));
        assertFalse(Hw0.threeSumDistinct(new int[] { 8, 2, -1, 15 }));
        assertTrue(Hw0.threeSumDistinct(new int[] { 8, 2, -1, -1, 15 }));
        assertFalse(Hw0.threeSumDistinct(new int[] { 5, 1, 0, 3, 6 }));
    }

    public static void main(String[] unused) {
        textui.runClasses(Tester.class);
    }
}
