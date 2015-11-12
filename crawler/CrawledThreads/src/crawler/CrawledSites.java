package crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import urlconnection.HTMLParser;

public class CrawledSites {

    private HashSet<Integer> crawledSites = new HashSet<Integer>();
    private Queue<String> listOfSites = new LinkedList<String>();
    // private HashMap<String,String> content = new HashMap<String,String>() ;
    private List<String> crawledDomains = new ArrayList<String>();

    public List<String> getCrawledDomains() {
        return crawledDomains;
    }

    public void setCrawledDomains(List<String> crawledDomains) {
        this.crawledDomains = crawledDomains;
    }

    public HashSet<Integer> getCrawledSites() {
        return crawledSites;
    }

    public String getTopUrl() {
        return this.getListOfSites().peek();
    }

    public synchronized String getSeedUrl() {
        return (this.getListOfSites().remove());
    }

    public void addCrawledSites(String url, String fileOutput) throws MalformedURLException, IOException {
        int HashToAdd = getHash(url);
        this.crawledSites.add(HashToAdd);
        String currentDir = System.getProperty("user.dir");
        File fileOut = new File(currentDir, fileOutput);
        BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
        out.write(url+"\n");
        out.close();
    }

    public void setCrawledSites(HashSet<Integer> crawledSites) {
        this.crawledSites = crawledSites;
    }

    public synchronized Queue<String> getListOfSites() {
        return listOfSites;
    }

    public void setListOfSites(Queue<String> listOfSites) {
        this.listOfSites = listOfSites;
    }
    
    public String normalizeURL(String url) throws MalformedURLException {
        URL urlSample = new URL(url);
        return urlSample.getProtocol()+"://"+urlSample.getAuthority() + urlSample.getFile();
    }
    
    public int getHash(String url) throws MalformedURLException { // normalize and generate hash for url
        String normalizedURL = normalizeURL(url);
        return normalizedURL.hashCode();
    }

    public synchronized boolean addListOfSites(String url) throws MalformedURLException {
        int HashToAdd = getHash(url);
        if (!(this.getCrawledSites().contains(HashToAdd))) {
            this.listOfSites.add(normalizeURL(url));
            return true;
        }
        else
            return false;
    }
}
