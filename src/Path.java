import java.io.File;
import java.io.Serializable;

/**
 * Represents path of a file.
 *  Definition:
 *      Relative path: dir1/dir2/a.txt
 *      Absolute path: /dir1/dir2/a.txt
 *  Illegal examples:
 *      dir/        no file name
 *      /dir1/dir2/ no file name
 *      Any formats not listed in definition section
 */
public class Path implements Serializable
{
    /* each directory name is an element of the arrays, "/" is not stored
     e.g. path /dir1/dir2/file is stored as ["dir1", "dir2", "file"] */
    String[] absolutePath = null;
    String[] relativePath = null;

    /**
     * Constructor
     * @param path A String object represents a path to a file (see "Definition")
     * @param doCheckPath True = will check if path is legal AND if file exists; else False
     * @throws IllegalFilePathException
     */
    public Path(String path, boolean doCheckPath) throws IllegalFilePathException
    {
        /** accommodate ./ as beginning of relative path **/
        if (path.startsWith("./"))
            path = path.substring(2, path.length());

        String fullPath;
        if (doCheckPath && !path.startsWith("/")) { // relative path, make it absolute
            String prefix = this.getClass().getClassLoader().getResource("").getPath(); // get path prefix
            fullPath = prefix + path;
            System.out.println(fullPath);
        } else if (!doCheckPath)
            fullPath = path;
        else if (doCheckPath && path.startsWith("/")) { // absolute path
            fullPath = path;
        } else // relative path can't start with /
            throw new IllegalFilePathException();

        /** check if path is valid (good path + file exists) input **/
        if (doCheckPath && !checkPath(fullPath))
            throw new IllegalFilePathException();

        /** all good, save the path **/
        String[] elements = fullPath.split("/");
        if (doCheckPath) { // client's put request
            absolutePath = new String[elements.length - 1];
            for (int i = 1; i < elements.length; i++)
                absolutePath[i-1] = elements[i];
        } else { // client's get request, server's put/get response doesn't create new path, so irrelevant here
            if (fullPath.startsWith("/")) { // dealing with absolute path
                absolutePath = new String[elements.length - 1];
                for (int i = 1; i < elements.length; i++)
                    absolutePath[i-1] = elements[i];
            } else { // dealing with relative path
                relativePath = new String[elements.length];
                for (int i = 0; i < elements.length; i++)
                    relativePath[i] = elements[i];
            }
        }
    }

    /**
     * Get file name from a path.
     * @return File name
     */
    public String getFileName()
    {
        if (absolutePath != null) // absolute path has the priority
            return absolutePath[absolutePath.length - 1];
        else if (relativePath != null)
            return relativePath[relativePath.length - 1];
        else return null;
    }

    /**
     * Outputs absolute path if not null, otherwise relative path
     * @return File path in String
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if (absolutePath != null) {
            for (String dir : absolutePath) {
                sb.append("/");
                sb.append(dir);
            }
        } else if (relativePath != null) {
            for (String dir : relativePath) {
                sb.append(dir);
                sb.append("/");
            }
            sb.delete(sb.length()-1, sb.length()); // remove trailing "/"
        }
        return sb.toString();
    }

    /**
     * Check if path is legal AND is file exists.
     * @param fullPath A String object represents the path to the file
     * @return Whether the path is legal
     */
    public static boolean checkPath(String fullPath)
    {
        File file = new File(fullPath);
        return file.isFile();
    }
}
