package ce325.hw2.html;

/**
 * HTML element interface
 */
public interface Element <T extends Element<T>> {
    boolean hasChildren();
    String getHTML();
    T addAttribute(String name, String value);
    T addChild(Element child);
    boolean removeChild(Element child);
    boolean removeAttribute(String name);
    Element childAt(int index);
}
