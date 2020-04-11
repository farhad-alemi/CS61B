import java.util.Arrays;
import java.util.Random;

/** Runs timing tests on Sum.java.
 *  @author Josh Hug & Farhad Alemi
 */
public class SumTest {
    /** Random number seed for reproducibility. */
    private static final long RANDOM_SEED = 12345654321L;
    /** Generator for test data. */
    private static Random r = new Random(RANDOM_SEED);

    /** Size of "large" test array. */
    static final int LARGE_ARRAY_SIZE = 100000;

    /** Run sumTo on A, B, and M, and print timings. */
    public static void printTime(int[] A, int[] B, int m) {
        double timeTaken = time(A, B, m);
        System.out.printf("%s took %.2f seconds to search %s.\n",
                "SumTo", timeTaken, "if A[i] + B[j] = m for some i and j");
    }

    /** Return the time for executing sumTo on A, B and M. */
    private static double time(int[] A, int[] B, int m) {
        Stopwatch sw = new Stopwatch();
        Sum.sumsTo(A, B, m);
        return sw.elapsedTime();
    }

    /** Returns an array of N integers between [0, MAXINT). */
    public static int[] randomInts(int N, int maxInt) {
        int[] ints = new int[N];
        for (int i = 0; i < N; i += 1) {
            ints[i] = r.nextInt(maxInt);
        }
        return ints;
    }

    /** Returns a partially sorted array of N integers between
     * [0, MAXINT). */
    public static int[] randomNearlySortedInts(int N, int maxInt) {
        int[] ints = new int[N];
        for (int i = 0; i < N; i += 1) {
            ints[i] = r.nextInt(maxInt);
        }
        Arrays.sort(ints);

        for (int i = 0; i < N; i += 1) {
            int swapIndex = r.nextInt(N - 1);
            MySortingAlgorithms.swap(ints, swapIndex, swapIndex + 1);
        }
        return ints;
    }

    /** Tests sumTo on arrays of upto NUMINTS for M. */
    public static void largeArrayTest(int numInts, int m) {
        int maxVal = Integer.MAX_VALUE;

        int[] A = randomInts(numInts, maxVal);
        int[] B = randomInts(numInts, maxVal / 10);

        System.out.println("/* Random Arrays A and B */");
        printTime(A, B, m);
    }

    /** Tests sumTo on almost sorted arrays of upto NUMINTS for M. */
    public static void almostSortedTest(int numInts, int m) {
        int maxVal = Integer.MAX_VALUE;
        int[] A =
                randomNearlySortedInts(numInts, maxVal);
        int[] B = randomNearlySortedInts(numInts, maxVal / 100);

        System.out.println("/* Almost-sorted Arrays A and B */");
        printTime(A, B, m);
    }

    /** Run timing tests on arrays whose is given by ARGS[0], or
     *  LARGE_ARRAY_SIZE if there are no arguments. */
    public static void main(String[] args) {
        System.out.println("Edit Configurations and type a number for "
                + "program arguments to run a smaller test.");
        int size =
                args.length == 0 ? LARGE_ARRAY_SIZE : Integer.parseInt(args[0]);

        int m = r.nextInt();
        largeArrayTest(size, m);
        almostSortedTest(size, m);
    }
}
