package a2.cleaner;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.Socket;

/**
 * Ensures that server terminates nicely when ctrl+C is hit.
 * Closes SSLSocket and SSLServerSocket.
 */
public class ServerShutdown
{
    Socket SSLSocket;
    SSLServerSocket sslServerSocket;

    public ServerShutdown(SSLServerSocket sslServerSocket, SSLSocket sslSocket)
    {
        this.sslServerSocket = sslServerSocket;
        this.SSLSocket = sslSocket;
    }

    public void run()
    {
        if (SSLSocket != null)
            try {
                SSLSocket.close();
                System.out.println("Sockets closed");
            } catch (IOException e) {
                System.out.println("Cannot close socket(s)");
            }
    }
}
