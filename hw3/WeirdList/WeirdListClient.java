/** Functions to increment and sum the elements of a WeirdList. */
class WeirdListClient {

    /** Return the result of adding N to each element of L. */
    static WeirdList add(WeirdList L, int n) {
        return L.map(new Helper(n));
    }

    /** Return the sum of all the elements in L. */
    static int sum(WeirdList L) {
        return L.m
    }

    /**
     * Helper class which implements the IntUnaryFunction interface.
     */
    private static class Helper implements IntUnaryFunction{

        /** The value to be added to x. */
        int _n;

        /**
         * The constructor which initializes the field.
         */
        Helper(int n) {
            _n = n;
        }

        /**
         * The function adds x to n;
         */
        public int apply(int x) {
            return _n + x;
        }
    }
    /* IMPORTANT: YOU ARE NOT ALLOWED TO USE RECURSION IN ADD AND SUM
     *
     * As with WeirdList, you'll need to add an additional class or
     * perhaps more for WeirdListClient to work. Again, you may put
     * those classes either inside WeirdListClient as private static
     * classes, or in their own separate files.

     * You are still forbidden to use any of the following:
     *       if, switch, while, for, do, try, or the ?: operator.
     *
     * HINT: Try checking out the IntUnaryFunction interface.
     *       Can we use it somehow?
     */
}
