package a2.handlers;

import a2.comm.Message;
import a2.util.ByteHelper;
import a2.util.Crypto;
import a2.util.Hasher;
import a2.util.IO;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Handles all client-related operations such as get and put.
 */
public class ClientHandler
{
    public static void handleGet(Path path, InputStream netIn, OutputStream netOut)
    {
        handleGet(path, null, netIn, netOut);
    }

    public static void handleGet(Path path, String password, InputStream netIn, OutputStream netOut)
    {
        ObjectOutputStream objOut = null;
        ObjectInputStream objIn = null;
        try {
            objOut = new ObjectOutputStream(netOut);
            objIn = new ObjectInputStream(netIn);
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
                    IO.writeFile(rsp.getData(), rsp.getPath().getFileName().toString());
                    System.out.printf("Retrieval of %s completed\n", rsp.getPath().getFileName());
                }
            } else if (rsp.getType() == Message.GET_RSP_E) { // decrypt, hash, and compare, if good, write to disk
                if (password == null) {
                    System.out.println("Error: Server returned encrypted data, but no password provided");
                } else { // encrypted, have password
                    /** decrypt data **/
                    byte[][] VICiphertext = ByteHelper.split(rsp.getData(), 16);
                    byte[] IV = VICiphertext[0], ciphertext = VICiphertext[1];
                    byte[] plaintext = null;
                    try {
                        plaintext = Crypto.decryptAES(password.getBytes(Charset.forName("UTF-8")), IV, ciphertext);
                    } catch (Crypto.DecryptionFailedException e) {
                        System.out.println("Error: Decryption failed, wrong password or file not encrypted");
                    }
                    /** check hash **/
                    if (plaintext != null) {
                        if (!Arrays.equals(Hasher.SHA256(plaintext), rsp.getHash()))
                            System.out.printf("Error: Computed hash of %s does not match retrieved hash\n", rsp.getPath().getFileName());
                        else { /** nothing wrong, write the received file to disk **/
                            IO.writeFile(plaintext, rsp.getPath().getFileName().toString());
                            System.out.printf("Retrieval of %s completed\n", rsp.getPath().getFileName());
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Class read out from ObjectInputStream not found");
        } finally {
            try {
                if (objOut != null)
                    objOut.close();
                if (objIn != null)
                    objIn.close();
                if (netOut != null)
                    netOut.close();
                if (netIn != null)
                    netIn.close();
            } catch (IOException e) {
                System.out.println("Cannot close stream(s)");
            }
        }
    }

    public static void handlePut(Path path, InputStream netIn, OutputStream netOut)
    {
        handlePut(path, null, netIn, netOut);
    }

    public static void handlePut(Path path, String password, InputStream netIn, OutputStream netOut)
    {
        ObjectOutputStream objOut = null;
        ObjectInputStream objIn = null;
        try {
            objOut = new ObjectOutputStream(netOut);
            objIn = new ObjectInputStream(netIn);
            Message msg;
            /** prepare file to send **/
            byte[] plaintext = IO.readFile(path.toString());
            byte[] hash = Hasher.SHA256(plaintext);
            if (password == null) { /** if no encryption, ready to send **/
                msg = new Message(Message.PUT_REQ_N, plaintext, hash);
            } else { /** encrypt file **/
                byte[] IV = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                byte[] ciphertext = Crypto.encryptAES(password.getBytes(Charset.forName("UTF-8")), IV, plaintext);
                byte[] IVCiphertext = ByteHelper.concate(IV, ciphertext);
                msg = new Message(Message.PUT_REQ_E, IVCiphertext, hash);
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
        } finally {
            try {
                if (objOut != null)
                    objOut.close();
                if (objIn != null)
                    objIn.close();
                if (netOut != null)
                    netOut.close();
                if (netIn != null)
                    netIn.close();
            } catch (IOException e) {
                System.out.println("Cannot close stream(s)");
            }
        }
    }
}
