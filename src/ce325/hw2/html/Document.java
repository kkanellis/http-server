package ce325.hw2.html;

import java.util.LinkedList;
import java.util.List;

/**
 * HTML Document object
 */
public class Document {
    private Tag mBody;
    private Tag mHead;
    private Tag mHtml;
    private List<Element> mChildren = new LinkedList<>();

    public Document() {

        // Add html doctype
        mChildren.add(new Doctype("html"));

        // Create html tag
        mHtml = new Html().addAttribute("lang", "en");

        // Create head tag
        mHead = new Head().addChild(new Meta().addAttribute("charset", "utf-8"));
        mHtml.addChild(mHead);

        // Create body tag
        mBody = new Body();
        mHtml.addChild(mBody);

        // Add the html tag as a child
        mChildren.add(mHtml);
    }

    public Tag getHead(){
        return mHead;
    }

    public Tag getBody() {
        return mBody;
    }

    /**
     * Get the HTML of this element and sub-elements
     * @return HTML String
     */
    public String getHTML() {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i < mChildren.size(); i++) {
           builder.append(mChildren.get(i).getHTML());
        }
        return builder.toString();
    }

}
