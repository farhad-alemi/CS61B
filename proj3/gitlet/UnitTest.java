package gitlet;

import ucb.junit.textui;
import org.junit.Test;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Farhad Alemi
 */
public class UnitTest {

    /** Run the JUnit tests in the loa package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class, CommitTest.class));
    }

    /** A dummy test to avoid complaint. */
    @Test
    public void placeholderTest() {
    }

}


