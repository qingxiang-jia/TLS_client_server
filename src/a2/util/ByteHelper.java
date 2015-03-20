package a2.util;

/**
 * Deals with all byte or byte array related operations.
 */
public class ByteHelper
{
    // given [pre] and [arr], return [pre arr]
    public static byte[] concate(byte[] pre, byte[] arr)
    {
        byte[] res = new byte[pre.length + arr.length];
        System.arraycopy(pre, 0, res, 0, pre.length); // copy pre to res
        System.arraycopy(arr, 0, res, pre.length+1, arr.length); // copy arr to res
        return res;
    }

    // given [arr], return [[arr1] [arr2]]
    public static byte[][] split(byte[] arr, int newStart)
    {
        byte[] arr1 = new byte[newStart], arr2 = new byte[arr.length - arr1.length];
        byte[][] res = new byte[][]{arr1, arr2};
        System.arraycopy(arr, 0, arr1, 0, arr1.length);
        System.arraycopy(arr, arr1.length, arr2, 0, arr2.length);
        return res;
    }
}
