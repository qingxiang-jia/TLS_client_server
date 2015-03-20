package a2.util;//Qingxiang Jia

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
    public static byte[] encryptAES(byte[] pwd, byte[] IV, byte[] file) { return AES(pwd, IV, file, Cipher.ENCRYPT_MODE); }

    /**
     * Wrapper to decrypt file using password (AES)
     *
     * @param pwd  Password, can be number, letter, and , . / < > ? ; : ’ " [ ] { } \ | ! @ # $ % ˆ & * ( ) - _ = +
     * @param file File to be decrypted
     * @return Decrypted byte array
     */
    public static byte[] decryptAES(byte[] pwd, byte[] IV, byte[] file) { return AES(pwd, IV, file, Cipher.DECRYPT_MODE); }

    /**
     * The actual AES encryption/decryption procedure.
     *
     * @param pwd  Password
     * @param file File to be encrypted/decrypted
     * @param mode 1 = encryption, 2 = decryption
     * @return encrypted/decrypted byte array
     */
    private static byte[] AES(byte[] pwd, byte[] IV, byte[] file, int mode)
    { // IV new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        try {
            Cipher AESCipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // cannot use PKCS7 with AES in Java
            SecretKey AESKey = new SecretKeySpec(pwd, "AES"); // set up key using password
            AESCipher.init(mode, AESKey, new IvParameterSpec(IV)); // IV, as TA suggested, hardcoded
            return AESCipher.doFinal(file); // return the encrypted/decrypted byte array
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot find algorithm for AES.");
        } catch (NoSuchPaddingException e) {
            System.out.println("AES: PKCS5Padding is invalid.");
        } catch (InvalidKeyException e) {
            System.out.println("AES: Key is invalid.");
        } catch (IllegalBlockSizeException e) {
            System.out.println("The file to be en/decrypted has a size not of multiple of 16. The file is tampered!");
        } catch (BadPaddingException e) {
            System.out.println("AES: PKCS5Padding is invalid.");
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println("AES: The IV is invalid.");
        }
        return null;
    }
}
