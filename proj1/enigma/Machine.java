package enigma;

import java.util.Collection;
import java.util.Iterator;

import

import static org.junit.Assert.*;

/** Class that represents a complete enigma machine.
 *  @author Farhad Alemi
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        assertTrue(numRotors > 1);
        assertTrue(pawls >= 0 && pawls < numRotors);
        assertTrue(allRotors.size() > numRotors);

        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number of pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        assertEquals(rotors.length, numRotors());
        _currRotors = new Rotor[rotors.length];

        for (Iterator<Rotor> iter = _allRotors.iterator(); iter.hasNext();) {
            Rotor tempRotor = iter.next();
            for (int i = 0; i < rotors.length; ++i) {
                if (tempRotor.name().equals(rotors[i])) {
                    _currRotors[i] = tempRotor;
                    i = rotors.length;
                }
            }
        }
        if (!_currRotors[0].reflecting()) {
            throw new EnigmaException("Rotor 1 is not Reflector");
        }

        for (int i = 1; i <_currRotors.length; ++i) {
            if (_currRotors[i].rotates() && !_currRotors[i].rotates()) {
                throw new EnigmaException("Fixed rotors must be to the left" +
                        " of non-moving rotors");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        assertEquals(setting.length(), numRotors() - 1);
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
       _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        return 0; // FIXME
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        return ""; // FIXME
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots in the machine.*/
    int _numRotors;

    /** Number of pawls, i.e. moving rotors in the machine. */
    int _pawls;

    /** Rotors in the machine */
    Rotor[] _currRotors;

    /** Set of all available rotors */
    Collection<Rotor> _allRotors;

    /** Plug-board configuration for the machine */
    Permutation _plugboard;
}
