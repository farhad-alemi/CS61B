package enigma;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static enigma.TestUtils.UPPER_STRING;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for Rotor class.
 * @author Farhad Alemi
 *
 */
public class RotorTest {

    /** Testingtime limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TEST UTILITIES ***** */

    private String alphabet = UPPER_STRING;
    private Permutation p = new Permutation("(AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)",
            new Alphabet(alphabet));
    private Rotor rotor = new Rotor("VIII", p);

    @Test
    public void testName() {
        assertEquals("VIII", rotor.name());
    }

    @Test
    public void testSet() {
        assertEquals(0, rotor.setting());

        rotor.set(5);
        assertEquals(5, rotor.setting());

        rotor.set(0);
        assertEquals(0, rotor.setting());

        rotor.set('A');
        assertEquals(0, rotor.setting());

        rotor.set('M');
        assertEquals(12, rotor.setting());
    }
}
