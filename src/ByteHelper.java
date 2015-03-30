/**
 * Deals with all byte or byte array related operations.
 * e.g. Can be used to prepend IV to file or to split IV out of a file.
 * The idea is that many operations in this assignment deals with array, it is convenient to
 * add a layer of abstraction to make those operations easier.
 */
public class ByteHelper
{
    /**
     * Concatenates two byte array.
     * i.e. given [pre] and [arr], return [pre arr]
     * @param pre The first array to be concatenated
     * @param arr The second array to be concatenated
     * @return A byte array that is concatenation of pre and arr (notice the order)
     */
    public static byte[] concate(byte[] pre, byte[] arr)
    {
        byte[] res = new byte[pre.length + arr.length];
        System.arraycopy(pre, 0, res, 0, pre.length); // copy pre to res
        System.arraycopy(arr, 0, res, pre.length, arr.length); // copy arr to res
        return res;
    }

    //

    /**
     * Splits an array given the splitting point.
     * i.e. given [arr], return [[arr1] [arr2]], where newStart is the index of the
     * first element of arr2.
     * @param arr The original array to be split
     * @param newStart The index of the first element of arr2
     * @return A byte array of split byte arrays
     */
    public static byte[][] split(byte[] arr, int newStart)
    {
        byte[] arr1 = new byte[newStart], arr2 = new byte[arr.length - arr1.length];
        byte[][] res = new byte[][]{arr1, arr2};
        System.arraycopy(arr, 0, arr1, 0, arr1.length);
        System.arraycopy(arr, arr1.length, arr2, 0, arr2.length);
        return res;
    }
}
