package ce325.hw2.http;

import ce325.hw2.util.Logger;

import java.nio.file.Path;

/**
 * Singleton class that holds icons paths
 */
public class Icons {
  private static Icons ourInstance = new Icons();

  public static Icons getInstance() {
    return ourInstance;
  }

  private static Logger logger = Logger.getInstance();
  private static String iconsDir;

  private Icons() { }

  public static void setDir(Path iconsDir) {
      Icons.iconsDir = "/" + iconsDir.toString() +
            (iconsDir.endsWith("/") ? "" : "/");
  }

  public static String getFileIcon(String fileExt) {
      String icon = iconsDir;
      switch (fileExt.toLowerCase()) {
          /* doc */
          case "doc":
          case "docx":
          case "odt":
              icon += "doc.png";
              break;
            /* xls */
          case "xls":
          case "xlsx":
          case "ods":
              icon += "xls.png";
              break;
            /* ppt */
          case "ppt":
          case "pptx":
          case "odp":
              icon += "ppt.png";
              break;
            /* pdf */
          case "pdf":
          case "ps":
              icon += "pdf.png";
              break;
            /* images */
          case "png":
          case "jpg":
          case "jpeg":
          case "bmp":
          case "tiff":
          case "svg":
          case "pgm":
          case "ppm":
          case "pbm":
              icon += "img.png";
              break;
            /* video */
          case "mp4":
          case "flv":
          case "mkv":
          case "ogv":
          case "avi":
          case "mov":
          case "qt":
              icon += "video.png";
              break;
            /* audio */
          case "wav":
          case "mp3":
          case "ogg":
          case "cda":
          case "flac":
          case "snd":
          case "aa":
          case "mka":
          case "wma":
          case "m4p":
          case "mp4a":
          case "mpa":
              icon += "audio.png";
              break;
            /* html */
          case "html":
          case "htm":
              icon += "html.png";
              break;
            /* xml */
          case "xml":
              icon += "xml.png";
              break;
            /* rss */
          case "rss":
              icon += "rss.png";
              break;
          default:
              icon += "txt.png";
      }
      return icon;
  }

  public static String getDirIcon() {
    return iconsDir + "dir.png";
  }

}
