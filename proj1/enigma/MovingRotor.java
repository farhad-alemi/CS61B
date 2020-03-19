package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Farhad Alemi
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    @Override
    void advance() {
        _setting = (_setting + 1) % size();
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        char currLetter = permutation().alphabet().toChar(setting());
        for (int i = 0; i < _notches.length(); ++i) {
            if (_notches.charAt(i) == currLetter) {
                return true;
            }
        }
        return false;
    }

    /** Returns the notch. */
    String getNotches() {
        return _notches;
    }

    //fixme
    /** Updates the notches using alphaSetting parameter. */
    void setNotches(char charSetting) {
        String newNotches = "";
        for (int i = 0; i < _notches.length(); ++i) {
            newNotches += alphabet().toChar((alphabet().size() + alphabet().toInt(charSetting) + alphabet().toInt(_notches.charAt(i))) % alphabet().size());
                       //(_alphabet.size() - _alphabet.toInt(alphaSetting.charAt(i)) + _currRotors[i + 1].setting()) % _alphabet.size()
        }
        _notches = newNotches;
    }

    /** Rotor notches. */
    private String _notches;
}
