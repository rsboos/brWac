package crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import urlconnection.HTMLParser;

public class CrawledSites {

    private List<Integer> crawledSites = new ArrayList<Integer>();
    private Queue<String> listOfSites = new LinkedList<String>();
    // private HashMap<String,String> content = new HashMap<String,String>() ;
    private List<String> crawledDomains = new ArrayList<String>();

    public List<String> getCrawledDomains() {
        return crawledDomains;
    }

    public void setCrawledDomains(List<String> crawledDomains) {
        this.crawledDomains = crawledDomains;
    }

    public List<Integer> getCrawledSites() {
        return crawledSites;
    }

    public String getTopUrl() {
        return this.getListOfSites().peek();
    }

    public synchronized String getSeedUrl() {
        return (this.getListOfSites().remove());
    }

    public void addCrawledSites(String url) throws MalformedURLException {
        int HashToAdd = getHash(url);
        this.crawledSites.add(HashToAdd);
        System.out.println(url);
    }

    public void setCrawledSites(ArrayList<Integer> crawledSites) {
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
