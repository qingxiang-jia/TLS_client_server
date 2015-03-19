package a2;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

/**
 * a2.TLSServer [port]
 */
public class TLSServer
{
    public void run(int port)
    {
        char[] passphrase = "coms4180pwd".toCharArray();
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("/Users/lee/Dropbox/NS/assn2/programming/client_keystore.jks"), passphrase);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, passphrase);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), null, null);
            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port);
            printServerSocketInfo(serverSocket);
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            printSocketInfo(socket);
            BufferedWriter w = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream()));
            BufferedReader r = new BufferedReader( new InputStreamReader( socket.getInputStream()));
            String m = "hello from server";
            w.write(m, 0, m.length());
            w.newLine();
            w.flush();
            while ((m=r.readLine())!= null) {
                if (m.equals(".")) break;
                char[] a = m.toCharArray();
                int n = a.length;
                for (int i=0; i<n/2; i++) {
                    char t = a[i];
                    a[i] = a[n-1-i];
                    a[n-i-1] = t;
                }
                w.write(a,0,n);
                w.newLine();
                w.flush();
            }
            w.close();
            r.close();
            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        TLSServer server = new TLSServer();
        server.run(Integer.parseInt(args[0]));
    }

    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: "+s.getClass());
        System.out.println("   Remote address = "
                +s.getInetAddress().toString());
        System.out.println("   Remote port = "+s.getPort());
        System.out.println("   Local socket address = "
                +s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                +s.getLocalAddress().toString());
        System.out.println("   Local port = "+s.getLocalPort());
        System.out.println("   Need a2.client authentication = "
                +s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = "+ss.getCipherSuite());
        System.out.println("   Protocol = "+ss.getProtocol());
    }
    private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: "+s.getClass());
        System.out.println("   Socker address = "
                +s.getInetAddress().toString());
        System.out.println("   Socker port = "
                +s.getLocalPort());
        System.out.println("   Need a2.client authentication = "
                +s.getNeedClientAuth());
        System.out.println("   Want a2.client authentication = "
                +s.getWantClientAuth());
        System.out.println("   Use a2.client mode = "
                +s.getUseClientMode());
    }
}
