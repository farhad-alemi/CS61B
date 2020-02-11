/**
 * Scheme-like pairs that can be used to form a list of integers.
 *
 * @author P. N. Hilfinger; updated by Vivant Sakore (1/29/2020)
 */
public class IntDList {

    /**
     * First and last nodes of list.
     */
    protected DNode _front, _back;

    /**
     * An empty list.
     */
    public IntDList() {
        _front = _back = null;
    }

    /**
     * @param values the ints to be placed in the IntDList.
     */
    public IntDList(Integer... values) {
        _front = _back = null;
        for (int val : values) {
            insertBack(val);
        }
    }

    /**
     * @return The first value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getFront() {
        return _front._val;
    }

    /**
     * @return The last value in this list.
     * Throws a NullPointerException if the list is empty.
     */
    public int getBack() {
        return _back._val;
    }

    /**
     * @return The number of elements in this list.
     */
    public int size() {
        DNode iter = _front;
        int count = 0;

        while (iter != null) {
            count += 1;
            iter = iter._next;
        }
        return count;
    }

    /**
     *
     * @param i The index of element to return.
     * @return The pointer to DNode at index i.
     */
    private DNode getNode(int i) {
        if (size() == 0) {
            return null;
        } else {
            DNode iter;
            if (i >= 0) {
                iter = _front;
                while (i != 0) {
                    --i;
                    iter = iter._next;
                }
            } else {
                iter = _back;
                while (i < -1) {
                    ++i;
                    iter = iter._prev;
                }
            }
            return iter;
        }
    }

    /**
     * @param i index of element to return,
     *          where i = 0 returns the first element,
     *          i = 1 returns the second element,
     *          i = -1 returns the last element,
     *          i = -2 returns the second to last element, and so on.
     *          You can assume i will always be a valid index,
     *          i.e 0 <= i < size for positive indices,
     *          and -size <= i <= -1 for negative indices.
     * @return The integer value at index i
     */
    public int get(int i) {
        return getNode(i)._val;
    }

    /**
     * @param d value to be inserted in the front
     */
    public void insertFront(int d) {
        DNode obj = new DNode(null, d, _front);
        if (_front !=  null) {
            _front._prev = obj;
        } else {
            _back = obj;
        }
        _front = obj;
    }

    /**
     * @param d value to be inserted in the back
     */
    public void insertBack(int d) {
        DNode obj = new DNode(_back, d, null);
        if (_back != null) {
            _back._next = obj;
        } else {
            _front = obj;
        }
        _back = obj;
    }

    /**
     * @param d     value to be inserted
     * @param index index at which the value should be inserted
     *              where index = 0 inserts at the front,
     *              index = 1 inserts at the second position,
     *              index = -1 inserts at the back,
     *              index = -2 inserts at the second to last position, and so on.
     *              You can assume index will always be a valid index,
     *              i.e 0 <= index <= size for positive indices
     *               (including insertions at front and back)
     *              and -(size+1) <= index <= -1 for negative indices
     *              (including insertions at front and back).
     */
    public void insertAtIndex(int d, int index) {
        int listSize = size();
        if (listSize == 0 || index == -1 || listSize <= index) {
            insertBack(d);
        } else if (index == 0 || index == -listSize - 1) {
            insertFront(d);
        } else {
            DNode ptr = getNode(index);

            if (index < 0) {
                ptr = ptr._next;
            }
            DNode obj = new DNode(ptr._prev, d, ptr);
            if (index != 0) {
                ptr._prev._next = obj;
            }
            ptr._prev = obj;
        }
    }

    /**
     * Removes the first item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteFront() {
        int tempVal = -1000;
        if (size() == 0) {
            return tempVal;
        }

        tempVal = _front._val;
        if (size() == 1) {
            _front = _back = null;

        } else {
            _front._next._prev = null;
            _front = _front._next;
        }
        return tempVal;
    }

    /**
     * Removes the last item in the IntDList and returns it.
     *
     * @return the item that was deleted
     */
    public int deleteBack() {
        int tempVal;
        if (size() == 0 || size() == 1) {
            tempVal = deleteFront();
        } else {
            tempVal = _back._val;
            _back._prev._next = null;
            _back = _back._prev;
        }
        return tempVal;
    }

    /**
     * @param index index of element to be deleted,
     *          where index = 0 returns the first element,
     *          index = 1 will delete the second element,
     *          index = -1 will delete the last element,
     *          index = -2 will delete the second to last element, and so on.
     *          You can assume index will always be a valid index,
     *              i.e 0 <= index < size for positive indices
     *              (including deletions at front and back)
     *              and -size <= index <= -1 for negative indices
     *              (including deletions at front and back).
     * @return the item that was deleted
     */
    public int deleteAtIndex(int index) {
        int val;
        int size = size();

        if (size == 0) {
            val = -1000;
        }
        else if (index == 0 || index == -size || size == 1) {
            val =  deleteFront();
        }
        else if (index == -1 || index == size - 1) {
            val = deleteBack();
        } else {
            DNode ptr = getNode(index);
            val = ptr._val;
            ptr._prev._next = ptr._next;
            ptr._next._prev = ptr._prev;
        }
        return val;
    }

    /**
     * @return a string representation of the IntDList in the form
     * [] (empty list) or [1, 2], etc.
     * Hint:
     * String a = "a";
     * a += "b";
     * System.out.println(a); //prints ab
     */
    public String toString() {
        if (size() == 0) {
            return "[]";
        }
        String stringList = "[" + _front._val;
        DNode iter = _front._next;

        while (iter != null) {
            stringList += ", " + iter._val;
            iter = iter._next;
        }
        return stringList += "]";
    }

    /**
     * DNode is a "static nested class", because we're only using it inside
     * IntDList, so there's no need to put it outside (and "pollute the
     * namespace" with it. This is also referred to as encapsulation.
     * Look it up for more information!
     */
    static class DNode {
        /** Previous DNode. */
        protected DNode _prev;
        /** Next DNode. */
        protected DNode _next;
        /** Value contained in DNode. */
        protected int _val;

        /**
         * @param val the int to be placed in DNode.
         */
        protected DNode(int val) {
            this(null, val, null);
        }

        /**
         * @param prev previous DNode.
         * @param val  value to be stored in DNode.
         * @param next next DNode.
         */
        protected DNode(DNode prev, int val, DNode next) {
            _prev = prev;
            _val = val;
            _next = next;
        }
    }

}
