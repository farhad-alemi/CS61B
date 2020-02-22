import java.io.IOException;
import java.io.StringReader;

/** String translation.
 *  @author Farhad Alemi
 */
public class Translate {
    /** This method should return the String S, but with all characters that
     *  occur in FROM changed to the corresponding characters in TO.
     *  FROM and TO must have the same length.
     *  NOTE: You must use your TrReader to achieve this. */
    static String translate(String S, String from, String to) {
        char[] buffer = new char[S.length()];
        try {
            new TrReader(new StringReader(S), from, to)
                    .read(buffer, 0, S.length());;
            return buffer.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
