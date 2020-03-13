package enigma;

import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

import static enigma.TestUtils.UPPER_STRING;

/** The suite of all JUnit tests for Machine class.
 * @author Farhad Alemi
 */
public class MachineTest {
    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TEST UTILITIES ***** */

    static String alphabet = UPPER_STRING;

    static Permutation plugboard = new Permutation("(YF) (ZH)",
            new Alphabet(alphabet));
    static Permutation i = new Permutation("(AELTPHQXRU) (BKNW)"
            + " (CMOY) (DFG) (IV) (JZ) (S)", new Alphabet(alphabet));
    static Permutation ii = new Permutation("(FIXVYOMW) (CDKLHUP)"
            + " (ESZ) (BJ) (GR) (NT) (A) (Q)", new Alphabet(alphabet));
    static Permutation iii = new Permutation("(ABDHPEJT) "
            + "(CFLVMZOYQIRWUKXSG) (N)", new Alphabet(alphabet));
    static Permutation iv = new Permutation("(AEPLIYWCOXMRFZBSTGJQNH)"
            + " (DV) (KU)", new Alphabet(alphabet));
    static Permutation v = new Permutation("(AVOLDRWFIUQ)(BZKSMNHYC) "
            + "(EGTJPX)", new Alphabet(alphabet));
    static Permutation vi = new Permutation("(AJQDVLEOZWIYTS) (CGMNHFUX) "
            + "(BPRK)", new Alphabet(alphabet));
    static Permutation vii = new Permutation("(ANOUPFRIMBZTLWKSVEGCJYDHXQ)",
            new Alphabet(alphabet));
    static Permutation viii = new Permutation("(AFLSETWUNDHOZVICQ) (BKJ) "
            + "(GXY) (MPR)", new Alphabet(alphabet));
    static Permutation beta = new Permutation("(ALBEVFCYODJWUGNMQTZSKPR)"
            + " (HIX)", new Alphabet(alphabet));
    static Permutation gamma = new Permutation("(AFNIRLBSQWVXGUZDKMTPCOYJHE)",
            new Alphabet(alphabet));
    static Permutation b = new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) "
            + "(HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", new Alphabet(alphabet));
    static Permutation c = new Permutation("(AR) (BD) (CO) (EJ) (FN) (GT) "
            + "(HK) (IV) (LM) (PW) (QZ) (SX) (UY)", new Alphabet(alphabet));

    static MovingRotor rotorI = new MovingRotor("I", i, "Q");
    static MovingRotor rotorII = new MovingRotor("II", ii, "E");
    static MovingRotor rotorIII = new MovingRotor("III", iii, "V");
    static MovingRotor rotorIV = new MovingRotor("IV", iv, "J");
    static MovingRotor rotorV = new MovingRotor("V", v, "Z");
    static MovingRotor rotorVI = new MovingRotor("VI", vi, "ZM");
    static MovingRotor rotorVII = new MovingRotor("VII", vii, "ZM");
    static MovingRotor rotorVIII = new MovingRotor("VIII", viii, "ZM");

    static FixedRotor rotorBeta = new FixedRotor("Beta", beta);
    static FixedRotor rotorGamma = new FixedRotor("Gamma", gamma);
    static Reflector rotorB = new Reflector("B", b);
    static Reflector rotorC = new Reflector("C", c);

    public static void main(String[] args) {
        Collection<Rotor> allRotors = new HashSet<>();
        allRotors.add(MachineTest.rotorI);
        allRotors.add(MachineTest.rotorII);
        allRotors.add(MachineTest.rotorIII);
        allRotors.add(MachineTest.rotorIV);
        allRotors.add(MachineTest.rotorV);
        allRotors.add(MachineTest.rotorVI);
        allRotors.add(MachineTest.rotorVII);
        allRotors.add(MachineTest.rotorVIII);

        allRotors.add(MachineTest.rotorBeta);
        allRotors.add(MachineTest.rotorGamma);
        allRotors.add(MachineTest.rotorB);
        allRotors.add(MachineTest.rotorC);

        String[] rotorsList = {"B", "Beta", "III", "IV", "I"};

        Machine machine = new Machine(new Alphabet(alphabet), 5, 3, allRotors);
        machine.setPlugboard(plugboard);
        machine.insertRotors(rotorsList);
        machine.setRotors("AXLE");

        String input = "FHELLOMYNAMEISFARHADHELLOMYNAMEISF"
                + "ARHADHELLOMYNAMEISFARHADHELLOMYNAMEISFARHADHELLOMYNAMEISFAR"
                + "HADHELLOMYNAMEISFARHADHELLOMYNAMEISFARHADLLOMYNAMEISFARHAD";

        String simulatorOutput = "AJDGZPYWJTVCYMUFMOYVVNYVQYFZJKVRJZTAOBLNNSCD"
                + "ZIKLAIRNMZASWQFFXCNFOMFURJYNNUMVNQFDMYEKZOHCAXKPIOHAUZQZUBJ"
                + "MPPSKROFMISPLOIEMDMATUQKPLYUGOVYOVTDEVBMQYGDZVNG";

        String machineOutput = machine.convert(input);

        assertTrue(simulatorOutput.equals(machineOutput));
    }
}
