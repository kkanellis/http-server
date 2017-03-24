package ce325.hw2.html;

import java.util.Map;

/**
 * Created by georgetg on 24/3/2017.
 */
public class VoidTag extends Tag {
    /**
     * Get the HTML of this element and sub-elements
     * @return HTML String
     */
    @Override
    public String getHTML() {
        StringBuilder builder = new StringBuilder();

        // Create opening tag
        builder.append("<" + this.mTagName);
        // Add attributes
        for (Map.Entry<String, String> entry: mAttributes.entrySet()) {
            builder.append( String.format(" %s=\"%s\"", entry.getKey(), entry.getValue()) );
        }
        builder.append(">");
        // No body
        return builder.toString();
    }

}
