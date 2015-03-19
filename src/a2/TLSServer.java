package a2;

import a2.net.Auth;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

/**
 * a2.TLSServer [port]
 */
public class TLSServer
{
    public void run(int port) throws Exception
    {
        String sKsPath = "/Users/lee/Dropbox/NS/assn2/programming/s.ks",
                sKsPass = "sstorepass",
                sKeyPass = "skeypass",
                strustPath = "/Users/lee/Dropbox/NS/assn2/programming/strust.ks",
                strustPass = "struststorepass";

        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", sKsPath, sKsPass, sKeyPass, strustPath, strustPass);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        sslServerSocket.setNeedClientAuth(true); // mutual authentication

        while (true)
        {
            SSLSocket socket = (SSLSocket) sslServerSocket.accept();
            socket.startHandshake();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                writer.println("Welcome~, enter exit to leave.");
                String s;
                while ((s = reader.readLine()) != null && !s.trim().equalsIgnoreCase("exit")) {
                    writer.println("Echo: " + s);
                }
                writer.println("Bye~");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        TLSServer server = new TLSServer();
        server.run(Integer.parseInt(args[0]));
    }
}
