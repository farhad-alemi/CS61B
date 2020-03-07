package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        Permutation p1 = new Permutation("(!@) (12)", new Alphabet("1!@2"));

        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('C', p.invert('D'));
        assertEquals('D', p.invert('B'));

        assertEquals('2', p1.invert('1'));
        assertEquals('@', p1.invert('!'));
        assertEquals('!', p1.invert('@'));
        assertEquals('1', p1.invert('2'));
    }

    @Test
    public void testSize() {
        Permutation p1 = new Permutation("(BACD)", new Alphabet("ABCD"));
        Permutation p2 = new Permutation("(B)", new Alphabet("B"));
        Permutation p3 = new Permutation("", new Alphabet(""));
        Permutation p4 = new Permutation("(!@) (12)", new Alphabet("1!@2"));

        assertEquals(p1.size(), 4);
        assertEquals(p2.size(), 1);
        assertEquals(p3.size(), 0);
        assertEquals(p4.size(), 4);
    }

    @Test
    public void testPermuteChar() {
        Permutation p1 = new Permutation("(ACBD)", new Alphabet("ABCD"));
        Permutation p2 = new Permutation("(B)", new Alphabet("ABCD"));
        Permutation p3 = new Permutation("(!@) (12)", new Alphabet("1!@2"));

        assertEquals('C', p1.permute('A'));
        assertEquals('B', p1.permute('C'));
        assertEquals('D', p1.permute('B'));
        assertEquals('A', p1.permute('D'));

        assertEquals('B', p2.permute('B'));

        assertEquals('2', p3.permute('1'));
        assertEquals('@', p3.permute('!'));
        assertEquals('!', p3.permute('@'));
        assertEquals('1', p3.permute('2'));

    }

    @Test
    public void testAlphabet() {
        Permutation p1 = new Permutation("(ACBD)", new Alphabet("ABCD"));
        Permutation p2 = new Permutation("(!@) (12)", new Alphabet("1!@2"));

        assertEquals(p1.alphabet().toChar(0), 'A');
        assertEquals(p1.alphabet().toChar(1), 'B');
        assertEquals(p1.alphabet().toChar(2), 'C');
        assertEquals(p1.alphabet().toChar(3), 'D');

        assertEquals(p2.alphabet().toChar(0), '1');
        assertEquals(p2.alphabet().toChar(1), '!');
        assertEquals(p2.alphabet().toChar(2), '@');
        assertEquals(p2.alphabet().toChar(3), '2');
    }

    @Test
    public void testPermuteInt() {
        Permutation p1 = new Permutation("(ACBD)", new Alphabet("ABCD"));
        Permutation p2 = new Permutation("(B)", new Alphabet("ABCD"));
        Permutation p3 = new Permutation("(!@) (12)", new Alphabet("1!@2"));

        assertEquals(2, p1.permute(0));
        assertEquals(3, p1.permute(1));
        assertEquals(1, p1.permute(2));
        assertEquals(0, p1.permute(3));

        assertEquals(1, p2.permute(1));

        assertEquals(3, p3.permute(0));
        assertEquals(2, p3.permute(1));
        assertEquals(1, p3.permute(2));
        assertEquals(0, p3.permute(3));
    }

    @Test
    public void testInverseInt() {
        Permutation p1 = new Permutation("(ACBDEF)", new Alphabet("ABCDEF"));
        Permutation p2 = new Permutation("(BA)", new Alphabet("ABCD"));
        Permutation p3 = new Permutation("(!@) (12)", new Alphabet("1!@2"));

        assertEquals(5, p1.invert(0));
        assertEquals(2, p1.invert(1));
        assertEquals(0, p1.invert(2));
        assertEquals(1, p1.invert(3));
        assertEquals(3, p1.invert(4));
        assertEquals(4, p1.invert(5));

        assertEquals(0, p2.invert(1));

        assertEquals(3, p3.invert(0));
        assertEquals(2, p3.invert(1));
        assertEquals(1, p3.invert(2));
        assertEquals(0, p3.invert(3));
    }

    @Test
    public void testDerangement() {
        Permutation p1 = new Permutation("(ACBDEF)", new Alphabet("ABCDEF"));
        Permutation p2 = new Permutation("(BA) (CD)", new Alphabet("ABCD"));
        Permutation p3 = new Permutation("(A) (CBDEF)", new Alphabet("ABCDEF"));
        Permutation p4 = new Permutation("(B)", new Alphabet("ABCD"));
        Permutation p5 = new Permutation("(!@) (12)", new Alphabet("1!@2"));
        Permutation p6 = new Permutation("(!@) (1)", new Alphabet("1!@2"));

        assertTrue(p1.derangement());
        assertTrue(p2.derangement());
        assertFalse(p3.derangement());
        assertFalse(p4.derangement());
        assertTrue(p5.derangement());
        assertFalse(p6.derangement());
    }
}
