package client;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.security.KeyStore;

/**
 * TLSClient [server ip] [server port]
 */
public class TLSClient
{
    public void run(String servIP, int servPort)
    {
        SSLSocketFactory factory = null;
        try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "coms4180pwd".toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream("/Users/lee/Dropbox/NS/assn2/programming/client_keystore.jks"), passphrase);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null); // <-- null null?

            factory = ctx.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }

        SSLSocket socket = null;
        try {
            socket = (SSLSocket) factory.createSocket(servIP, servPort);
            socket.startHandshake();

            PrintWriter out = new PrintWriter(new BufferedWriter( new OutputStreamWriter( socket.getOutputStream())));
            out.println("hello from client");
            out.flush();

            if (out.checkError()) System.out.println("SSLSocketClient: java.io.PrintWriter error");

            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null)
                System.out.println(inputLine);

            in.close();
            out.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
