package ce325.hw2.util;

/**
 * Created by georgetg on 25/3/2017.
 */
public class Logger {
    private static Logger ourInstance = new Logger();

    public static Logger getInstance() {
        return ourInstance;
    }

    private Logger() {
    }
}
