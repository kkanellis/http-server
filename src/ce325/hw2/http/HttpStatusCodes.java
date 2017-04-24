package ce325.hw2.http;

/**
 * Created by sfi on 20/4/2017.
 */
public class HttpStatusCodes {
    public static final int HTTP_OK = 200;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_METHOD_NOT_ALLOWED = 405;
    public static final int HTTP_SERVER_ERROR = 500;

    public static String describe(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            case 405:
                return "Method Not Allowed";
            case 500:
                return "Internal Server Error";
            default:
                return "";
        }
    }
}
