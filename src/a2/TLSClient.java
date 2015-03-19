package a2;

import a2.net.Auth;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;

/**
 * a2.TLSClient [server ip] [server port]
 */
public class TLSClient
{
    public void run(String sIP, int sPort) throws Exception
    {
        String cKsPath = "/Users/lee/Dropbox/NS/assn2/programming/c.ks",
                cKsPass = "cstorepass",
                cKeyPass = "ckeypass",
                ctrustPath = "/Users/lee/Dropbox/NS/assn2/programming/ctrust.ks",
                ctrustPass = "ctruststorepass";

        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", cKsPath, cKsPass, cKeyPass, ctrustPath, ctrustPass);

        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        Socket socket = socketFactory.createSocket(sIP, sPort);

        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        send("hello from client", out);
        send("exit", out);
        receive(in);
        socket.close();
    }

    public static void send(String s, PrintWriter out) throws IOError
    {
        System.out.println("Sending: " + s);
        out.println(s);
    }

    public static void receive(BufferedReader in) throws IOException
    {
        String s;
        while ((s = in.readLine()) != null)
        {
            System.out.println("Received: " + s);
        }
    }

    public static void main(String[] args) throws Exception
    {
        TLSClient client = new TLSClient();
        client.run(args[0], Integer.parseInt(args[1]));
    }
}
