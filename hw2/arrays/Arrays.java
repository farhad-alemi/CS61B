package arrays;

/** HW #2 */

/** Array utilities.
 *  @author Farhad Alemi
 */
class Arrays {

    /* C1. */
    /** Returns a new array consisting of the elements of A followed by the
     *  the elements of B. */
    static int[] catenate(int[] A, int[] B) {
        int[] result = new int[A.length + B.length];
        System.arraycopy(A, 0, result, 0, A.length);
        System.arraycopy(B, 0, result, A.length, B.length);
        return result;
    }

    /* C2. */
    /** Returns the array formed by removing LEN items from A,
     *  beginning with item #START. */
    static int[] remove(int[] A, int start, int len) {
        int[] result = new int[A.length - len];
        System.arraycopy(A, 0, result, 0, start);
        System.arraycopy(A, start + len, result, start, A.length - len - start);
        return result;
    }

    /* C3. */
    /** Returns the array of arrays formed by breaking up A into
     *  maximal ascending lists, without reordering.
     *  For example, if A is {1, 3, 7, 5, 4, 6, 9, 10}, then
     *  returns the three-element array
     *  {{1, 3, 7}, {5}, {4, 6, 9, 10}}. */
    static int[][] naturalRuns(int[] A) {
        if (A.length == 0) {
            return new int[0][0];
        } else if (A.length == 1) {
            int[][] tempArr = new int[1][];
            tempArr[0] = new int[1];
            System.arraycopy(A, 0,tempArr[0], 0, A.length);
            return tempArr;
        }
        int[][] tempArr = new int[A.length][];
        int startIndex = 0, ascIndex = 0;

        for (int i = 0, j = 1; j < A.length; ++i, ++j) {
            if (A[i] >= A[j]) {
                tempArr[ascIndex] = new int[j - startIndex];
                System.arraycopy(A, startIndex, tempArr[ascIndex], 0, j - startIndex);
                startIndex = j;
                ++ascIndex;
                //1, 3, 7, 5, 4, 6, 9, 10, 10, 11
            } if (j == A.length - 1) {
                tempArr[ascIndex] = new int[j - startIndex + 1];
                System.arraycopy(A, startIndex, tempArr[ascIndex], 0, j - startIndex + 1);
                ++ascIndex;
            }
        }
        int[][] ascArr = new int[ascIndex][];
        System.arraycopy(tempArr, 0, ascArr, 0, ascIndex);
        return ascArr;
    }
}
