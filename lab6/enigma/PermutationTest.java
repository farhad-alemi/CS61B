package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/**
 * The suite of all JUnit tests for the Permutation class. For the purposes of
 * this lab (in order to test) this is an abstract class, but in proj1, it will
 * be a concrete class. If you want to copy your tests for proj1, you can make
 * this class concrete by removing the 4 abstract keywords and implementing the
 * 3 abstract methods.
 *
 *  @author Farhad Alemi
 */
public abstract class PermutationTest {

    /**
     * For this lab, you must use this to get a new Permutation,
     * the equivalent to:
     * new Permutation(cycles, alphabet)
     * @return a Permutation with cycles as its cycles and alphabet as
     * its alphabet
     * @see Permutation for description of the Permutation conctructor
     */
    abstract Permutation getNewPermutation(String cycles, Alphabet alphabet);

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet(chars)
     * @return an Alphabet with chars as its characters
     * @see Alphabet for description of the Alphabet constructor
     */
    abstract Alphabet getNewAlphabet(String chars);

    /**
     * For this lab, you must use this to get a new Alphabet,
     * the equivalent to:
     * new Alphabet()
     * @return a default Alphabet with characters ABCD...Z
     * @see Alphabet for description of the Alphabet constructor
     */
    abstract Alphabet getNewAlphabet();

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /** Check that PERM has an ALPHABET whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha,
                           Permutation perm, Alphabet alpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.toInt(c), ei = alpha.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        Alphabet alpha = getNewAlphabet();
        Permutation perm = getNewPermutation("", alpha);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, perm, alpha);
    }

    @Test
    public void testInvertChar() {
        Permutation p = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('A', p.invert('C'));
        assertEquals('C', p.invert('D'));
        assertEquals('D', p.invert('B'));
    }

    @Test
    public void testSize() {
        Permutation p1 = getNewPermutation("(BACD)", getNewAlphabet("ABCD"));
        //Permutation p2 = getNewPermutation("(B)", getNewAlphabet("B"));
        //Permutation p3 = getNewPermutation("", getNewAlphabet(""));

        assertEquals(p1.size(), 4);
        //assertEquals(p2.size(), 1);
        //assertEquals(p3.size(), 0);
    }

    @Test
    public void testPermuteChar() {
        Permutation p1 = getNewPermutation("(ACBD)", getNewAlphabet("ABCD"));
        //Permutation p2 = getNewPermutation("(B)", getNewAlphabet("ABCD"));

        assertEquals('C', p1.permute('A'));
        assertEquals('B', p1.permute('C'));
        assertEquals('D', p1.permute('B'));
        assertEquals('A', p1.permute('D'));

        //assertEquals('B', p2.permute('B'));
    }

    @Test
    public void testAlphabet() {
        Permutation p1 = getNewPermutation("(ACBD)", getNewAlphabet("ABCD"));

        assertEquals(p1.alphabet().toChar(0), 'A');
        assertEquals(p1.alphabet().toChar(1), 'B');
        assertEquals(p1.alphabet().toChar(2), 'C');
        assertEquals(p1.alphabet().toChar(3), 'D');
    }

    @Test
    public void testPermuteInt() {
        Permutation p1 = getNewPermutation("(ACBD)", getNewAlphabet("ABCD"));
        //Permutation p2 = getNewPermutation("(B)", getNewAlphabet("ABCD"));

        //assertEquals()
    }
}
