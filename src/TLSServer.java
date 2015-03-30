import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class is the server.
 * Arguments to run: java TLSServer [port_number] [path_keystore] [password_keystore] [password_key] [path_trust_store] [password_trust_store]
 * Notice: takes only absolute path; put password in " ".
 */
public class TLSServer
{
    String sKsPath, sKsPass, sKeyPass, strustPath, strustPass; // see constructor's @param
    int port; // port number
    Thread shutdownHook; // to be registered with JVM shutdown hook
    boolean shouldRun; // a flag tells whether the server should run

    /**
     * Constructor
     * @param port Port that server listens to
     * @param sKsPath Path to server's keystore
     * @param sKsPass Password for accessing server's keystore
     * @param sKeyPass Password for accessing server's private key
     * @param strustPath Path to server's trust store
     * @param strustPass Password for accessing server's trust store
     */
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

    /**
     * Entry point for server to run.
     */
    public void run()
    {
        /** setting up TLS environment **/
        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", sKsPath, sKsPass, sKeyPass, strustPath, strustPass); // get SSLContext

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        SSLServerSocket sslServerSocket = null;
        try {
            sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port); // constructing server socket
        } catch (IOException e) {
            System.out.println("Cannot create SSLServerSocket at port " + port);
            System.exit(1); // exit, no need to proceed
        }
        sslServerSocket.setNeedClientAuth(true); // mutual authentication (both client and server need to authenticate)

        shutdownHook = new ServerShutdownHook(sslServerSocket, null, this); // add socket to the hook
        Runtime.getRuntime().addShutdownHook(shutdownHook); // register the hook
        SSLSocket socket = null;

        /** ready to serve client **/
        /**
         * The idea of this outer while loop is that is will run as long as sslServerSocket is valid. If putting the
         * creation of sslServerSocket and socket (talks to client) in the same while loop, any exception raised from
         * socket will cause sslServerSocket to fail, thus server needs to be restarted. However, this while loop contains
         * the creation of socket (but not the creation of sslServerSocket). If anything associated with socket fails,
         * only socket will fail, but the server still hold up.
         */
        while (shouldRun) {
            try {
                socket = (SSLSocket) sslServerSocket.accept(); /** accepts client connection **/
                ((ServerShutdownHook) shutdownHook).setSSLSocket(socket); // add this socket to the hook
                socket.startHandshake(); /** begin authentication **/
                ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream()); // data from client
                ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream()); // data to client
                while (shouldRun) // inner while loop, client and server talks in term
                    ServerHandler.handles(objIn, objOut); /** ServerHandler takes the job from here **/
            } catch (IOException e) {
                System.out.println("Socket (to client) failed, exiting");
            } finally { // anything goes wrong, close socket
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

    /**
     * Checks if arguments are legal in format. If not, exit with error code 1.
     * @param args String[] object that is the program arguments
     */
    public static void paramCheck(String[] args)
    {
        String usage = "usage: java TLSServer [port_number] [path_keystore] [password_keystore] [password_key] [path_trust_store] [password_trust_store]";
        if (args.length != 6) {
            System.out.println("Number of arguments does not match." + usage);
            System.exit(1);
        } else if (!ArgsCheck.isPositiveInteger(args[0])) {
            System.out.println(args[0] + " is not valid port number.\n" + usage);
            System.exit(1);
        } else if (!Path.checkPath(args[1])) {
            System.out.println("Path " + args[1] + " is illegal and/or does not exist.\n" + usage);
            System.exit(1);
        } else if (!Path.checkPath(args[4])) {
            System.out.println("Path " + args[4] + " is illegal and/or does not exist.\n" + usage);
            System.exit(1);
        }
    }

    /**
     * Entry point of client program.
     */
    public static void main(String[] args) throws Exception
    {
        TLSServer.paramCheck(args); // checks if arguments are legal in format
        TLSServer server = new TLSServer(Integer.parseInt(args[0]), args[1], args[2], args[3], args[4], args[5]);
        server.run();
    }
}
