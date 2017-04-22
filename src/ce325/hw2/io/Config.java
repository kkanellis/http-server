package ce325.hw2.io;

import javax.xml.bind.annotation.*;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * XML Configuration Binding (using JavaBean)
 */
@XmlRootElement(name="ce325server")
public class Config {
    public static final String SERVER_NAME = "CE325 HW2 Server";

    @XmlElement(name="listen")
    private ListenPort mainServer;
    public int getMainServerPort() { return mainServer.port; }

    @XmlElement(name="statistics")
    private ListenPort statisticsServer;
    public int getStatisticsServerPort() { return statisticsServer.port; }

    @XmlElement(name="log")
    private LoggersInfo log;
    public Path getAccessFilepath() {
        return Paths.get(log.accessLogFilepath.filepath)
                .normalize().toAbsolutePath();
    }
    public Path getErrorFilepath() {
        return Paths.get(log.errorLogFilepath.filepath)
                .normalize().toAbsolutePath();
    }

    @XmlElement(name="documentroot")
    private FileInfo documentRoot;
    public Path getDocumentRootPath() {
        return Paths.get(documentRoot.filepath)
                .normalize().toAbsolutePath();
    }

    @XmlElement(name="mimetypes")
    private FileInfo mimetypes;
    public Path getMIMETypesPath() {
        return Paths.get(mimetypes.filepath)
                .normalize().toAbsolutePath();
    }

    private static class LoggersInfo {
        @XmlElement(name="access")
        private FileInfo accessLogFilepath;

        @XmlElement(name="error")
        private FileInfo errorLogFilepath;
    }

    private static class ListenPort {
        @XmlAttribute(name="port")
        private int port;
    }

    private static class FileInfo {
        @XmlAttribute(name="filepath")
        private String filepath;
    }

}
