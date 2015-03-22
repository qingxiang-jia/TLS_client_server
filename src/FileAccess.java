import java.io.Serializable;
import javax.security.cert.CertificateEncodingException;
import javax.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Ensures that each client can only access its own file.
 * Client's uniqueness is identified by its certificate,
 * using a map between the files accessible and the public
 * key provided in the certificate.
 */
@SuppressWarnings("unchecked")
public class FileAccess implements Serializable
{
    Map<String, byte[]> accessTable;
    String defaultTablePath = "access_table";

    public FileAccess() // if access_table exists, read it in, otherwise create new
    {
        if (Path.checkPath(defaultTablePath)) {
            accessTable = (Map<String, byte[]>) IO.deserialize(defaultTablePath);
            System.out.println("Found existing access table, loaded");
        } else {
            accessTable = new HashMap<String, byte[]>();
            System.out.println("No access table, created new");
        }
    }

    public boolean checkAccess(String path, X509Certificate cert)
    {
        try {
            if (!accessTable.containsKey(path))
                return false;
            else if (!Arrays.equals(cert.getEncoded(), accessTable.get(path)))
                return false;
            else
                return true;
        } catch (CertificateEncodingException e) {
            System.out.println("Client's certificate has an invalid encoding");
            return false;
        }
    }

    public boolean updateAccess(String path, X509Certificate cert)
    {
        try {
            accessTable.put(path, cert.getEncoded());
            return true;
        } catch (CertificateEncodingException e) {
            System.out.println("Failed to update access table due to invalid certificate encoding");
            return false;
        }
    }

    // call when server quits
    public void saveAccessTable()
    {
        IO.serialize(defaultTablePath, accessTable);
    }
}
