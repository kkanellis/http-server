package ce325.hw2.html;

/**
 * Created by georgetg on 24/3/2017.
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
