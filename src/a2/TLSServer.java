package a2;

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
                sKsPass = "Sstorepass",
                sKeyPass = "skeypass",
                strustPath = "/Users/lee/Dropbox/NS/assn2/programming/strust.ks",
                strustPass = "struststorepass";

        KeyStore sKs = KeyStore.getInstance("JKS");
        sKs.load(new FileInputStream(sKsPath), sKsPass.toCharArray());

        KeyStore strustKs = KeyStore.getInstance("JKS");
        strustKs.load(new FileInputStream(strustPath), strustPass.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(sKs, sKeyPass.toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(strustKs);


        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        sslServerSocket.setNeedClientAuth(true);

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
}
