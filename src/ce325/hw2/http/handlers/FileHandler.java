package ce325.hw2.http.handlers;

import ce325.hw2.html.*;
import ce325.hw2.http.HttpStatusCodes;
import ce325.hw2.http.MIMETypes;
import ce325.hw2.io.Config;
import ce325.hw2.util.DirectoriesFirstComparator;
import ce325.hw2.util.Logger;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the HTTP requests
 */
public class FileHandler implements HttpHandler {
    private final int STREAM_BUFFER_SIZE = 1024;

    private Logger logger = Logger.getInstance();
    private String rootDir;

    public FileHandler(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws UnsupportedEncodingException {
        // Check if request method is not GET
        if ( !exchange.getRequestMethod().equalsIgnoreCase("GET") ) {
            sendStringResponse(
                exchange,
                HttpStatusCodes.HTTP_METHOD_NOT_ALLOWED,
                "Server supports only GET requests"
            );
        }

        // find requested absolute path
        Path filepath = Paths.get(
            rootDir,
            URLDecoder.decode(exchange.getRequestURI().toString(), "UTF-8")
        ).toAbsolutePath();

        logger.debug(
            String.format("Request for '%s'", filepath.toString())
        );

        // decide how to handle the request
        try {
            if (Files.isDirectory(filepath)) {
                //serveDir(exchange, filepath);
            } else if (Files.isRegularFile(filepath)) {
                serveFile(exchange, filepath);
            } else {
                sendStringResponse(
                    exchange,
                    HttpStatusCodes.HTTP_NOT_FOUND,
                    "Requested resource does not exist (or is symbolic link)"
                );
            }
        }
        catch (Exception ex) {
            logger.warn("handle: " + ex.getMessage());
            sendStringResponse(
                exchange,
                HttpStatusCodes.HTTP_SERVER_ERROR,
                "Internal server error"
            );
        }
    }

    /**
     * Serves a file to the client
     * @param exchange Request HttpExchange object
     * @param filepath Path object pointing to the file
     * @throws IOException
     */
    private void serveFile(HttpExchange exchange, Path filepath) throws IOException {
        logger.debug(String.format("serveFile: serving file '%s'", filepath.toString()));

        // find MIME type for file
        String filename = filepath.getFileName().toString();
        int fileExtPos = filename.lastIndexOf('.');

        String mimeType = (fileExtPos > 0) ?
                MIMETypes.getMIMEType( filename.substring(fileExtPos + 1) ) :
                MIMETypes.UNKNOWN_FILE_MIME_TYPE;

        // add 'Content-Type' header
        exchange.getResponseHeaders().add("Content-Type", mimeType);

        // send file to client
        File file = filepath.toFile();
        sendResponse(
            exchange,
            HttpStatusCodes.HTTP_OK,
            new FileInputStream(file),
            file.length()
        );
    }



    /**
     * Sends an HTTP response to the client
     *
     * @param exchange Request HttpExchange object
     * @param responseCode HTTP Status code
     * @param responseStream An InputStream from where the response will be read
     * @param responseLength Length (in bytes) of the response
     */
    private void sendResponse(HttpExchange exchange, int responseCode,
                              InputStream responseStream, long responseLength) {
        // Add required headers
        // NOTE: `Date` header is added by default
        Headers headers = exchange.getResponseHeaders();
        headers.add("Server", Config.SERVER_NAME);
        headers.add("Connection", "close");
        headers.add("Content-Length", String.valueOf(responseLength));

        byte[] buffer;
        int bytesRead;
        OutputStream os = null;
        try {
            exchange.sendResponseHeaders(responseCode, responseLength);
            os = exchange.getResponseBody();

            // send response body
            if (responseLength > 0) {
                buffer = new byte[STREAM_BUFFER_SIZE];
                while( (bytesRead = responseStream.read(buffer)) > 0) {
                    os.write(buffer, 0, bytesRead);
                }
            }
        }
        catch (IOException ex) {
            logger.warn("sendResponse: " + ex.getMessage());
        }
        finally {
            // cleanup
            try {
                os.close();
            }
            catch (IOException | NullPointerException ex) {
                logger.warn("sendResponse: " + ex.getMessage());
            }
        }
    }

    private void sendStringResponse(HttpExchange exchange, int responseCode, String response)
            throws UnsupportedEncodingException {
        byte[] bytes = response.getBytes("UTF-8");
        sendResponse(
            exchange,
            responseCode,
            new ByteArrayInputStream(bytes),
            bytes.length
        );
    }
}
