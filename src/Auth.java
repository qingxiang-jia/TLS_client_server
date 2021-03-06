import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Utility class: handles authentication related operations.
 * The idea is for both server and client, these operations are the same, it saves some work to
 * put them into the same place.
 */
public class Auth
{
    /**
     * Takes type (of keystore), path (of key store), and password for this keystore; constructs
     * a KeyStore object.
     * @param type Type of KeyStore object
     * @param path Where KeyStore object is stored
     * @param storePass Password for this KeyStore object
     * @return A KeyStore object
     */
    public static KeyStore getKeyStore(String type, String path, String storePass)
    {
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance(type);
            keystore.load(new FileInputStream(path), storePass.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("Cannot find keystore at " + path);
        } catch (IOException e) {
            System.out.println("Cannot load keystore at " + path);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Cannot find the selected algorithm: " + type);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return keystore;
    }

    /**
     * Constructs a KeyManagerFactory given a KeyStore object and its password.
     * @param store The KeyStore object
     * @param keyPass Password for the KeyStore object
     * @return A KeyManagerFactory object
     */
    public static KeyManagerFactory getKeyManagerFactory(KeyStore store, String keyPass)
    {
        KeyManagerFactory kmf = null;
        try {
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(store, keyPass.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Default algorithm for KeyManagerFactory not working");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            System.out.println("Key pass does not match key store");
        }
        return kmf;
    }

    /**
     * Constructs a TrustManagerFactory given a KeyStore object that represents trust store.
     * @param trustStore The KeyStore object that represents a trust store.
     * @return A TrustMangerFactory object
     */
    public static TrustManagerFactory getTrustManagerFactory(KeyStore trustStore)
    {
        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Default algorithm for TrustManagerFactory not working");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return tmf;
    }

    /**
     * Takes all essential elements of an SSLContext and constructs an SSLContext object.
     * @param contextType A String object that specifies the type of SSLContext
     * @param storeType A String object that specifies the type of KeyStore
     * @param keystorePath A String object represents the path to the keystore
     * @param keystorePass A String object represents the password for the keystore
     * @param keyPass A String object represents the password for a particular key in the keystore
     * @param truststorePath A String object represents the path to the trust store
     * @param truststorePass A String object represents the password for the trust store
     * @return An SSLContext object
     */
    public static SSLContext getSSLContext(String contextType, String storeType,
                                           String keystorePath, String keystorePass, String keyPass,
                                           String truststorePath, String truststorePass)
    {
        /** get keystore containing public and private key server/client **/
        KeyStore sKs = Auth.getKeyStore(storeType, keystorePath, keystorePass);
        /** get trust store containing client's/server's certificate **/
        KeyStore strustKs = Auth.getKeyStore(storeType, truststorePath, truststorePass);
        /** generate key and trust manager **/
        KeyManagerFactory kmf = Auth.getKeyManagerFactory(sKs, keyPass);
        TrustManagerFactory tmf = Auth.getTrustManagerFactory(strustKs);
        /** generate SSL context **/
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(contextType);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(contextType + " not supported");
        } catch (KeyManagementException e) {
            System.out.println("SSLContext initialization went wrong");
            e.printStackTrace();
        }
        return sslContext;
    }
}
