package ce325.hw2.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * HTML Tag (implements HTML Element)
 */
public class Tag implements Element {
    protected String mTagName = "";
    protected List<Element> mChildren = new LinkedList<>();
    protected HashMap<String, String> mAttributes = new HashMap<>();

    /**
     * Add a child node
     * @param child the child to add
     * @return this tag
     */
    public Tag addChild(Element child) {
        mChildren.add(child);
        return this;
    }

    /**
     * Add an atribute
     * @param name name of the attribute
     * @param value value of the attribute
     * @return this tag
     */
    public Tag addAttribute(String name, String value) {
        mAttributes.put(name, value);
        return this;
    }

    /**
     * Remove an attribute
     * @param name name of the attribute
     * @return whether the attribute was removed
     */
    public boolean removeAttribute(String name) {
        if (mAttributes.containsKey(name)) {
            mAttributes.remove(name);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove a child
     * @param child the child to remove
     * @return whether the child was removed or not
     */
    public boolean removeChild(Element child) {
        if (mChildren.contains(child)) {
            mChildren.remove(child);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove a child at index
     * @param index index of child to remove
     * @return whether the child was removed or not
     */
    public boolean removeChild(int index) {
         if (mChildren.size() < index) {
            mChildren.remove(index);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find out if this element has any children
     * @return true if element  has any children, false otherwise
     */
    public boolean hasChildren() {
       return mChildren.size() > 0;
    }

    /**
     * Get the child at index
     * @param index
     * @return child if the index is valid or null
     */
    public Element childAt(int index) {
        if (mChildren.size() < index) {
            return mChildren.get(index);
        } else {
            return null;
        }
    }

    /**
     * Get the HTML of this element and sub-elements
     * @return HTML String
     */
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        // Create opening tag
        builder.append("<" + this.mTagName);
        // Add attributes
        for (Map.Entry<String, String> entry: mAttributes.entrySet()) {
            builder.append( String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()) );
        }
        builder.append(">");

        // Create body
        for (int i=0; i < mChildren.size(); i++) {
           builder.append(mChildren.get(i).getHTML());
        }
        // Wrap it up
        builder.append("</" + this.mTagName + ">" );

        return builder.toString();
    }

}
