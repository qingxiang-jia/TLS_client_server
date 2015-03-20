package a2;

import a2.cleaner.ClientShutdown;
import a2.logic.ClientLogic;
import a2.net.Auth;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.UnknownHostException;

/**
 * a2.TLSClient [server ip] [server port]
 */
public class TLSClient
{
    String cKsPath, cKsPass, cKeyPass, ctrustPath, ctrustPass;
    String sIP;
    int sPort;
    Thread clientShutdown; // to be registered with JVM shutdown hook

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

    public void run()
    {
        SSLContext sslContext = Auth.getSSLContext("TLS", "JKS", cKsPath, cKsPass, cKeyPass, ctrustPath, ctrustPass);
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        SSLSocket socket = null;
        ClientLogic logic = null;
        try {
            socket = (SSLSocket) socketFactory.createSocket(sIP, sPort);

            clientShutdown = new ClientShutdown(socket); // so that the socket can be closed nicely upon termination
            Runtime.getRuntime().addShutdownHook(clientShutdown);

            socket.startHandshake();
            logic = new ClientLogic(socket.getInputStream(), socket.getOutputStream());
            logic.perform(); // all user interactions are handled here
        } catch (UnknownHostException e) {
            System.out.println("Host " + sIP + " unreachable");
        } catch (IOException e) {
            System.out.println("Cannot create SSL socket");
        } finally { // when exception happens close socket
            if (socket != null)
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Cannot close socket");
                }
        }
    }

    public static void main(String[] args) throws Exception
    {
        TLSClient client = new TLSClient(args[0], Integer.parseInt(args[1]), args[2], args[3], args[4], args[5], args[6]);
        client.run();
    }
}
