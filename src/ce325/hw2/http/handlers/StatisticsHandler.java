package ce325.hw2.http.handlers;

import ce325.hw2.html.*;
import ce325.hw2.http.HttpStatusCodes;
import ce325.hw2.service.StatisticsService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * Handles the HTTP server statistics requests
 */
public class StatisticsHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        StatisticsService stats = StatisticsService.getInstance();

        // Create the html document
        Document DOM = new Document();
        DOM.getHead().addChild(new Title("Server statistics"));

        DOM.getBody().addChild(new H(3).addChild(new Text("Server statistics:")));
        DOM.getBody().addChild(new Hr());
        DOM.getBody().addChild(new P("Total connections: " + stats.getConnections()));
        DOM.getBody().addChild(new P("Total errors: " + stats.getTotalErrors()));
        DOM.getBody().addChild(new P(String.format("Request mean time: %d ms", stats.getMeanTime())));
        DOM.getBody().addChild(new Hr());
        Date date = new Date(stats.getStartedTime());
        DOM.getBody().addChild(new P(String.format("Server up since: %s (%.2f seconds)",
                date.toString(), stats.getTimeDelta()/1000f)));

        // write the response
        String response = DOM.getHTML();
        httpExchange.sendResponseHeaders(HttpStatusCodes.HTTP_OK, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }


}
