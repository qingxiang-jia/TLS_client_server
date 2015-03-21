import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

        try {
            SSLSocket socket = (SSLSocket) sslServerSocket.accept();
            ((ServerShutdownHook) shutdownHook).setSSLSocket(socket); // add socket to the hook
            socket.startHandshake();
            ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
            while (true)
                ServerHandler.handles(objIn, objOut);
        } finally {
            try {
//                socket.close();
//                System.out.println("finally: sslSocket closed");
                sslServerSocket.close();
                System.out.println("finally: sslServerSocket closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static void main(String[] args) throws Exception
    {
        TLSServer server = new TLSServer(Integer.parseInt(args[0]), args[1], args[2], args[3], args[4], args[5]);
        server.run();
    }
}
