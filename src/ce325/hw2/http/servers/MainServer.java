package ce325.hw2.http.servers;

import ce325.hw2.http.handlers.FileHandler;
import ce325.hw2.util.Logger;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main server
 */
public class MainServer {
    private Logger logger = Logger.getInstance();
    private HttpServer server;
    private ExecutorService executor;

    public MainServer(String rootDir, int listenPort) {
        try {
            // create HTTP server
            server = HttpServer.create(
                    new InetSocketAddress(listenPort),
                    0   // system default
            );
        } catch (IllegalArgumentException | IOException ex) {
            logger.error("Error while initializing main server...");
            logger.error(ex.getMessage());
            System.exit(-1);
        }

        try {
            // set handler for all requests
            server.createContext("/", new FileHandler(rootDir));
        } catch(IllegalArgumentException | NullPointerException ex) {
            logger.error(String.format("Invalid root directory [%s]", rootDir));
            logger.error(ex.getMessage());
            System.exit(-1);
        }

        // number of threads that handle requests
        executor = Executors.newFixedThreadPool(2);
        server.setExecutor(executor);

        logger.debug("Server initialized");
    }

    public void start() {
        logger.debug("Starting server...");
        server.start();
        InetSocketAddress address = server.getAddress();
        logger.info(String.format("File server listening on %s:[%d]", address.getHostName(), address.getPort()));
    }

    public void stop() {
        server.stop(0);
        executor.shutdown();
    }

}
