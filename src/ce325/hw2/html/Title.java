package ce325.hw2.html;

/**
 * Created by georgetg on 24/3/2017.
 */
public class Title extends Tag{

    public Title(String title) {
        mTagName = "title";
        mChildren.add(new Text(title));
    }
}
