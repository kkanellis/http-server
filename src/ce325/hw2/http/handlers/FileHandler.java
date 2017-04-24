package ce325.hw2.http.handlers;

import ce325.hw2.html.*;
import ce325.hw2.http.HttpStatusCodes;
import ce325.hw2.http.Icons;
import ce325.hw2.http.MIMETypes;
import ce325.hw2.io.Config;
import ce325.hw2.service.StatisticsService;
import ce325.hw2.service.WebLoggingService;
import ce325.hw2.util.DirectoriesFirstComparator;
import ce325.hw2.util.Logger;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the HTTP requests
 */
public class FileHandler implements HttpHandler {
    private final int STREAM_BUFFER_SIZE = 1024;

    private Logger logger = Logger.getInstance();
    private WebLoggingService webLogger = WebLoggingService.getInstance();
    private StatisticsService stats = StatisticsService.getInstance();
    private String rootDir;

    public FileHandler(String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public void handle(HttpExchange exchange) throws UnsupportedEncodingException {
        // Get a slot and request start time
        int slot = stats.onConnect(-(int)Thread.currentThread().getId());
        long connectedAt = stats.getTimeDelta();

        // Check if request method is not GET
        if ( !exchange.getRequestMethod().equalsIgnoreCase("GET") ) {
            sendStringResponse(
                exchange,
                HttpStatusCodes.HTTP_METHOD_NOT_ALLOWED,
                "Server supports only GET requests"
            );
            stats.onDisconnect(slot, (int)(stats.getTimeDelta() - connectedAt));
            return;
        }

        // find requested absolute path
        Path filepath = Paths.get(
            rootDir,
            URLDecoder.decode(exchange.getRequestURI().toString(), "UTF-8")
        ).toAbsolutePath();

        logger.debug(String.format("Request for '%s'", filepath.toString()));

        // decide how to handle the request
        try {
            if (Files.isDirectory(filepath)) {
                serveDir(exchange, filepath);
            } else if (Files.isRegularFile(filepath)) {
                serveFile(exchange, filepath);
            } else {
                sendStringResponse(
                    exchange,
                    HttpStatusCodes.HTTP_NOT_FOUND,
                    "Requested resource does not exist (or is symbolic link)"
                );
            }
        } catch (Exception ex) {
            logger.warn("handle: " + ex.getMessage());
            sendStringResponse(
                exchange,
                HttpStatusCodes.HTTP_SERVER_ERROR,
                "Internal server error"
            );

            // Need to convert to string
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            webLogger.error(exchange, sw.toString());
            stats.onError();
        } finally {
            stats.onDisconnect(slot, (int)(stats.getTimeDelta() - connectedAt));
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
        String fileExt = getFileExtension(filepath);
        String mimeType = (fileExt.length() > 0) ?
                MIMETypes.getMIMEType(fileExt) :
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
        webLogger.access(exchange, HttpStatusCodes.HTTP_OK);
    }

    /**
     * Serves this directory to the client
     *
     * If an 'index.html' file exists in the directory returns its contents
     * Otherwise, returns a String which contains an HTML page which
     * presents the contents of this directory
     * @param exchange request object
     * @param dir Path obj of the directory
     */
    private void serveDir(HttpExchange exchange, Path dir) throws IOException {
        // add trailing slash (needed for correct links etc)
        URI requestURI = exchange.getRequestURI();
        requestURI = !requestURI.toString().endsWith("/") ?
                        requestURI.resolve(requestURI.toString() + "/") :
                        requestURI;
        logger.debug(String.format(
                "serveDir: '%s' -> '%s'",
                requestURI.toString(),
                dir.toString()
        ));

        // return 'index.html' if exists
        Path indexFile = Paths.get(dir.toString(), "index.html");
        if (Files.isRegularFile(indexFile) ) {
            serveFile(exchange, indexFile);
        }

        Document response = new Document();
        response.getBody().addChild(new H(1).addChild(
            new Text(String.format("Index of %s", dir.getFileName()))
        ));

        // fetch and sort files/dirs inside directory
        List<Path> files = Files.list(dir)
            .sorted(new DirectoriesFirstComparator())
            .collect(Collectors.toCollection(ArrayList::new));

        // add table header
        Table table = new Table();
        Tr header = new Tr();
        header.addChild(new Th());
        header.addChild(new Th().addChild(new Text("Name")));
        header.addChild(new Th().addChild(new Text("Last Modified")));
        header.addChild(new Th().addChild(new Text("Size")));
        table.addChild(header);

        // horizontal rule
        table.addChild(new Td().addAttribute("colspan", "5")
            .addChild(new Hr())
        );

        // add parent directory as entry
        if ( !requestURI.getPath().equals("/") ) {
            URI parentDir = requestURI.getPath().endsWith("/") ?
                                requestURI.resolve("..") :
                                requestURI.resolve(".");
            table.addChild(
               getFileEntryHTML(dir, "..", parentDir)
            );
        }

        // populate table
        for(Path p : files) {
            String filename = p.getFileName().toString();
            URI href = requestURI.resolve( URLEncoder.encode(filename, "UTF-8") );
            table.addChild(
                getFileEntryHTML(p, filename, href)
            );
        }
        response.getBody().addChild(table);

        // horizontal rule
        table.addChild(new Td().addAttribute("colspan", "5")
            .addChild(new Hr())
        );

        // TODO: add <address>

        // add 'Content-Type' header
        exchange.getResponseHeaders().add("Content-Type", "text/html");

        sendStringResponse(
            exchange,
            HttpStatusCodes.HTTP_OK,
            response.getHTML()
        );
        webLogger.access(exchange, HttpStatusCodes.HTTP_OK);
    }

    /**
     * Returns a Tr obj (HTML row) containing the file info
     *
     * @param file Path object of the file
     * @param displayName Name that will be displayed in the HTML
     * @param href Link to the file
     * @return Tr object
     * @throws IOException
     */
    private Tr getFileEntryHTML(Path file, String displayName, URI href)
            throws IOException {
        Tr r = new Tr();

        // icon
        Img icon = new Img();
        icon.addAttribute("src",
                Files.isDirectory(file) ?
                    Icons.getDirIcon() :
                    Icons.getFileIcon(getFileExtension(file))
        );
        r.addChild(new Td().addChild(icon));

        // link to file/dir
        A a = new A();
        a.addAttribute("href", href.toString());
        a.addChild(new Text(displayName));
        r.addChild(new Td().addChild(a));

        // file last modified
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
        r.addChild(new Td().addChild(new Text(
            df.format(Files.getLastModifiedTime(file).toMillis())
        )));

        // human readable file size (file only)
        String fileSize = "";
        if (Files.isRegularFile(file)) {
            long bytes = Files.size(file);
            if (bytes < 1024) {
                fileSize = bytes + " B";
            }
            else {
                int exp = (int) (Math.log(bytes) / Math.log(1024));
                String pre = Character.toString("KMGTPE".charAt(exp - 1));
                fileSize = String.format("%.2f %sB", bytes / Math.pow(1024, exp), pre);
            }
        }
        r.addChild(new Td().addChild(new Text(fileSize))
                .addAttribute("align", "right")
        );

        return r;
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

        // Log access and add error if needed
        webLogger.access(exchange, responseCode);
        if (responseCode >= 400) {
            stats.onError();
        }
    }

    /**
     * Returns the file extension as a String (without the dot)
     * @param file
     * @return The file extension if there is one; empty string otherwise.
     */
    private String getFileExtension(Path file) {
        String filename = file.getFileName().toString();
        int fileExtPos = filename.lastIndexOf('.');

        return (fileExtPos > 0) ? filename.substring(fileExtPos + 1) : "";
    }
}
