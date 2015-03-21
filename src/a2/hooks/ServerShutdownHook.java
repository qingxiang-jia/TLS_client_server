package a2.hooks;

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

    public ServerShutdownHook(SSLServerSocket sslServerSocket, SSLSocket sslSocket)
    {
        this.sslServerSocket = sslServerSocket;
        this.sslSocket = sslSocket;
    }

    public void run()
    {
        if (sslSocket != null)
            try {
                sslSocket.close();
            } catch (IOException e) {
                System.out.println("Cannot close sslSocket");
            }
        if (sslServerSocket != null)
            try {
                sslServerSocket.close();
            } catch (IOException e) {
                System.out.println("Cannot close sslServerSocket");
            }
    }

    public void setSSLSocket(Socket sslSocket)
    {
        this.sslSocket = sslSocket;
    }
}
