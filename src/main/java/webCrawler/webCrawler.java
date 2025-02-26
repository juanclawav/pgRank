package webCrawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class webCrawler {

    private static final String dominioPrincipal = "rioselva.com.bo";
    private static final int maxD = 10;
    private MultiValuedMap < String, Out > inOut;
    public webCrawler() {
        inOut = new HashSetValuedHashMap < > ();
    }
    public static String getDomainName(String url) {
        String dominio = null;

        try {
            URI uri = new URI(url);
            dominio = uri.getHost();
            dominio = dominio.startsWith("www.") ? dominio.substring(4) : dominio;

            if (dominio.startsWith("mailto:")) {
                dominio = null;
            }

        } catch (URISyntaxException e) {

        }

        return dominio;
    }
    public void crawl(String URL, int depth) {
        System.out.println("D =  " + depth + " | URL = " + URL);

        try {
            String dom = getDomainName(URL);

            if (dom != null && !inOut.containsKey(URL)
                    && dom.equals(dominioPrincipal) &&
                    (depth < maxD)) {
                Document doc = Jsoup.connect(URL).get();
                Elements ahrefs = doc.select("a[href]");

                depth++;

                for (Element ahref: ahrefs) {
                    String dest = ahref.attr("abs:href");
                    String anchor = ahref.text();
                    inOut.put(URL, new Out(dest, anchor));

                    crawl(dest, depth);
                }
            }

        } catch (Exception e) {
        }
    }
    public void writeGraphFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(inOut.size() + " " + inOut.values().size() + "\n");
            for (String source : inOut.keySet()) {
                for (Out out : inOut.get(source)) {
                    writer.write(source + " " + out.getDestination() + "\n");
                }
            }
            System.out.println("grafo creado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        webCrawler crawler = new webCrawler();
        crawler.crawl("https://www.rioselva.com.bo", 0);
        crawler.writeGraphFile("graph.txt");
    }
}