package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static enigma.TestUtils.UPPER_STRING;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for Rotor class.
 * @author Farhad Alemi
 */
public class RotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TEST UTILITIES ***** */

    private String alphabet = UPPER_STRING;
    private Permutation p = new Permutation("(AELTPHQXRU) (BKNW) (CMOY)" +
            " (DFG) (IV) (JZ) (S)", new Alphabet(alphabet));
    private Rotor rotorI = new Rotor("I", p);

    @Test
    public void testName() {
        assertEquals("I", rotorI.name());
    }

    @Test
    public void testSet() {
        assertEquals(0, rotorI.setting());

        rotorI.set(5);
        assertEquals(5, rotorI.setting());

        rotorI.set(0);
        assertEquals(0, rotorI.setting());

        rotorI.set(rotorI.alphabet().toChar(0));
        assertEquals(0, rotorI.setting());

        rotorI.set(rotorI.alphabet().toChar(12));
        assertEquals(12, rotorI.setting());

        rotorI.set(rotorI.alphabet().toChar(rotorI.size() - 1));
        assertEquals(rotorI.size() - 1, rotorI.setting());
    }

    @Test
    public void convertForward() {
        rotorI.set(5);
        int perm = rotorI.convertForward(rotorI.alphabet().toInt('F'));
        assertEquals(rotorI.alphabet().toInt('I'), perm);

        /*rotorI.set(0);
        String fromAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String toAlpha = "JLEKFCPUYMSNVXGWTROZHAQBID";

        for (int i = 0; i < fromAlpha.length(); ++i) {
            System.out.println(fromAlpha.charAt(i));
            assertEquals(fromAlpha.charAt(rotorI.convertForward(fromAlpha.charAt(i))), toAlpha.charAt(i));
        }*/
    }

    @Test
    public void convertBackward() {
        rotorI.set(5);
        int perm = rotorI.convertBackward(rotorI.alphabet().toInt('I'));
        assertEquals(rotorI.alphabet().toInt('F'), perm);
    }
}
