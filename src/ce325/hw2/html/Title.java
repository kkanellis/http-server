package ce325.hw2.html;

/**
 * HTML <title> tag
 */
public class Title extends Tag{

    public Title(String title) {
        mTagName = "title";
        mChildren.add(new Text(title));
    }
}
