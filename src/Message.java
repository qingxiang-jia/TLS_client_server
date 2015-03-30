import java.io.Serializable;

/**
 * Represents the data sent between server and client.
 * This is the object that is been sent back and forth between client and server.
 * However, yesterday I noticed that Professor Debra suggests us to only manipulate bytes. Next time, I will
 * do it in the suggested way. She says this is fine.
 */
public class Message implements Serializable
{
    /** define message types **/
    public static final int DEBUG = -1; // debug mode
    /** client side options **/
    public static final int GET_REQ_N = 0; // get operation, no encryption
    public static final int GET_REQ_E = 1; // get operation, with encryption
    public static final int PUT_REQ_N = 2; // put operation, no encryption
    public static final int PUT_REQ_E = 3; // put operation, with encryption
    /** server side options **/
    public static final int GET_RSP_N = 4; // server's response to get (N)
    public static final int GET_RSP_E = 5; // server's response to get (E)
    public static final int PUT_RSP_N = 6; // server's response to put (N)
    public static final int PUT_RSP_E = 7; // server's response to put (E)
    public static final int ERROR_RSP = 8; // server's response: error
    public static final int SUCCESS_RSP = 9; // indicating operation's success

    int type; // one of the types above
    Path path; // represents the path in either get or put operation
    String info; // represents text messages from server
    byte[] data, hash; // data: ciphertext or plaintext

    /**
     * Constructor typically used in client's get operations
     * @param type An integer that specifies which type the message is.
     * @param path An Path object that specifies the path of the file
     */
    public Message(int type, Path path)
    {
        this.type = type;
        this.path = path;
    }

    /**
     * Constructor typically used in client's put operation or server's response to get
     * @param type An integer that specifies which type the message is.
     * @param path An Path object that specifies the path of the file
     * @param data Ciphertext or plaintext
     * @param hash Hash of the file (If file is encrypted, the hash is for the plaintext.)
     */
    public Message(int type, Path path, byte[] data, byte[] hash)
    {
        this.type = type;
        this.path = path;
        this.data = data;
        this.hash = hash;
    }

    /**
     * Constructor typically used in server sending error message to client.
     * @param type An integer that specifies which type the message is.
     * @param info Text information
     */
    public Message(int type, String info)
    {
        this.type = type;
        this.info = info;
    }

    /**
     * @return Type of this message
     */
    public int getType()
    {
        return type;
    }

    /**
     * @return Hash associated with the file contained in this message
     */
    public byte[] getHash()
    {
        return hash;
    }

    /**
     * @return File contained in this message
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * @return Path object associated with the file
     */
    public Path getPath()
    {
        return path;
    }

    /**
     * @return String object that usually is the error/success message sent from server to client
     */
    public String getInfo()
    {
        return info;
    }
}
