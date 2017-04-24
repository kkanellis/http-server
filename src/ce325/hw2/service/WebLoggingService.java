package ce325.hw2.service;

import ce325.hw2.http.HttpStatusCodes;
import ce325.hw2.util.Logger;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    private Logger logger = Logger.getInstance();
    private final static String lineSep = System.getProperty("line.separator");

    private WebLoggingService() {
        // default paths, just in case
        mAccessPath = Paths.get("access.log");
        mErrorPath = Paths.get("error.log");
    }

    public synchronized void error(HttpExchange exchange, String trace) {
        String requestUri = "";
        String IpAddress = exchange.getRemoteAddress().getHostString();
        final String sep = "---------------------";
        try {
            requestUri = URLDecoder.decode(exchange.getRequestURI().toString(), "UTF-8");
            StringBuilder sb = new StringBuilder();
            sb.append(sep + lineSep);
            sb.append(IpAddress + new Date().toString() + lineSep);
            sb.append(requestUri + lineSep);
            sb.append(stringifyHeaders(exchange.getRequestHeaders()) + lineSep);
            sb.append(trace + lineSep);
            sb.append(sep + lineSep);
            Files.write(mErrorPath, sb.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
        } catch (UnsupportedEncodingException ex) {
            logger.error("Unsupported encoding!");
        } catch (IOException ex) {
            logger.error("Cannot write to access file!");
        }
    }

    public synchronized void access(HttpExchange exchange, int code) {
        String requestUri = "";
        String IpAddress = exchange.getRemoteAddress().getHostString();
        try {
            requestUri = URLDecoder.decode(exchange.getRequestURI().toString(), "UTF-8");
            String agent = exchange.getRequestHeaders().getFirst("user-agent");
            // IP Address - Ημερομηνία Σύνδεσης - Request URL -> Response Code - User-Agent HTTP Request Header
            String line = String.format("%s - %s - %s -> [%d] %s - %s%s",
                    IpAddress, new Date().toString(), requestUri,
                    code, HttpStatusCodes.describe(code), agent,
                    System.getProperty("line.separator"));
            Files.write(mAccessPath, line.getBytes("UTF-8"), StandardOpenOption.APPEND);

        } catch (UnsupportedEncodingException ex) {
            logger.error("Unsupported encoding!");
        } catch (IOException ex) {
            logger.error("Cannot write to access file!");
        }
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


    private String stringifyHeaders(Map<String, List<String>> map) {
        StringBuilder sb = new StringBuilder("Headers: " + lineSep);
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            sb.append(entry.getKey() + ": ");  // header name
            sb.append(String.join(",", entry.getValue()));
            sb.append(lineSep);
        }

        return sb.toString();
    }
}
