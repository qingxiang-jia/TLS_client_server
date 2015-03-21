import java.util.Arrays;

/**
 * I hate CLIC lab using Java 6
 * Always stores absolute path.
 *  Definition:
 *      Relative path: dir1/dir2/a.txt
 *      Absolute path: /dir1/dir2/a.txt
 */
public class Path
{
    String[] filePath;

    public Path(String path)
    {
        String fullPath;
        if (!path.startsWith("/")) { // relative path, make it absolute
            String prefix = this.getClass().getClassLoader().getResource("").getPath();
            fullPath = prefix + path;
        } else
            fullPath = path;
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
}
