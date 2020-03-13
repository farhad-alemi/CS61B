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

        String[] rotors1List = {"B", "Beta", "III", "IV", "I"};
        Permutation plugboard1 = new Permutation("(YF) (ZH)",
                new Alphabet(alphabet));
        Machine machine1 = new Machine(new Alphabet(alphabet), 5, 3, allRotors);
        machine1.setPlugboard(plugboard1);
        machine1.insertRotors(rotors1List);
        machine1.setRotors("AXLE");
        String input1 = "FHELLOMYNAMEISFARHADHELLOMYNAMEISF"
                + "ARHADHELLOMYNAMEISFARHADHELLOMYNAMEISFARHADHELLOMYNAMEISFAR"
                + "HADHELLOMYNAMEISFARHADHELLOMYNAMEISFARHADLLOMYNAMEISFARHAD";
        String simulatorOutput1 = "AJDGZPYWJTVCYMUFMOYVVNYVQYFZJKVRJZTAOBLNNSCD"
                + "ZIKLAIRNMZASWQFFXCNFOMFURJYNNUMVNQFDMYEKZOHCAXKPIOHAUZQZUBJ"
                + "MPPSKROFMISPLOIEMDMATUQKPLYUGOVYOVTDEVBMQYGDZVNG";
        String machineOutput1 = machine1.convert(input1);
        assertTrue(simulatorOutput1.equals(machineOutput1));

        Permutation plugboard2 = new Permutation("(HQ) (EX) (IP) (TR) (BY)",
                new Alphabet(alphabet));
        Machine machine2 = new Machine(new Alphabet(alphabet), 5, 3, allRotors);
        machine2.setPlugboard(plugboard2);
        machine2.insertRotors(rotors1List);
        machine2.setRotors("AXLE");
        String input2 = "FROMHISSHOULDERHIAWATHATOOKTHECAMERAOFROSEWOODMADEOF"
                + "SLIDINGFOLDINGROSEWOODNEATLYPUTITALLTOGETHERINITSCASEITLAYCO"
                + "MPACTLYFOLDEDINTONEARLYNOTHINGBUTHEOPENEDOUTTHEHINGESPUSHEDA"
                + "NDPULLEDTHEJOINTSANDHINGESTILLITLOOKEDALLSQUARESANDOBLONGSLI"
                + "KEACOMPLICATEDFIGUREINTHESECONDBOOKOFEUCLID";
        String simulatorOutput2 = "QVPQSOKOILPUBKJZPISFXDWBHCNSCXNUOAATZXSRCF"
                + "YDGUFLPNXGXIXTYJUJRCAUGEUNCFMKUFWJFGKCIIRGXODJGVCGPQOHALWEBU"
                + "HTZMOXIIVXUEFPRPRKCGVPFPYKIKITLBURVGTSFUSMBNKFRIIMPDOFJVTT"
                + "UGRZMUVCYLFDZPGIBXREWXUEBZQJOYMHIPGRREGOHETUXDTWLCMMWAVNV"
                + "JVHOUFANTQACKKTOZZRDABQNNVPOIEFQAFSVVICVUDUEREYNPFFMNBJVGQ";

        String machineOutput2 = machine2.convert(input2);
        assertTrue(simulatorOutput2.equals(machineOutput2));
    }
}
