/** A WeirdList holds a sequence of integers.
 * @author Farhad Alemi
 */
public class WeirdList {
    /** The empty sequence of integers. */
    public static final WeirdList EMPTY = new Helper(0,null);

    /** Sequence head */
    private int _head;

    /** Sequence tail */
    WeirdList _tail;

    /** A new WeirdList whose head is HEAD and tail is TAIL. */
    public WeirdList(int head, WeirdList tail) { 
        _head = head;
        _tail = tail;
    }

    /** Returns the number of elements in the sequence that
     *  starts with THIS. */
    public int length() {
        return 1 + _tail.length();
    }

    /** Return a string containing my contents as a sequence of numerals
     *  each preceded by a blank.  Thus, if my list contains
     *  5, 4, and 2, this returns " 5 4 2". */
    @Override
    public String toString() {
        return (" " + _head + _tail.toString());
    }

    /** Part 3b: Apply FUNC.apply to every element of THIS WeirdList in
     *  sequence, and return a WeirdList of the resulting values. */
    public WeirdList map(IntUnaryFunction func) {
        return new WeirdList(func.apply(_head), _tail.map(func));
    }

    /**
     * Helper class which helps implement polymorphism.
     */
    private static final class Helper extends WeirdList {

        /**
         * A new WeirdList whose head is HEAD and tail is TAIL.
         * HEAD is the head element in the list. TAIL is the
         * tail element.
         */
        Helper(int head, WeirdList tail) {
            super(head, tail);
        }

        /**
         * Returns the length for the list.
         */
        public int length() {
            return 0;
        }

        /**
         * Returns the String representation of the lists.
         */
        @Override
        public String toString() {
            return "";
        }

        /**
         * This function returns null. Super.map() applies func.apply
         * to every element in WeirdList object and returns a new object.
         * This behavior is required for polymorphism to work correctly.
         */
        public WeirdList map(IntUnaryFunction func) {
            return new Helper(0, null);
        }
    }
}
