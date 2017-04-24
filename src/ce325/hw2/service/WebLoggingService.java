package ce325.hw2.service;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by georgetg on 4/24/2017.
 */
public class WebLoggingService {
    private static WebLoggingService ourInstance = new WebLoggingService();

    public static WebLoggingService getInstance() {
        return ourInstance;
    }

    private Path mAccessPath;
    private Path mErrorPath;

    private WebLoggingService() {
        // default paths, just in case
        mAccessPath = Paths.get("access.log");
        mErrorPath = Paths.get("error.log");
    }

    public synchronized void logError() {

    }

    public synchronized void logAccess() {

    }

    /**
     * Set the path for the access log file
     * @param path path to file
     */
    public void setAccessLogPath(Path path) {
        mAccessPath = path;
    }


    /**
     * Set the path for the error log file
     * @param path path to file
     */
    public void setErrorLogPath(Path path) {
        mErrorPath = path;
    }

}
