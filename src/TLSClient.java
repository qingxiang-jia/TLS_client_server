import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.UnknownHostException;

/**
 * This class is the client.
 * Arguments to run: java TLSClient [server_ip] [server_port] [path_keystore] [password_keystore] [password_key] [path_trust_store] [password_trust_store]
 * Notice: takes only absolute path; put password in " ".
 */
public class TLSClient
{
    String cKsPath, cKsPass, cKeyPass, ctrustPath, ctrustPass; // see constructor's @param
    String sIP; // server IP address
    int sPort; // server port number
    Thread shutdownHook; // to be registered with JVM shutdown hook

    /**
     * Constructor
     * @param sIP Server IP address (Can also be domain name, e.g. tokyo.clic.cs.columbia.edu .)
     * @param sPort Server port number
     * @param cKsPath Client keystore file path
     * @param cKsPass Client keystore password
     * @param cKeyPass Client key password (password to get client's private key)
     * @param ctrustPath Client trust store file path
     * @param ctrustPass Client trust store password
     */
    public TLSClient(String sIP, int sPort,
                     String cKsPath, String cKsPass, String cKeyPass, String ctrustPath, String ctrustPass)
    {
        this.sIP = sIP;
        this.sPort = sPort;
        this.cKsPath = cKsPath;
        this.cKsPass = cKsPass;
        this.cKeyPass = cKeyPass;
        this.ctrustPath = ctrustPath;
        this.ctrustPass = ctrustPass;
    }

    /**
     * Entry point for the client to run.
     */
    public void run()
    {
        /** setting up the TLS environment **/
        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", cKsPath, cKsPass, cKeyPass, ctrustPath, ctrustPass); // get SSLContext
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        SSLSocket socket = null;
        ClientLogic logic = null;
        try {
            socket = (SSLSocket) socketFactory.createSocket(sIP, sPort); /** connecting to server **/

            shutdownHook = new ClientShutdownHook(socket); // so that the socket can be closed nicely upon termination
            Runtime.getRuntime().addShutdownHook(shutdownHook); /** add shutdown hook **/

            socket.startHandshake(); /** begin authentication process (two ways) **/
            logic = new ClientLogic(socket.getInputStream(), socket.getOutputStream()); /** client logic takes the job from here **/
            logic.perform(); // all user interactions are handled here (communication to server handled by client handler, invoked by logic)
        } catch (UnknownHostException e) {
            System.out.println("Host " + sIP + " unreachable");
        } catch (IOException e) {
            System.out.println("Cannot create SSL socket, perhaps server is not running?");
        } finally { // when exception happens close socket
            System.out.println("Close socket");
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Cannot close socket");
                }
        }
    }

    /**
     * Checks if arguments are legal in format. If not, exit with error code 1.
     * @param args String[] object that is the program arguments
     */
    public static void paramCheck(String[] args)
    {
        String usage = "usage: java TLSClient [server_ip] [server_port] [path_keystore] [password_keystore] [password_key] [path_trust_store] [password_trust_store]";
        if (args.length != 7) {
            System.out.println("Number of arguments does not match." + usage);
            System.exit(1);
        } else if (!(ArgsCheck.isIPAddress(args[0]) || ArgsCheck.isDomainName(args[0]))) {
            System.out.println(args[0] + " is not valid IP address or domain name.\n" + usage);
            System.exit(1);
        } else if (!ArgsCheck.isPositiveInteger(args[1])) {
            System.out.println(args[1] + " is not valid port number.\n" + usage);
            System.exit(1);
        } else if (!Path.checkPath(args[2])) {
            System.out.println("Path " + args[2] + " is illegal and/or does not exist.\n" + usage);
            System.exit(1);
        } else if (!Path.checkPath(args[5])) {
            System.out.println("Path " + args[5] + " is illegal and/or does not exist.\n" + usage);
            System.exit(1);
        }
    }

    /**
     * Entry point of client program.
     */
    public static void main(String[] args) throws Exception
    {
        TLSClient.paramCheck(args); // arguments format check
        TLSClient client = new TLSClient(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], args[5], args[6]);
        client.run();
    }
}
