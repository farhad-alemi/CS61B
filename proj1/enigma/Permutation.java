package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Farhad Alemi
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        cycle = "(" + cycle + ")";
        _cycles = (_cycles.length() == 0) ? cycle : _cycles + " " + cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char permuteChar = permute(alphabet().toChar(p));
        return alphabet().toInt(permuteChar);
    }

    /** Return the result of applying the inverse of this permutation
     *  to C modulo the alphabet size. */
    int invert(int c) {
        char invertChar = invert(alphabet().toChar(wrap(c)));
        return alphabet().toInt(invertChar);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int indexOfP = _cycles.indexOf(p), permIndex;
        if (indexOfP == -1) {
            return p;
        } else if (!alphabet().contains(p)) {
            throw new EnigmaException("Letter not in alphabet");
        }

        if (_cycles.charAt(indexOfP + 1) == ')') {
            permIndex = _cycles.lastIndexOf('(', indexOfP) + 1;
        } else {
            permIndex = indexOfP + 1;
        }
        return _cycles.charAt(permIndex);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int indexOfC = _cycles.indexOf(c), inverseIndex;
        if (indexOfC == -1 || !alphabet().contains(c)) {
            return c;
        }
        if (_cycles.charAt(indexOfC - 1) == '(') {
            inverseIndex = _cycles.indexOf(')', indexOfC) - 1;
        } else {
            inverseIndex = indexOfC - 1;
        }
        return _cycles.charAt(inverseIndex);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        if (_cycles.length() == 0) {
            return false;
        }
        String[] groupedCycles = _cycles.split("\\)");
        for (String cycle : groupedCycles) {
            if (cycle.trim().length() <= 2) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String _cycles;
}
