package a2;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

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

        KeyStore cKs = KeyStore.getInstance("JKS");
        cKs.load(new FileInputStream(cKsPath), cKsPass.toCharArray());

        KeyStore ctrustKs = KeyStore.getInstance("JKS");
        ctrustKs.load(new FileInputStream(ctrustPath), ctrustPass.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(cKs, cKsPass.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ctrustKs);


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

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
