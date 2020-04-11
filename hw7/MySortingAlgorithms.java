import java.util.Arrays;

/**
 * Note that every sorting algorithm takes in an argument k. The sorting
 * algorithm should sort the array from index 0 to k. This argument could
 * be useful for some of your sorts.
 *
 * Class containing all the sorting algorithms from 61B to date.
 *
 * You may add any number instance variables and instance methods
 * to your Sorting Algorithm classes.
 *
 * You may also override the empty no-argument constructor, but please
 * only use the no-argument constructor for each of the Sorting
 * Algorithms, as that is what will be used for testing.
 *
 * Feel free to use any resources out there to write each sort,
 * including existing implementations on the web or from DSIJ.
 *
 * All implementations except Counting Sort adopted from Algorithms,
 * a textbook by Kevin Wayne and Bob Sedgewick. Their code does not
 * obey our style conventions.
 * @author Farhad Alemi
 */
public class MySortingAlgorithms {

    /**
     * Java's Sorting Algorithm. Java uses Quicksort for ints.
     */
    public static class JavaSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            Arrays.sort(array, 0, k);
        }

        @Override
        public String toString() {
            return "Built-In Sort (uses quicksort for ints)";
        }
    }

    /** Insertion sorts the provided data. */
    public static class InsertionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k < 0 || k > array.length) {
                return;
            }
            for (int i = 1; i < k; i++) {
                int sortKey = array[i];
                int j = i - 1;

                while (j >= 0 && array[j] > sortKey) {
                    array[j + 1] = array[j];
                    --j;
                }
                array[j + 1] = sortKey;
            }
        }

        @Override
        public String toString() {
            return "Insertion Sort";
        }
    }

    /**
     * Selection Sort for small K should be more efficient
     * than for larger K. You do not need to use a heap,
     * though if you want an extra challenge, feel free to
     * implement a heap based selection sort (i.e. heapsort).
     */
    public static class SelectionSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k < 0 || k > array.length) {
                return;
            }
            for (int i = 0; i < k - 1; ++i) {
                int minIndex = i;
                for (int j = i + 1; j < k; ++j) {
                    if (array[j] < array[minIndex]) {
                        minIndex = j;
                    }
                }
                swap(array, minIndex, i);
            }
        }

        @Override
        public String toString() {
            return "Selection Sort";
        }
    }

    /** Your mergesort implementation. An iterative merge
      * method is easier to write than a recursive merge method.
      * Note: I'm only talking about the merge operation here,
      * not the entire algorithm, which is easier to do recursively.
      */
    public static class MergeSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            if (k < 2) {
                return;
            }
            int midIndex = k / 2;
            int[] leftArr = new int[midIndex];
            int[] rightArr = new int[k - midIndex];

            for (int i = 0; i < midIndex; ++i) {
                leftArr[i] = array[i];
            }
            for (int i = midIndex; i < k; i++) {
                rightArr[i - midIndex] = array[i];
            }
            sort(leftArr, midIndex);
            sort(rightArr, k - midIndex);

            merge(array, leftArr, rightArr, midIndex, k - midIndex);
        }

        /** Helper method used for merging ARRAY using LEFTARR, RIGHTARR,
         *  LEFTINDEX, RIGHTINDEX. */
        public static void merge(int[] array, int[] leftArr, int[] rightArr,
                                 int leftIndex, int rightIndex) {
            int i = 0, j = 0, k = 0;
            while (i < leftIndex && j < rightIndex) {
                if (leftArr[i] <= rightArr[j]) {
                    array[k++] = leftArr[i++];
                } else {
                    array[k++] = rightArr[j++];
                }
            }
            while (i < leftIndex) {
                array[k++] = leftArr[i++];
            }
            while (j < rightIndex) {
                array[k++] = rightArr[j++];
            }
        }

        @Override
        public String toString() {
            return "Merge Sort";
        }
    }

    /**
     * Your Counting Sort implementation.
     * You should create a count array that is the
     * same size as the value of the max digit in the array.
     */
    public static class CountingSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            /* to be implemented */
        }

        /* to be implemented - may want to add additional methods */

        @Override
        public String toString() {
            return "Counting Sort";
        }
    }

    /** Your Heapsort implementation.
     */
    public static class HeapSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            /* may want to add additional methods */
        }

        @Override
        public String toString() {
            return "Heap Sort";
        }
    }

    /** Your Quicksort implementation.
     */
    public static class QuickSort implements SortingAlgorithm {
        @Override
        public void sort(int[] array, int k) {
            /* may want to add additional methods */
        }

        @Override
        public String toString() {
            return "Quicksort";
        }
    }

    /* For radix sorts, treat the integers as strings of x-bit numbers.  For
     * example, if you take x to be 2, then the least significant digit of
     * 25 (= 11001 in binary) would be 1 (01), the next least would be 2 (10)
     * and the third least would be 1.  The rest would be 0.  You can even take
     * x to be 1 and sort one bit at a time.  It might be interesting to see
     * how the times compare for various values of x. */

    /**
     * LSD Sort implementation.
     */
    public static class LSDSort implements SortingAlgorithm {

        /** Method which returns the largest element in ARRAY for the first K
         * elements. */
        static int findMax(int[] array, int k) {
            if (k > array.length) {
                return Integer.MIN_VALUE;
            }
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < k; ++i) {
                if (array[i] > max) {
                    max = array[i];
                }
            }
            return max;
        }

        /** The method does the counting sort of ARRAY upto K according to the
         * digit represented by EXPONENT. */
        static void countSort(int[] array, int k, int exponent) {
            int[] output = new int[k], count = new int[10];
            int i;
            Arrays.fill(count, 0);

            for (i = 0; i < k; ++i) {
                ++count[array[i] / exponent % 10];
            }

            for (i = 1; i < 10; ++i) {
                count[i] += count[i - 1];
            }

            for (i = k - 1; i >= 0; --i) {
                output[count[array[i] / exponent % 10] - 1] = array[i];
                --count[(array[i] / exponent) % 10];
            }
            System.arraycopy(output, 0, array, 0, k);
        }

        @Override
        public void sort(int[] a, int k) {
            int max = findMax(a, k);

            for (int exponent = 1; max / exponent > 0; exponent *= 10) {
                countSort(a, k, exponent);
            }
        }

        @Override
        public String toString() {
            return "LSD Sort";
        }
    }

    /**
     * MSD Sort implementation.
     */
    public static class MSDSort implements SortingAlgorithm {
        @Override
        public void sort(int[] a, int k) {
            /* may want to add additional methods */
        }

        @Override
        public String toString() {
            return "MSD Sort";
        }
    }

    /** Exchange A[I] and A[J]. */
    static void swap(int[] a, int i, int j) {
        int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
}
