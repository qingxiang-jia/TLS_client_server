import java.io.IOException;
import java.net.Socket;

/**
 * Ensures that client terminates nicely when CTRL+C is hit.
 * Closes client socket.
 * It is a thread that will only run when JVM captures "CTRL+C".
 */
public class ClientShutdownHook extends Thread
{
    Socket socketOfClient; // the socket to be taken care of

    /**
     * Constructor
     * @param socketOfClient The socket to be taken care of
     */
    public ClientShutdownHook(Socket socketOfClient)
    {
        this.socketOfClient = socketOfClient;
    }

    /**
     * Closes socket.
     */
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
