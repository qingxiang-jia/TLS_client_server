import java.io.*;

/**
 * Handles all requests from client.
 * Notice that since server has no interaction with user,
 * there is no ServerLogic between this and server.
 */
public class ServerHandler
{
    /**
     * Handles all client requests, a role of dispatcher.
     * @param objIn Data from client comes in
     * @param objOut Data sent to client
     */
    public static void handles(ObjectInputStream objIn, ObjectOutputStream objOut) throws IOException
    {
        try {
            Message msg;
            msg = (Message) objIn.readObject(); // blocking
            if (msg.getType() == Message.GET_REQ_N || msg.getType() == Message.GET_REQ_E) // delegation
                handleGetRequest(objOut, msg);
            else if (msg.getType() == Message.PUT_REQ_N || msg.getType() == Message.PUT_REQ_E) // delegation
                handlePutRequest(objOut, msg);
        } catch (ClassNotFoundException e) {
            System.out.println("Class read out from ObjectInputStream not found");
        }
    }

    /**
     * Handles get request from client.
     * Server doesn't need to know if the file is encrypted, the client is responsible
     * for specifying whether he wants to retrieve the file E or N
     * @param objOut Data sent to client
     * @param msg Message object from client
     */
    private static void handleGetRequest(ObjectOutputStream objOut, Message msg)
    {
        Message rsp = null;
        /** check if file path is legal (is path in right format + file exists)
            check if client has access to the path, and if file exists **/
        if (!Path.checkPath(msg.getPath().toString())) {
            try {
                objOut.writeObject(new Message(Message.ERROR_RSP, "Error: Requested file cannot be retrieved")); // send error message to client
            } catch (IOException innerE) {
                System.out.println("Failed to send error message");
            } return; // done
        }
        System.out.println("Path valid"); // passed check
        /** read wanted file **/
        byte[] file, hash;
        try {
            file = IO.readFileThrowsException(msg.getPath().toString()); // read in file
            hash = IO.readFileThrowsException(msg.getPath().toString()+".sha256"); // read in associated hash
            if (msg.getType() == Message.GET_REQ_N) { // no encryption (N)
                rsp = new Message(Message.GET_RSP_N, msg.getPath(), file, hash);
            } else { // with encryption (E)
                rsp = new Message(Message.GET_RSP_E, msg.getPath(), file, hash);
            }
        } catch (IOException e) { // anyway, file is unreadable to client
            try {
                objOut.writeObject(new Message(Message.ERROR_RSP, "Error: Requested file cannot be retrieved")); // send error message to client
            } catch (IOException innerE) {
                System.out.println("Failed to send error message");
            }
        }
        /** send wanted file **/
        try {
            if (rsp != null)
                objOut.writeObject(rsp); // send file to client
        } catch (IOException e) {
            System.out.println("Failed to send requested file");
        }
    }

    /**
     * Handles put request from client.
     * Server receives the file (not checking its hash), stores
     * the hash and file separately with IV(clear) prepended to ciphertext
     * @param objOut Data sent to client
     * @param msg Message object from client
     */
    private static void handlePutRequest(ObjectOutputStream objOut, Message msg)
    {
        /** write file and hash to disk **/
        IO.writeFile(msg.getData(), msg.getPath().getFileName());
        IO.writeFile(msg.getHash(), msg.getPath().getFileName()+".sha256");
        /** send success notice **/                               // as required, only stores file under its directory, filename can be used as path
        Message rsp = new Message(Message.SUCCESS_RSP, "Transfer of "+msg.getPath().getFileName()+" complete");
        try {
            objOut.writeObject(rsp);
            System.out.println("Retrieval success");
        } catch (IOException e) {
            System.out.println("Failed to send success message");
        }
    }
}
