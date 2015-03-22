import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Utility class that handles all encryption/decryption related operations.
 */
public class Crypto
{
    /**
     * Wrapper to encrypt file using password (AES).
     *
     * @param pwd  Password, can be number, letter, and , . / < > ? ; : ’ " [ ] { } \ | ! @ # $ % ˆ & * ( ) - _ = +
     * @param file File to be encrypted
     * @return Encrypted byte array
     */
    public static byte[] encryptAES(String pwd, byte[] IV, byte[] file)
    {
        try {
            return AES(pwd, IV, file, Cipher.ENCRYPT_MODE);
        } catch (DecryptionFailedException e) {
            // no need to handle since it won't be thrown during encryption
        }
        return null;
    }

    /**
     * Wrapper to decrypt file using password (AES)
     *
     * @param pwd  Password, can be number, letter, and , . / < > ? ; : ’ " [ ] { } \ | ! @ # $ % ˆ & * ( ) - _ = +
     * @param file File to be decrypted
     * @return Decrypted byte array
     */
    public static byte[] decryptAES(String pwd, byte[] IV, byte[] file) throws DecryptionFailedException
    { return AES(pwd, IV, file, Cipher.DECRYPT_MODE); }

    /**
     * The actual AES encryption/decryption procedure.
     *
     * @param pwd  Password
     * @param file File to be encrypted/decrypted
     * @param mode 1 = encryption, 2 = decryption
     * @return encrypted/decrypted byte array
     */
    private static byte[] AES(String pwd, byte[] IV, byte[] file, int mode) throws DecryptionFailedException
    { // IV new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        try {
            Cipher AESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // cannot use PKCS7 with AES in Java
            SecretKey AESKey = new SecretKeySpec(AESKeyGen16Bit(pwd), "AES"); // set up key using password
            AESCipher.init(mode, AESKey, new IvParameterSpec(IV));
            return AESCipher.doFinal(file); // return the encrypted/decrypted byte array
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot find algorithm for AES.");
        } catch (NoSuchPaddingException e) {
            System.out.println("AES: PKCS5Padding is invalid.");
            if (mode == Cipher.DECRYPT_MODE) throw new DecryptionFailedException();
        } catch (InvalidKeyException e) {
            System.out.println("AES: Key is invalid.");
            e.printStackTrace();
            if (mode == Cipher.DECRYPT_MODE) throw new DecryptionFailedException();
        } catch (IllegalBlockSizeException e) {
            System.out.println("The file to be en/decrypted has a size not of multiple of 16. The file is tampered!");
            if (mode == Cipher.DECRYPT_MODE) throw new DecryptionFailedException();
        } catch (BadPaddingException e) {
            System.out.println("AES: PKCS5Padding is invalid.");
            if (mode == Cipher.DECRYPT_MODE) throw new DecryptionFailedException();
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println("AES: The IV is invalid.");
            if (mode == Cipher.DECRYPT_MODE) throw new DecryptionFailedException();
        }
        return null;
    }

    private static byte[] AESKeyGen16Bit(String pwd8Bit)
    {
        byte[] AESKey = new byte[16];
        Random rand = new Random(ByteHelper.byteArrToLong(pwd8Bit.getBytes(Charset.forName("UTF-8"))));
        rand.nextBytes(AESKey);
        return AESKey;
    }

    // a rename of general exception, notifies caller password MIGHT be wrong
    public static class DecryptionFailedException extends Exception {}
}
