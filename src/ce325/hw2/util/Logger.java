package ce325.hw2.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Simple logging class
 * TODO: maybe add colors (if is portable across os)
 */
public class Logger {
    private static Logger ourInstance = new Logger();
    private LocalDateTime created;

    public static Logger getInstance() {
        return ourInstance;
    }

    private Logger() {
        created = LocalDateTime.now();
    }

    public void debug(String s) {
        this.print("DEBUG", s);
    }

    public void info(String s) {
        this.print("INFO", s);
    }

    public void warn(String s) {
        this.print("WARN", s);
    }

    public void error(String s) {
        this.print("ERROR", s);
    }

    /**
     * Prints to stdout the given msg along with the its type and the elapsed time
     * @param type one of the strings "DEBUG", "INFO", "WARNING", "ERROR"
     * @param msg the message to print
     */
    private void print(String type, String msg) {
        double elapsed = ((double)created.until(LocalDateTime.now(), ChronoUnit.MILLIS)) / 1000.0;
        System.out.println(
            String.format("[%7.3f][%5s] %s", elapsed, type, msg)
        );
    }
}
