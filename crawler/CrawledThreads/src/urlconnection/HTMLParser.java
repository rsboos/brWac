package urlconnection;

import crawler.CrawledSites;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pagecontent.FetchPage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLParser {

    private final static Pattern Filters = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g"
            + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf"
            + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    public static synchronized void getLinks(CrawledSites crawledSites, String fileName, String outDirectory) throws IOException, BoilerpipeProcessingException {
        String url = crawledSites.getSeedUrl();
        if (shouldVisit(url)) {
            boolean notDownloaded = crawledSites.addListOfSites(url); // add to download
            if (notDownloaded)
                FetchPage.getContent(url, crawledSites, outDirectory);
        }
        Elements links = null;
        try {
            Document doc = Jsoup.connect(url).get();
            links = doc.select("a[href]");
        } catch (Exception e) {
            links = null;
        } finally {
            if (links != null) {
                for (Element link : links) {
                    String urlstr = link.attr("abs:href").toString();
                    if (shouldVisit(urlstr)) {
                        boolean notDownloaded = crawledSites.addListOfSites(urlstr); // add to download
                        if (notDownloaded)
                            FetchPage.getContent(urlstr, crawledSites, outDirectory);
                    }
                }
            }
        }
    }

    public static String getDomain(String url) throws MalformedURLException {
        String cleanUrl = url.toLowerCase().trim();
        URL link = new URL(cleanUrl);
        String domain = link.getHost();
        System.out.println(domain);
        return domain;
    }

    public static boolean shouldVisit(String url) {
        String cleanUrl = url.toLowerCase().trim();
        return !((Filters.matcher(cleanUrl).matches()));
    }
}
