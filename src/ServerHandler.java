import java.io.*;

/**
 * Handles all requests from client.
 * Notice that since server has no interaction with user,
 * there is no ServerLogic between this and server.
 *
 * The client can get whatever file it stores via the server.
 * Each client can ONLY get its files. The server should distinguish
 * client by authentication. <-- NOT YET IMPLEMENTED!
 */
public class ServerHandler
{
    /**
     * Handles all client requests, a role of dispatcher.
     * @param netIn data from client comes in
     * @param netOut data sent to client
     */
    public static void handles(InputStream netIn, OutputStream netOut)
    {
        ObjectOutputStream objOut = null;
        ObjectInputStream objIn = null;
        try {
            objOut = new ObjectOutputStream(netOut);
            objIn = new ObjectInputStream(netIn);
            Message msg;
            msg = (Message) objIn.readObject(); // blocking
            if (msg.getType() == Message.GET_REQ_N || msg.getType() == Message.GET_REQ_E) // delegation
                handleGetRequest(objOut, msg);
            else if (msg.getType() == Message.PUT_REQ_N || msg.getType() == Message.PUT_REQ_E) // delegation
                handlePutRequest(objOut, msg);
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

    // need to check if the client stores the file
    /* server doesn't need to know if the file is encrypted, the client is responsible
     for specifying whether he wants to retrieve the file E or N
     */
    private static void handleGetRequest(ObjectOutputStream objOut, Message msg)
    {
        /** read wanted file **/
        byte[] file, hash;
        Message rsp = null;
        try {
            file = IO.readFileThrowsException(msg.getPath().toString());
            hash = IO.readFileThrowsException(msg.getPath().toString()+".sha256");
            if (msg.getType() == Message.GET_REQ_N) { // no encryption
                rsp = new Message(Message.GET_RSP_N, file, hash);
            } else { // with encryption
                rsp = new Message(Message.GET_RSP_E, file, hash);
            }
        } catch (IOException e) { // anyway, file is unreadable
            try {
                objOut.writeObject(new Message(Message.ERROR_RSP, "Error: Requested file cannot be retrieved"));
            } catch (IOException innerE) {
                System.out.println("Failed to send error message");
            }
        }
        /** send wanted file **/
        try {
            if (rsp != null)
                objOut.writeObject(rsp);
        } catch (IOException e) {
            System.out.println("Failed to send requested file");
        }
    }

    // for now, the server receives the file (not checking its hash), stores
    // the hash and file separately with IV(clear) prepended to ciphertext
    // NEED to add book keeping of who stores the file later
    private static void handlePutRequest(ObjectOutputStream objOut, Message msg)
    {
        /** write file and hash to disk **/
        IO.writeFile(msg.getData(), msg.getPath().getFileName().toString());
        IO.writeFile(msg.getHash(), msg.getPath().getFileName().toString()+".sha256");
        /** send success notice **/
        Message rsp = new Message(Message.SUCCESS_RSP, "Transfer of "+msg.getPath().getFileName().toString()+" complete");
        try {
            objOut.writeObject(rsp);
        } catch (IOException e) {
            System.out.println("Failed to send success message");
        }
    }
}
