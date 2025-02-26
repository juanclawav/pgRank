package webCrawler;

public class Out {
    public String href;
    public String anchor;

    public Out(String h, String a) {
        href = h;
        anchor = a;
    }
    public String getDestination() {
        return href;
    }
}
