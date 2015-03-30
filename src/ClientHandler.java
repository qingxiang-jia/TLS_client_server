import java.io.*;
import java.util.Arrays;

/**
 * Handles all client-related operations such as get and put.
 */
public class ClientHandler
{
    public static void handleGet(Path path, ObjectInputStream objIn, ObjectOutputStream objOut)
    {
        handleGet(path, null, objIn, objOut);
    }

    public static void handleGet(Path path, String password, ObjectInputStream objIn, ObjectOutputStream objOut)
    {
        try {
            Message msg;
            if (password == null)
                msg = new Message(Message.GET_REQ_N, path); // get, no encryption
            else
                msg = new Message(Message.GET_REQ_E, path); // get, with encryption
            objOut.writeObject(msg); // sends msg to server
            /** handle server response **/
            Message rsp = (Message) objIn.readObject();
            if (rsp.getType() == Message.ERROR_RSP)
                System.out.println(rsp.getInfo());
            else if (rsp.getType() == Message.GET_RSP_N) { // hash and compare, if good, write to disk
                if (!Arrays.equals(Hasher.SHA256(rsp.getData()), rsp.getHash()))
                    System.out.printf("Error: Computed hash of %s does not match retrieved hash\n", rsp.getPath().getFileName());
                else { /** nothing wrong, write the received file to disk **/
                    IO.writeFile(rsp.getData(), rsp.getPath().getFileName());
                    System.out.printf("Retrieval of %s completed\n", rsp.getPath().getFileName());
                }
            } else if (rsp.getType() == Message.GET_RSP_E) { // decrypt, hash, and compare, if good, write to disk
                if (password == null) {
                    System.out.println("Error: Server returned encrypted data, but no password provided");
                } else { // encrypted, have password
                    /** decrypt data **/
                    System.out.println("Decrypting received file");
                    byte[][] VICiphertext = ByteHelper.split(rsp.getData(), 16);
                    byte[] IV = VICiphertext[0], ciphertext = VICiphertext[1];
                    byte[] plaintext = null;
                    try {
                        plaintext = Crypto.decryptAES(password, IV, ciphertext);
                    } catch (Crypto.DecryptionFailedException e) {
                        System.out.println("Error: Decryption failed, wrong password or file not encrypted");
                    }
                    /** check hash **/
                    if (plaintext != null) {
                        if (!Arrays.equals(Hasher.SHA256(plaintext), rsp.getHash()))
                            System.out.printf("Error: Computed hash of %s does not match retrieved hash\n", rsp.getPath().getFileName());
                        else { /** nothing wrong, write the received file to disk **/
                            System.out.println("Identical hash");
                            IO.writeFile(plaintext, rsp.getPath().getFileName());
                            System.out.printf("Retrieval of %s completed\n", rsp.getPath().getFileName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Server down.\nExiting");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.out.println("Class read out from ObjectInputStream not found");
        }
    }

    public static void handlePut(Path path, ObjectInputStream objIn, ObjectOutputStream objOut)
    {
        handlePut(path, null, objIn, objOut);
    }

    public static void handlePut(Path path, String password, ObjectInputStream objIn, ObjectOutputStream objOut)
    {
        try {
            Message msg;
            /** prepare file to send **/
            byte[] plaintext = IO.readFile(path.toString());
            byte[] hash = Hasher.SHA256(plaintext);
            if (password == null) { /** if no encryption, ready to send **/
                msg = new Message(Message.PUT_REQ_N, path, plaintext, hash);
            } else { /** encrypt file **/
                byte[] IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                byte[] ciphertext = Crypto.encryptAES(password, IV, plaintext);
                byte[] IVCiphertext = ByteHelper.concate(IV, ciphertext);
                msg = new Message(Message.PUT_REQ_E, path, IVCiphertext, hash);
            } /** send **/
            objOut.writeObject(msg);
            /** handle server response **/
            Message rsp = (Message) objIn.readObject(); // blocking
            if (rsp.getType() == Message.ERROR_RSP || rsp.getType() == Message.SUCCESS_RSP) // no matter what, print it
                System.out.println(rsp.getInfo());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class read out from ObjectInputStream not found");
        }
    }
}
