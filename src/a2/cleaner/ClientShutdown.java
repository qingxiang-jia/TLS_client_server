package a2.cleaner;

import java.io.IOException;
import java.net.Socket;

/**
 * Ensures that client terminates nicely when ctrl+C is hit.
 * So far, it only closes client socket.
 */
public class ClientShutdown extends Thread
{
    Socket socketOfClient;

    public ClientShutdown(Socket socketOfClient)
    {
        this.socketOfClient = socketOfClient;
    }

    public void run()
    {
        if (socketOfClient != null)
            try {
                socketOfClient.close();
                System.out.println("Socket closed");
            } catch (IOException e) {
                System.out.println("Cannot close socket");
            }
    }
}
