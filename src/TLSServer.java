import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.security.cert.X509Certificate;

/**
 * TLSServer [port]
 */
public class TLSServer
{
    String sKsPath, sKsPass, sKeyPass, strustPath, strustPass;
    int port;
    Thread shutdownHook; // to be registered with JVM shutdown hook
    boolean shouldRun;

    public TLSServer(int port, String sKsPath, String sKsPass, String sKeyPass, String strustPath, String strustPass)
    {
        shouldRun = true;
        this.port = port;
        this.sKsPath = sKsPath;
        this.sKsPass = sKsPass;
        this.sKeyPass = sKeyPass;
        this.strustPath = strustPath;
        this.strustPass = strustPass;
    }

    public void run()
    {
        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", sKsPath, sKsPass, sKeyPass, strustPath, strustPass);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = null;
        try {
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        } catch (IOException e) {
            System.out.println("Cannot create SSLServerSocket at port " + port);
            System.exit(1); // exit, no need to proceed
        }
        sslServerSocket.setNeedClientAuth(true); // mutual authentication

        FileAccess fileAccess = new FileAccess(); // initialize file access control

        shutdownHook = new ServerShutdownHook(sslServerSocket, null, fileAccess, this); // add socket to the hook
        Runtime.getRuntime().addShutdownHook(shutdownHook); // register the hook
        SSLSocket socket = null;

        while (shouldRun) { // outer while loop, on the level of SSLServerSocket
            try {
                socket = (SSLSocket) sslServerSocket.accept();
                ((ServerShutdownHook) shutdownHook).setSSLSocket(socket); // add socket to the hook
                socket.startHandshake();
                X509Certificate cert = socket.getSession().getPeerCertificateChain()[0];
                ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
                while (shouldRun) // inner while loop, on the level of SSLSocket
                    ServerHandler.handles(objIn, objOut, cert, fileAccess);
            } catch (IOException e) {
                System.out.println("Socket (to client) failed, exiting");
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                        System.out.println("finally: sslSocket closed");
                    }
                } catch (IOException e) {
                    System.out.println("Failed to close socket(s)");
                }
                System.out.println("Last client session ended, wait for the next one");
            }
        }
    }

    public static void main(String[] args) throws Exception
    {
        TLSServer server = new TLSServer(Integer.parseInt(args[0]), args[1], args[2], args[3], args[4], args[5]);
        server.run();
    }
}
