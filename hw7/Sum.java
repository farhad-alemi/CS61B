import java.util.Arrays;

/** HW #7, Two-sum problem.
 * @author Farhad Alemi
 */
public class Sum {

    /** Returns true iff A[i]+B[j] = M for some i and j. */
    public static boolean sumsTo(int[] A, int[] B, int m) {
        Arrays.sort(A);
        Arrays.sort(B);

        for (int val : A) {
            if (val + B[0] <= m && val + B[B.length - 1] >= m) {
                if (Arrays.binarySearch(B, m - val) >= 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
