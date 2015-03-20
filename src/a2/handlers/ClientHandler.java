package a2.handlers;

import a2.comm.Message;
import a2.util.Hasher;
import a2.util.IO;

import java.io.*;
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
                }
            } else if (rsp.getType() == Message.GET_RSP_E) { // decrypt, hash, and compare, if good, write to disk

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

    }

    public static void handlePut(Path path, String password, InputStream netIn, OutputStream netOut)
    {

    }

    public static void receive(BufferedReader in)
    {
        String s;
        try {
            while ((s = in.readLine()) != null)
                System.out.println("Received: " + s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
