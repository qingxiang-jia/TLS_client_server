import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Returns hash of the byte array.
 */
public class Hasher
{
    /**
     * Computes SHA-256 hash of file.
     * @param file The file to be hashed
     * @return A byte array represents the hash of file
     */
    public static byte[] SHA256(byte[] file)
    {
        byte[] hash = null;
        try {
            MessageDigest h = MessageDigest.getInstance("SHA-256");
            hash = h.digest(file);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA-256 unsupported");
        }
        return hash;
    }
}
