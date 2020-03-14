package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;
import static org.junit.Assert.*;

/** Enigma simulator.
 *  @author Farhad Alemi
 */
public final class Main {
    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
        rotorNames = new String[]{"I", "II", "III", "IV", "V", "VI", "VII",
                                  "VIII", "Beta", "Gamma", "B", "C"};
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine machine = readConfig();
        parseConfig(_input.nextLine().split("[ ]"), machine);

        while (_input.hasNext() || _input.hasNextLine()) {
            String temp = _input.nextLine(), processed;

            if (!temp.equals("") && temp.charAt(0) == '*') {
                parseConfig(temp.split("[ ]"), machine);
                continue;
            }
            processed = machine.convert(temp);
            printMessageLine(processed);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        int numRotors, pawls;
        Collection<Rotor> allRotors = new HashSet<>();
        try {
            if (_config.hasNext()) {
                String nextToken = _config.next();
                if (nextToken.contains("*") || nextToken.contains("(")
                        || nextToken.contains(")")) {
                    throw new EnigmaException("'*', '(', or ')' in alphabet");
                } else {
                    _alphabet = new Alphabet(nextToken);
                    numRotors = _config.nextInt();
                    pawls = _config.nextInt();
                    assertTrue(numRotors > pawls && pawls >= 0);
                    while (_config.hasNext()) {
                        allRotors.add(readRotor());
                    }
                }
                return new Machine(_alphabet, numRotors, pawls, allRotors);
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        String rotorName, temp;
        try {
            rotorName = _config.next();

            temp = _config.next();
            String perm = _config.nextLine();
            if (_config.hasNext(Pattern.compile("\\(([^\\)])*\\)"))) {
                perm += " " + _config.nextLine();
            }

            if (temp.charAt(0) != 'M' && temp.length() > 1) {
                throw new NoSuchElementException();
            } else if (temp.charAt(0) == 'N') {
                return new FixedRotor(rotorName, new Permutation(perm,
                        _alphabet));
            } else if (temp.charAt(0) == 'R') {
                return new Reflector(rotorName, new Permutation(perm,
                        _alphabet));
            } else if (temp.charAt(0) == 'M') {
                return new MovingRotor(rotorName, new Permutation(
                        perm, _alphabet), temp.substring(1));
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String[] settings) {
        String[] rotors = new String[M.numRotors()];
        String initialSettings = settings[M.numRotors()];

        System.arraycopy(settings, 0, rotors, 0, M.numRotors());

        for (int i = 0; i < rotors.length; ++i) {
            for (int j = i + 1; j < rotors.length; ++j) {
                if (rotors[i].equals(rotors[j])) {
                    throw new EnigmaException("Duplicate Rotors");
                }
            }
        }
        M.insertRotors(rotors);

        if (initialSettings.equals("")) {
            throw new EnigmaException("No initial setting");
        } else if (settings.length == M.numRotors() + 1) {
            M.setPlugboard(new Permutation("", _alphabet));
            M.setRotors(initialSettings);
        } else if (settings.length == M.numRotors() + 2) {
            if (settings[M.numRotors() + 1].charAt(0) == '(') {
                M.setPlugboard(new Permutation(settings[M.numRotors() + 1],
                        _alphabet));
                M.setRotors(initialSettings);
            } else {
                M.setPlugboard(new Permutation("", _alphabet));
                M.setRotors(initialSettings, settings[M.numRotors() + 1]);
            }
        } else if (settings.length == M.numRotors() + 3) {
            M.setPlugboard(new Permutation(settings[M.numRotors() + 2],
                    _alphabet));
            M.setRotors(initialSettings, settings[M.numRotors() + 1]);
        } else {
            throw new EnigmaException("Too many parameters in input settings");
        }
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        StringBuilder parsedMsg = new StringBuilder();

        for (int i = 0; i < msg.length(); ++i) {
            parsedMsg.append(msg.charAt(i));
            if ((i + 1) % 5 == 0) {
                parsedMsg.append(" ");
            }
        }
        _output.println(parsedMsg);
    }

    /** Searches the TOKEN for valid rotor names and returns true if values
     * are valid and false otherwise.
     */
    private boolean containsName(String token) {
        for (String temp : rotorNames) {
            if (temp.equals(token)) {
                return true;
            }
        }
        return false;
    }

    /** Parses the input using CONFIGARR array and calls setUp using M. */
    public void parseConfig(String[] configArr, Machine m) {
        if (!configArr[0].equals("*")) {
            throw new EnigmaException("Bad configuration");
        }
        String cycles = "";
        int cycleIndex = -1;
        for (int i = 1; i < configArr.length; ++i) {
            if (configArr[i].contains("(")) {
                cycleIndex = i;
                for (int j = i; j < configArr.length; ++j) {
                    cycles += " " + configArr[j];
                }
                cycles = cycles.substring(1);
                break;
            }
        }
        if (!cycles.equals("")) {
            String[] newConfig = new String[cycleIndex];
            System.arraycopy(configArr, 1, newConfig, 0, cycleIndex - 1);
            newConfig[cycleIndex - 1] = cycles;
            setUp(m, newConfig);
        } else {
            String[] newConfig = new String[configArr.length - 1];
            System.arraycopy(configArr, 1, newConfig, 0, configArr.length - 1);
            setUp(m, newConfig);
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** Names of all rotors. */
    private static String[] rotorNames;
}
