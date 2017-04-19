package ce325.hw2.http;

import ce325.hw2.util.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that holds common MIME Types
 */
public class MIMETypes {
    private static MIMETypes ourInstance = new MIMETypes();
    public static MIMETypes getInstance() {
        return ourInstance;
    }

    private static Logger logger = Logger.getInstance();

    public static final String UNKNOWN_FILE_MIME_TYPE = "application/octet-stream";
    private static Map<String, String> mimeTypes;

    private MIMETypes() {
        mimeTypes = new HashMap<>();
    }

    /**
     * Populates the MIME types Map from a given file
     *
     * Each line of the file should have the following format:
     * .[ext]=[mime-type]
     * where [ext] is the file extension and [mime-type] is
     * the correct MIME type for this kind of file
     *
     * @param filepath path to the file containing the MIMETypes
     */
    public static void populateFromFile(String filepath) {
        BufferedReader reader = null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(filepath));
            while ((line = reader.readLine()) != null) {
                // Find '=' index in line
                int equalSignPos = line.indexOf('=');
                if ( !line.startsWith(".") || equalSignPos < 0 ) {
                    logger.warn(String.format("MIMETypes: invalid line '%s'", line));
                    continue;
                }

                mimeTypes.put(
                    line.substring(1, equalSignPos),
                    line.substring(equalSignPos + 1, line.length())
                );
            }
        }
        catch (IOException ex) {
            logger.error("MIMETypes: error while populating map");
            logger.error(ex.getMessage());
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException ex) {
                logger.warn(String.format("MIMETypes: error while closing file '%s'", filepath));
                logger.warn(ex.getMessage());
            }
        }
    }

    /**
     * Returns the MIME type for the given file extension
     * @param fileExt the file extension (without the dot '.')
     * @return the correct MIME type if exists; UNKNOWN_FILE_MIME_TYPE otherwise
     */
    public static String getMIMEType(String fileExt) {
        String mimeType = mimeTypes.get(fileExt);
        return (mimeType != null) ? mimeType : UNKNOWN_FILE_MIME_TYPE;
    }
}
