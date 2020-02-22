import java.io.*;
import java.io.IOException;
import static org.junit.Assert.*;

/** Translating Reader: a stream that is a translation of an
 *  existing reader.
 *  @author Farhad Alemi
 */
public class TrReader extends Reader {
    /** A new TrReader that produces the stream of characters produced
     *  by STR, converting all characters that occur in FROM to the
     *  corresponding characters in TO.  That is, change occurrences of
     *  FROM.charAt(i) to TO.charAt(i), for all i, leaving other characters
     *  in STR unchanged.  FROM and TO must have the same length. */
    public TrReader(Reader str, String from, String to) {
        assertTrue("Lengths do not match.", from.length() == to.length());
        this.str = str;
        this.from = from;
        this.to = to;
    }

    /**
     * Reads upto LEN chars from STR. It also performs the corresponding
     * conversion (when applies) from FROM.charAt(i) to TO.charAt(i) and
     * records the result starting in CBUF[i + OFF]. Returns the actual
     * number of characters read or -1 if stream is closed.
     */
    public int read(char[] cbuf, int off, int len)  throws IOException {
        int count = 0;
        while (count < len) {
            int c = str.read(), index;
            if (c == -1) {
                break;
            }
            index = from.indexOf((char) c);
            if (index != -1) {
                cbuf[count + off] = to.charAt(index);
            } else {
                cbuf[count + off] = (char) c;
            }
            ++count;
        }
        return count;
    }

    /**
     * Closes the reader stream.
     */
    public void close() throws IOException {
        str.close();
    }

    /**
     * The reader object whose read() is called in TrReader.Read method.
     */
    Reader str;

    /**
     * String containing all characters that should be changed.
     */
    String from;

    /**
     * String containing all characters that those in FROM should change to.
     */
    String to;
}
