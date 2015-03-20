package a2.handlers;

import java.io.*;

/**
 * Handles all client-related operations such as get and put.
 */
public class ClientHandler
{
    public static void handleGet(String path, InputStream netIn, OutputStream netOut)
    {
        PrintWriter out = new PrintWriter(netOut, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(netIn));
        out.println(path);
        receive(in); // this is blocking
    }

    public static void handleGet(String path, String password, InputStream netIn, OutputStream netOut)
    {

    }

    public static void handlePut(String path, InputStream netIn, OutputStream netOut)
    {

    }

    public static void handlePut(String path, String password, InputStream netIn, OutputStream netOut)
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
