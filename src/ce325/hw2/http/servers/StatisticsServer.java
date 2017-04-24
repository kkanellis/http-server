package ce325.hw2.http.servers;

import ce325.hw2.http.handlers.StatisticsHandler;
import ce325.hw2.util.Logger;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Statistics server
 */
public class StatisticsServer {
    private Logger logger = Logger.getInstance();
    private HttpServer server;

    public StatisticsServer(int listenPort) {
        try {
            // create HTTP server
            server = HttpServer.create(
                    new InetSocketAddress(listenPort),
                    0   // system default
            );

            server.createContext("/", new StatisticsHandler());
        } catch (IllegalArgumentException | IOException ex) {
            logger.error("Error while initializing statistics server...");
            logger.error(ex.getMessage());
            System.exit(-1);
        }
    }

    public void start() {
        logger.debug("Starting statistics server...");
        server.start();
        InetSocketAddress address = server.getAddress();
        logger.info(String.format("Statistics server listening on %s:[%d]", address.getHostName(), address.getPort()));
    }

    public void stop() {
        server.stop(0);
    }

}

