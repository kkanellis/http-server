package ce325.hw2.html;

/**
 * Created by georgetg on 24/3/2017.
 */
public interface Element <T extends Element<T>> {
    public boolean hasChildren();
    public String getHTML();
    public T addAttribute(String name, String value);
    public T addChild(Element child);
    public boolean removeChild(Element child);
    public boolean removeAttribute(String name);
    public Element childAt(int index);
}
