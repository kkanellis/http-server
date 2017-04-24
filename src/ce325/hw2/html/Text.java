package ce325.hw2.html;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * HTML real text
 */
public class Text implements Element{
    private String mText;

    public Text(String text) {
        mText = text;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public String getHTML() {
        return mText;
    }

    @Override
    public Element addAttribute(String name, String value) {
        throw new NotImplementedException();
    }

    @Override
    public Element addChild(Element child) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeChild(Element child) {
        throw new NotImplementedException();
    }

    @Override
    public boolean removeAttribute(String name) {
        throw new NotImplementedException();
    }

    @Override
    public Element childAt(int index) {
        throw new NotImplementedException();
    }
}
