package crawler;

import java.net.MalformedURLException;
import java.util.*;
import urlconnection.HTMLParser;

public class CrawledSites {
    private List<String> crawledSites = new ArrayList<String>();
    private Queue<String> listOfSites = new LinkedList<String>();
   // private HashMap<String,String> content = new HashMap<String,String>() ;
    private List<String> crawledDomains = new ArrayList<String>();

    public List<String> getCrawledDomains() {
        return crawledDomains;
    }

    public void setCrawledDomains(List<String> crawledDomains) {
        this.crawledDomains = crawledDomains;
    }



   /* public HashMap<String, String> getContent() {
        return content;
    }

    public void setContent(HashMap<String, String> content) {
        this.content = content;
    } 

     public synchronized void addContent(String key,String value){
        this.content.put(key,value);
    } */


    public List<String> getCrawledSites() {
        return crawledSites;
    }

    public String getTopUrl(){
        return this.getListOfSites().peek();
    }
    public synchronized String getSeedUrl()
    {
        return (this.getListOfSites().remove());
    }
    public void addCrawledSites(String url){
    	System.out.println("Crawled site: "+url);
        this.crawledSites.add(url);
        
    }
    public void setCrawledSites(ArrayList<String> crawledSites) {
        this.crawledSites = crawledSites;
    }
    public  synchronized Queue<String> getListOfSites() {
        return listOfSites;
    }


    public void setListOfSites(Queue<String> listOfSites) {
        this.listOfSites = listOfSites;
    }

    public synchronized void addListOfSites(String url) throws MalformedURLException{

        if(!(this.getCrawledSites().contains(url)|| this.getListOfSites().contains(url)||url.contains("twitter")||url.contains(".pdf"))){
             this.listOfSites.add(url.trim());
          }


    }
}


