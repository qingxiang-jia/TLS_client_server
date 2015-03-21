import java.io.File;
import java.io.Serializable;

/**
 * I hate CLIC lab using Java 6
 * Always stores absolute path.
 *  Definition:
 *      Relative path: dir1/dir2/a.txt
 *      Absolute path: /dir1/dir2/a.txt
 */
public class Path implements Serializable
{
    String[] filePath;

    public Path(String path) throws IllegalFilePathException
    {
        String fullPath;
        if (!path.startsWith("/")) { // relative path, make it absolute
            String prefix = this.getClass().getClassLoader().getResource("").getPath();
            fullPath = prefix + path;
        } else
            fullPath = path;

        /** check if path is a garbage input **/
        if (!checkPath(fullPath))
            throw new IllegalFilePathException();

        /** all good, save the path **/
        String[] elements = fullPath.split("/");
        filePath = new String[elements.length - 1];
        for (int i = 1; i < elements.length; i++)
            filePath[i-1] = elements[i];
    }

    public String getFileName()
    {
        return filePath[filePath.length - 1];
    }

    // always outputs absolute path
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (String dir : filePath) {
            sb.append("/");
            sb.append(dir);
        }
        return sb.toString();
    }

    private boolean checkPath(String fullPath)
    {
        File file = new File(fullPath);
        return file.isFile();
    }

    public static void main(String args[]) throws IllegalFilePathException
    {
        Path path = new Path("Path2.java");
        System.out.println(path.getFileName());
        System.out.println(path.toString());
    }
}
