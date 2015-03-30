import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;

/**
 * Ensures that server terminates nicely when ctrl+C is hit.
 * Closes sslSocket and SSLServerSocket.
 */
public class ServerShutdownHook extends Thread
{
    Socket sslSocket;
    SSLServerSocket sslServerSocket;
    TLSServer tlsServer;

    public ServerShutdownHook(SSLServerSocket sslServerSocket, SSLSocket sslSocket, TLSServer tlsServer)
    {
        this.sslServerSocket = sslServerSocket;
        this.sslSocket = sslSocket;
        this.tlsServer = tlsServer;
    }

    public void run()
    {
        tlsServer.shouldRun = false; // so the server stops actively listening to new requests
        if (sslServerSocket != null)
            try {
                sslServerSocket.close();
            } catch (IOException e) {
                System.out.println("Cannot close sslServerSocket");
            }
        if (sslSocket != null)
            try {
                sslSocket.close();
            } catch (IOException e) {
                System.out.println("Cannot close sslSocket");
            }
    }

    public void setSSLSocket(Socket sslSocket)
    {
        this.sslSocket = sslSocket;
    }
}
