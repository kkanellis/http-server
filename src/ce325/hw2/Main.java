package ce325.hw2;

import ce325.hw2.html.Document;
import ce325.hw2.html.P;
import ce325.hw2.html.Text;
import ce325.hw2.html.Title;
import ce325.hw2.http.Icons;
import ce325.hw2.http.MIMETypes;
import ce325.hw2.http.servers.MainServer;
import ce325.hw2.io.Config;
import ce325.hw2.service.StatisticsService;
import ce325.hw2.util.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Document DOM = new Document();
        DOM.getBody().addChild(new P().addChild(new Text("Hello World!")));

        DOM.getHead().addChild(new Title("TEST!"));
        System.out.println(DOM.getHTML());

        StatisticsService s = StatisticsService.getInstance();

        s.start();

        int slot = s.onConnect(-1);
        int slot2 = s.onConnect(-2);
        s.onDisconnect(slot, 400);
        s.onDisconnect(slot2, 200);
        int slot3 = s.onConnect(-3);
        s.onDisconnect(slot3, 200);
        System.out.println(s.getMeanTime());

        // Parse the XML configuration file
        Logger logger = Logger.getInstance();
        Config config;
        try {
            JAXBContext jc = JAXBContext.newInstance(Config.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            config = (Config)unmarshaller.unmarshal(new File("config.xml"));
        }
        catch (Exception ex) {
            logger.error("Error while reading configuration file!");
            logger.error(ex.getMessage());
            ex.printStackTrace();
            return;
        }

        // Read MIME types
        MIMETypes.populateFromFile(
                config.getMIMETypesPath().toString()
        );

        // Set icons path
        Path iconsDir = config.getIconsDirPath();
        Path absoluteIconsDir = config.getDocumentRootPath().resolve(iconsDir);
        if (!Files.isDirectory(absoluteIconsDir)) {
            logger.warn(String.format("Provided icons dir '%s' is not a directory!", absoluteIconsDir));
            logger.warn("Icons may not show up correctly...");
        }
        Icons.setDir(iconsDir);

        // Initialize server
        MainServer mainServer = new MainServer(
            config.getDocumentRootPath().toString(),
            config.getMainServerPort()
        );
        mainServer.start();
    }
}
