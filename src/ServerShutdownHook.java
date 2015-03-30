import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;

/**
 * Ensures that server terminates nicely when CTRL+C is hit.
 * Closes sslSocket and SSLServerSocket.
 */
public class ServerShutdownHook extends Thread
{
    Socket sslSocket; // the socket connecting to client
    SSLServerSocket sslServerSocket; // the server socket that returns sslSocket
    TLSServer tlsServer; // reference to the server

    /**
     * Constructor
     * @param sslServerSocket An SSLServerSocket object that accepts incoming connections
     * @param sslSocket An SSLSocket object that connecting to client
     * @param tlsServer A TLSServer object that is going to terminate
     */
    public ServerShutdownHook(SSLServerSocket sslServerSocket, SSLSocket sslSocket, TLSServer tlsServer)
    {
        this.sslServerSocket = sslServerSocket;
        this.sslSocket = sslSocket;
        this.tlsServer = tlsServer;
    }

    /**
     * Runs and closes sslServerSocket and sslSocket when the server is about to be terminated by the user
     */
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

    /**
     * Setter for sslSocket
     * The idea is each time the server connects to a client, there is a different SSLSocket object,
     * so, providing the setter so that this shutdown hook always have the latest reference to sslSocket.
     * @param sslSocket An SSLSocket object that connects the server and client
     */
    public void setSSLSocket(Socket sslSocket)
    {
        this.sslSocket = sslSocket;
    }
}
