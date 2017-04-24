package ce325.hw2.html;

/**
 * HTML <h?> tag
 */
public class H extends Tag {
    public H(int size) {
        mTagName = String.format("h%d", size);
    }
}
