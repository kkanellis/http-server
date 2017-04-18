package ce325.hw2.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * A Directories-first comparator class
 * That is directories are considered before files
 */
public class DirectoriesFirstComparator implements Comparator<Path> {
    public int compare(Path path1, Path path2) {
        if (Files.isDirectory(path1) && Files.isDirectory(path2)) {
            return path1.compareTo(path2);
        }

        if (Files.isDirectory(path1) || Files.isDirectory(path2) ) {
            return Files.isDirectory(path1) ? -1 : 1;
        }

        // both are files
        return path1.compareTo(path2);
    }
}
