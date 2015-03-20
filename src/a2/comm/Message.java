package a2.comm;

import java.io.Serializable;
import java.nio.file.Path;

/**
 * Represents the data sent between server and client.
 */
public class Message implements Serializable
{
    // message types
    public static final int DEBUG = -1;
    /** client side options **/
    public static final int GET_REQ_N = 0;
    public static final int GET_REQ_E = 1;
    public static final int PUT_REQ_N = 2;
    public static final int PUT_REQ_E = 3;
    /** server side options **/
    public static final int GET_RSP_N = 4;
    public static final int GET_RSP_E = 5;
    public static final int PUT_RSP_N = 6;
    public static final int PUT_RSP_E = 7;
    public static final int ERROR_RSP = 8; // any errors will be indicated
    public static final int SUCCESS_RSP = 9; // indicating operation's success

    int type;
    Path path;
    String info;
    byte[] data, hash;

    public Message(int type, Path path) // client: get
    {
        this.type = type;
        this.path = path;
    }

    public Message(int type, byte[] data, byte[] hash) // client: put, server: get
    {
        this.type = type;
        this.data = data;
        this.hash = hash;
    }

    public Message(int type, String info)
    {
        this.type = type;
        this.info = info;
    }

    public int getType()
    {
        return type;
    }

    public byte[] getHash()
    {
        return hash;
    }

    public byte[] getData()
    {
        return data;
    }

    public Path getPath()
    {
        return path;
    }

    public String getInfo()
    {
        return info;
    }
}
