import java.io.File;
import java.io.Serializable;

/**
 * I hate CLIC lab using Java 6
 * Always stores absolute and relative path.
 * If you don't check path, it implies you want relative path, so, the path will stay as it is.
 * If you choose to check path, it implies you want absolute path. It will check the format of
 * the path and whether the referenced file exists. Also, if the path comes as relative path,
 * it will make it absolute based on where the program runs.
 *  Definition:
 *      Relative path: dir1/dir2/a.txt
 *      Absolute path: /dir1/dir2/a.txt
 */
public class Path implements Serializable
{
    String[] absolutePath = null;
    String[] relativePath = null;

    // if don't want client to check path, set doCheckPath to false
    public Path(String path, boolean doCheckPath) throws IllegalFilePathException
    {
        String fullPath;
        if (doCheckPath && !path.startsWith("/")) { // relative path, make it absolute
            String prefix = this.getClass().getClassLoader().getResource("").getPath();
            fullPath = prefix + path;
        } else if (!doCheckPath && !path.startsWith("/"))
            fullPath = path;
        else // relative path can't start with /
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
            relativePath = new String[elements.length];
            for (int i = 0; i < elements.length; i++)
                relativePath[i] = elements[i];
        }

    }

    public String getFileName()
    {
        if (absolutePath != null)
            return absolutePath[absolutePath.length - 1];
        else if (relativePath != null)
            return relativePath[relativePath.length - 1];
        else return null;
    }

    // output absolute path if not null, otherwise relative path
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
            sb.delete(sb.length()-1, sb.length()); // remove trailing /
        }
        return sb.toString();
    }

    public boolean checkPath(String fullPath)
    {
        File file = new File(fullPath);
        return file.isFile();
    }

    public static void main(String args[]) throws IllegalFilePathException
    {
        Path path = new Path("Path.java", false);
        System.out.println(path.getFileName());
        System.out.println(path.toString());
    }
}
