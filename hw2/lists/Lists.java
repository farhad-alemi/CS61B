package lists;

/** HW #2, Problem #1. */

/** List problem.
 *  @author Farhad Alemi
 */
class Lists {

    /** Return the list of lists formed by breaking up L into "natural runs":
     *  that is, maximal strictly ascending sublists, in the same order as
     *  the original.  For example, if L is (1, 3, 7, 5, 4, 6, 9, 10, 10, 11),
     *  then result is the four-item list
     *            ((1, 3, 7), (5), (4, 6, 9, 10), (10, 11)).
     *  Destructive: creates no new IntList items, and may modify the
     *  original list pointed to by L. */
    static IntListList naturalRuns(IntList L) {
        if (L == null) {
            return null;
        }
        IntListList lst = new IntListList();
        IntListList temp1Ptr = lst;

        temp1Ptr.head = L;
        IntList temp2Ptr = temp1Ptr.head;
        L = L.tail;

        while (L != null) {
            if (temp2Ptr.head >= L.head) {
                temp1Ptr.tail = new IntListList();
                temp1Ptr = temp1Ptr.tail;
                temp1Ptr.head = L;
                temp2Ptr.tail = null;
                temp2Ptr = temp1Ptr.head;
            } else {
                temp2Ptr = temp2Ptr.tail;
            }
            L = L.tail;
        }
        return lst;
    }
}
