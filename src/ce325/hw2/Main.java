package ce325.hw2;

import ce325.hw2.html.Document;
import ce325.hw2.html.P;
import ce325.hw2.html.Text;
import ce325.hw2.html.Title;
import ce325.hw2.http.MIMETypes;
import ce325.hw2.http.servers.MainServer;
import ce325.hw2.service.StatisticsService;

public class Main {

    public static void main(String[] args) {
        Document DOM = new Document();
        DOM.getBody().addChild(new P().addChild(new Text("Hello World!")));

        DOM.getHead().addChild(new Title("TEST!"));
        System.out.println(DOM.getHTML());

        StatisticsService s = StatisticsService.getInstance();

        s.start();

        int slot = s.onConnect(-1);
        int slot2 = s.onConnect(-2);
        s.onDisconnect(slot, 400);
        s.onDisconnect(slot2, 200);
        int slot3 = s.onConnect(-3);
        s.onDisconnect(slot3, 200);
        System.out.println(s.getMeanTime());

        MIMETypes.populateFromFile("mime-types.txt");

        MainServer mainServer = new MainServer("/home/", 8000);
        mainServer.start();

    }
}
