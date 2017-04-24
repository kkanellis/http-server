package ce325.hw2.html;

/**
 * HTML <p> tag
 */
public class P extends Tag{
    public P() {
        mTagName = "p";
    }

    public P(String text) {
        mTagName = "p";
        mChildren.add(new Text(text));
    }
}
