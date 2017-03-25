package ce325.hw2;

import ce325.hw2.html.Document;
import ce325.hw2.html.P;
import ce325.hw2.html.Text;
import ce325.hw2.html.Title;

public class Main {

    public static void main(String[] args) {
        Document DOM = new Document();
        DOM.getBody().addChild(new P().addChild(new Text("Hello World!")));

        DOM.getHead().addChild(new Title("TEST!"));
        System.out.println(DOM.getHTML());
    }
}
