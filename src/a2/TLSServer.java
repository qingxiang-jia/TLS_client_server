package a2;

import a2.net.Auth;

import javax.net.ssl.*;
import java.io.*;

/**
 * a2.TLSServer [port]
 */
public class TLSServer
{
    String sKsPath, sKsPass, sKeyPass, strustPath, strustPass;
    int port;

    public TLSServer(int port, String sKsPath, String sKsPass, String sKeyPass, String strustPath, String strustPass)
    {
        this.port = port;
        this.sKsPath = sKsPath;
        this.sKsPass = sKsPass;
        this.sKeyPass = sKeyPass;
        this.strustPath = strustPath;
        this.strustPass = strustPass;
    }

    public void run() throws Exception
    {
        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", sKsPath, sKsPass, sKeyPass, strustPath, strustPass);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        sslServerSocket.setNeedClientAuth(true); // mutual authentication

        while (true) // for now, it's an echo server
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
        TLSServer server = new TLSServer(Integer.parseInt(args[0]), args[1], args[2], args[3], args[4], args[5]);
        server.run();
    }
}
