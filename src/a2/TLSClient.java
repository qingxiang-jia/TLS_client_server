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
    String cKsPath, cKsPass, cKeyPass, ctrustPath, ctrustPass;
    String sIP;
    int sPort;

    public TLSClient(String sIP, int sPort,
                     String cKsPath, String cKsPass, String cKeyPass, String ctrustPath, String ctrustPass)
    {
        this.sIP = sIP;
        this.sPort = sPort;
        this.cKsPath = cKsPath;
        this.cKsPass = cKsPass;
        this.cKeyPass = cKeyPass;
        this.ctrustPath = ctrustPath;
        this.ctrustPass = ctrustPass;
    }

    public void run() throws Exception
    {
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
        TLSClient client = new TLSClient(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], args[5], args[6]);
        client.run();
    }
}
