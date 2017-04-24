package ce325.hw2;

import ce325.hw2.http.Icons;
import ce325.hw2.http.MIMETypes;
import ce325.hw2.http.servers.MainServer;
import ce325.hw2.http.servers.StatisticsServer;
import ce325.hw2.io.Config;
import ce325.hw2.service.StatisticsService;
import ce325.hw2.service.WebLoggingService;
import ce325.hw2.util.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        // Initialize statistics server mechanism
        StatisticsService s = StatisticsService.getInstance();
        s.start();

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
        // NOTE that icons path is relative to the root directory
        Path iconsDir = config.getIconsDirPath();
        Path absoluteIconsDir = config.getDocumentRootPath().resolve(iconsDir);
        if (!Files.isDirectory(absoluteIconsDir)) {
            logger.warn(String.format("Provided icons dir '%s' is not a directory!", absoluteIconsDir));
            logger.warn("Icons may not show up correctly...");
        }
        Icons.setDir(iconsDir);

        // Initialize web logging
        WebLoggingService wls = WebLoggingService.getInstance();
        wls.setAccessLogPath(config.getAccessFilepath());
        wls.setErrorLogPath(config.getErrorFilepath());

        // Initialize servers
        MainServer mainServer = new MainServer(
            config.getDocumentRootPath().toString(),
            config.getMainServerPort()
        );
        mainServer.start();

        StatisticsServer statisticsServer = new StatisticsServer(
                config.getStatisticsServerPort()
        );
        statisticsServer.start();

    }
}
