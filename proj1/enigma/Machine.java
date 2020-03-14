package enigma;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

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
        assertTrue(allRotors.size() >= numRotors);

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
        int countMoving = 0;

        for (Iterator<Rotor> iter = _allRotors.iterator(); iter.hasNext();) {
            Rotor tempRotor = iter.next();
            for (int i = 0; i < rotors.length; ++i) {
                if (tempRotor.name().equals(rotors[i])) {
                    _currRotors[i] = tempRotor;
                    countMoving = (tempRotor.rotates()) ? (countMoving + 1)
                            : countMoving;
                    i = rotors.length;
                }
            }
        }

        if (_pawls != countMoving) {
            throw new EnigmaException("Incorrect number of moving rotor");
        }

        if (!_currRotors[0].reflecting()) {
            throw new EnigmaException("Rotor 1 is not Reflector");
        }

        for (int i = 1; i < _currRotors.length; ++i) {
            if (!_currRotors[i].rotates() && _currRotors[i - 1].rotates()) {
                throw new EnigmaException("Moving rotors must be to the left"
                        + " of non-moving rotors");
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        setRotors(setting, "AAAA");
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).
     *  ALPHASETTING is used to adjust rotor alphabet position. */
    void setRotors(String setting, String alphaSetting) {
        assertEquals(setting.length(), _currRotors.length - 1);
        for (int i = 0; i < setting.length(); ++i) {
            if (alphaSetting.charAt(i) != 'A') {
                String prevChars = _currRotors[i + 1].alphabet().getAlphabet();
                String tempStr = prevChars.substring(prevChars.indexOf
                        (alphaSetting.charAt(i)));
                tempStr += prevChars.substring(0, prevChars.length()
                        - tempStr.length());
                _currRotors[i + 1].setPermutation(new Permutation(_currRotors
                        [i + 1].permutation().getCycles(),
                        new Alphabet(tempStr)));
            }
            _currRotors[i + 1].set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        assertEquals(plugboard.alphabet().size(), _alphabet.size());

        for (int i = 0; i < _alphabet.size(); ++i) {
            if (plugboard.alphabet().toChar(i) != _alphabet.toChar(i)) {
                throw new EnigmaException("Letter not in machine alphabet");
            }
        }
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        if (c < 0 || c >= _alphabet.size()) {
            throw new EnigmaException("Character not in alphabet");
        }
        int permuteChar = _plugboard.permute(c);
        Vector<String> rotorsTurned = new Vector<>();

        if (_currRotors.length != 2) {
            for (int i = 1; i < numRotors() - 1; ++i) {
                if (i + 1 < numRotors() - 1) {
                    if (_currRotors[i + 1].atNotch()
                            && _currRotors[i].rotates()) {
                        if (!rotorsTurned.contains(_currRotors[i].name())) {
                            rotorsTurned.add(_currRotors[i].name());
                            _currRotors[i].advance();
                        }
                        if (!rotorsTurned.contains(_currRotors[i + 1]
                                .name())) {
                            rotorsTurned.add(_currRotors[i + 1].name());
                            _currRotors[i + 1].advance();
                        }
                    }
                } else {
                    if (_currRotors[i + 1].atNotch()
                            && _currRotors[i].rotates()) {
                        if (!rotorsTurned.contains(_currRotors[i].name())) {
                            rotorsTurned.add(_currRotors[i].name());
                            _currRotors[i].advance();
                        }
                    }
                    if (!rotorsTurned.contains(_currRotors[i + 1].name())) {
                        rotorsTurned.add(_currRotors[i + 1].name());
                        _currRotors[i + 1].advance();
                    }
                }
            }
        } else {
            _currRotors[1].advance();
        }

        for (int i = numRotors() - 1; i > 0; --i) {
            if (!_currRotors[i].alphabet().getAlphabet().equals(_alphabet
                    .getAlphabet())) {
                permuteChar = _currRotors[i].alphabet().toInt(_alphabet
                        .toChar(permuteChar));
            }
            permuteChar = _currRotors[i].convertForward(permuteChar);
        }

        for (int i = 0; i < numRotors(); ++i) {
            permuteChar = _currRotors[i].convertBackward(permuteChar);
        }
        return _plugboard.permute(permuteChar);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String result = "";

        for (int i = 0; i < msg.length(); ++i) {
            char c = msg.charAt(i);
            if (c == ' ' || c == '\t') {
                continue;
            }
            result += _alphabet.toChar(convert(_alphabet.toInt(c)));
        }
        return result;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotor slots in the machine.*/
    private final int _numRotors;

    /** Number of pawls, i.e. moving rotors in the machine. */
    private final int _pawls;

    /** Rotors in the machine. */
    private Rotor[] _currRotors;

    /** Set of all available rotors. */
    private final Collection<Rotor> _allRotors;

    /** Plug-board configuration for the machine. */
    private Permutation _plugboard;
}
