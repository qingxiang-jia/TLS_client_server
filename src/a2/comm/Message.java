package a2.comm;

import java.io.Serializable;

/**
 * Represents the data sent between server and client.
 */
public class Message implements Serializable
{
    public static final int TEXT = 0;
    public static final int PLAIN_DATA = 1;
    public static final int ENCRYPT_DATA = 2;

    int type;
    String text;
    byte[] data, hash;

    public Message(int type, String text, byte[] hash)
    {
        this.type = type;
        this.text = text;
        this.hash = hash;
    }

    public Message(int type, byte[] data, byte[] hash)
    {
        this.type = type;
        this.data = data;
        this.hash = hash;
    }

    public int getType()
    {
        return type;
    }

    public String getText()
    {
        return text;
    }

    public byte[] getHash()
    {
        return hash;
    }

    public byte[] getData()
    {
        return data;
    }
}
