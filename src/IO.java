import java.io.*;

/**
 * Handles all operations related to file, such as reading a file, writing a file, and de/serialization.
 */
public class IO
{
    /**
     * Reads in a file, and return its byte array representation.
     * Java 1.7+ built-in class can handle this but CLIC machines have only JVM 1.6.
     * As its name suggests, it throws exception if there is one.
     *
     * @param path Path to file to be read
     * @return Byte array that contains all bytes of the file.
     */
    public static byte[] readFileThrowsException(String path) throws IOException
    {
        File file = new File(path);
        FileInputStream stream = null;
        byte[] fileInBytes = new byte[(int) file.length()];
        try {
            stream = new FileInputStream(file);
            int count = stream.read(fileInBytes);
            System.out.println("Read in " + count + " bytes");
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                System.out.println("Failed to close stream.");
            }
        }
        return fileInBytes;
    }

    /**
     * Reads in a file, and return its byte array representation.
     * Java 1.7+ built-in class can handle this but CLIC machines have only JVM 1.6.
     *
     * @param path Path to file to be read
     * @return Byte array that contains all bytes of the file.
     */
    public static byte[] readFile(String path)
    {
        File file = new File(path);
        FileInputStream stream = null;
        byte[] fileInBytes = new byte[(int) file.length()];
        try {
            stream = new FileInputStream(file);
            int count = stream.read(fileInBytes);
            System.out.println("Read in " + count + " bytes");
        } catch (FileNotFoundException e) {
            System.out.println("File " + path + " not found.");
        } catch (IOException e) {
            System.out.println("Cannot read " + path + ".");
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                System.out.println("Failed to close stream.");
            }
        }
        return fileInBytes;
    }

    /**
     * Writes data into a file.
     *
     * @param data     Byte array contains data
     * @param fileName File name
     */
    public static void writeFile(byte[] data, String fileName)
    {
        File file = new File(fileName);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            stream.write(data);
            System.out.println(data.length + " written");
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " not found.");
        } catch (IOException e) {
            System.out.println("Failed to write data to file " + fileName);
        } finally {
            try {
                if (stream != null)
                    stream.close();
            } catch (IOException e) {
                System.out.println("Failed to close stream.");
            }
        }
    }

    public static void serialize(String path, Object obj)
    {
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;
        try {
            fileOut = new FileOutputStream(path);
            objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Failed to serialize");
        } finally {
            try {
                if (objOut != null)
                    objOut.close();
                if (fileOut != null)
                    fileOut.close();
            } catch (IOException e) {
                System.out.println("Failed to close file &/ object stream");
            }
        }
    }

    public static Object deserialize(String path)
    {
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;
        Object obj = null;
        try {
            fileIn = new FileInputStream(path);
            objIn = new ObjectInputStream(fileIn);
            obj = objIn.readObject();
        } catch (IOException e) {
            System.out.println("Failed to serialize");
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // which never happens
        } finally {
            try {
                if (objIn != null)
                    objIn.close();
                if (fileIn != null)
                    fileIn.close();
            } catch (IOException e) {
                System.out.println("Failed to close file &/ object stream");
            }
        }
        return obj;
    }
}
