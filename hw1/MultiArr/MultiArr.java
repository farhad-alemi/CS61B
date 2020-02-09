/** Multidimensional array 
 *  @author Zoe Plaxco
 */

public class MultiArr {

    /**
    {{“hello”,"you",”world”} ,{“how”,”are”,”you”}} prints:
    Rows: 2
    Columns: 3
    
    {{1,3,4}, {1}, {5,6,7,8}, {7,9}} prints:
    Rows: 4
    Columns: 4
    */
    public static void printRowAndCol(int[][] arr) {
        int numRows = arr.length;
        int numCols = arr[0].length;

        for (int i = 1; i < arr.length; ++i) {
            if (numCols > arr[i].length) {
                numCols = arr[i].length;
            }
        }
        System.out.println("Rows: " + numRows);
        System.out.println("Columns: " + numCols);
    } 

    /**
    @param arr: 2d array
    @return maximal value present anywhere in the 2d array
    */
    public static int maxValue(int[][] arr) {
        int maxValue = arr[0][0];
        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < arr[i].length; ++j) {
                if (arr[i][j] > maxValue) {
                    maxValue = arr[i][j];
                }
            }
        }
        return maxValue;
    }

    /**Return an array where each element is the sum of the 
    corresponding row of the 2d array*/
    public static int[] allRowSums(int[][] arr) {
        int[] sumArr = new int[arr.length];

        for (int i = 0; i < arr.length; ++i) {
            for (int j = 0; j < arr[i].length; ++j) {
                sumArr[i] += arr[i][j];
            }
        }
        return sumArr;
    }
}
