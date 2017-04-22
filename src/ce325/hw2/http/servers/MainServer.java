package ce325.hw2.http.servers;

import ce325.hw2.http.handlers.FileHandler;
import ce325.hw2.util.Logger;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;

/**
 * Created by georgetg on 24/3/2017.
 */
public class MainServer {
    private Logger logger = Logger.getInstance();
    private HttpServer server;

    public MainServer(String rootDir, int listenPort) {
        try {
            // create HTTP server
            server = HttpServer.create(
                    new InetSocketAddress(listenPort),
                    0   // system default
            );
        }
        catch (IllegalArgumentException | IOException ex) {
            logger.error(ex.getMessage());
            // TODO: exit
        }

        try {
            // set handler for all requests
            server.createContext(
                    "/",
                    new FileHandler(rootDir)
            );
        }
        catch(IllegalArgumentException | NullPointerException ex) {
            logger.error(String.format("Invalid root directory [%s]", rootDir));
            logger.error(ex.getMessage());
            System.exit(-1);
        }
        // TODO: change that to add Queue support
        server.setExecutor(null);

        logger.debug("Server initialized");
    }

    public void start() {
        logger.debug("Starting server...");
        server.start();
        logger.info("Server started!");
    }

}
