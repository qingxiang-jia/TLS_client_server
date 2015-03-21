import javax.net.ssl.*;
import java.io.*;

/**
 * TLSServer [port]
 */
public class TLSServer
{
    String sKsPath, sKsPass, sKeyPass, strustPath, strustPass;
    int port;
    Thread shutdownHook; // to be registered with JVM shutdown hook

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

        shutdownHook = new ServerShutdownHook(sslServerSocket, null); // add socket to the hook
        Runtime.getRuntime().addShutdownHook(shutdownHook); // register the hook

        while (true) // for now, it's an echo server
        {
            SSLSocket socket = (SSLSocket) sslServerSocket.accept();
            ((ServerShutdownHook) shutdownHook).setSSLSocket(socket); // add socket to the hook
            socket.startHandshake();
            try {
                ServerHandler.handles(socket.getInputStream(), socket.getOutputStream());


//                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//
//                writer.println("Welcome~, enter exit to leave.");
//                String s;
//                while ((s = reader.readLine()) != null && !s.trim().equalsIgnoreCase("exit")) {
//                    writer.println("Echo: " + s);
//                }
//                writer.println("Bye~");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    System.out.println("finally: sslSocket closed");
                    sslServerSocket.close();
                    System.out.println("finally: sslServerSocket closed");
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
