package threading;
import crawler.CrawledSites;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.util.Queue;
import urlconnection.HTMLParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Crawler implements Runnable {
    private int numberOfLinksToCrawl;
    private CrawledSites crawledSites;

    public CrawledSites getCrawledSites() {
        return crawledSites;
    }

    public void setCrawledSites(CrawledSites crawledSites,int numberOfLinksToCrawl) {
        this.crawledSites = crawledSites;
        this.numberOfLinksToCrawl=numberOfLinksToCrawl;
    }



    Crawler(int number){
       this.numberOfLinksToCrawl =number;

    }

    Crawler(CrawledSites crawledSites, int number){
        this.crawledSites = crawledSites;
        this.numberOfLinksToCrawl=number;
    }
    public int getNumberOfLinksToCrawl() {
        return numberOfLinksToCrawl;
    }

    public void setNumberOfLinksToCrawl(int numberOfLinksToCrawl) {
        this.numberOfLinksToCrawl = numberOfLinksToCrawl;
    }

    @Override

    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        try {

            while(crawledSites.getListOfSites().size()<numberOfLinksToCrawl){
            HTMLParser.getLinks(crawledSites,true);

             }
          } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void initializeCrawling(int numberOfThreads,CrawledSites crawledSites,int maximumLimit){
       for(int i=0;i<numberOfThreads;++i){
        new Thread(new Crawler(crawledSites,maximumLimit)).start();
        }
    }

    public static void addSeedPages(CrawledSites crawledSites, String url) throws MalformedURLException{
                     crawledSites.addListOfSites(url);
    }
 
    public static void main(String[] args) throws IOException{
	int NumberOfThreads = 10
        CrawledSites crawledSites = new CrawledSites();
        String currentDir = System.getProperty("user.dir");
        File seedsFile = new File(currentDir,args[0]);
        BufferedReader br = new BufferedReader(new FileReader(seedsFile));
        String line;	
        while ((line = br.readLine()) != null) {
           crawledSites.addListOfSites(line);
        }
	if (args.length > 1) {
		File visitedFile = new File(currentDir,args[1]);
		br = new BufferedReader(new FileReader(visitedFile));
		while ((line = br.readLine()) != null) {
		   crawledSites.addCrawledSites(line);
		}
	}

        br.close();
        initializeCrawling(NumberOfThreads,crawledSites,2000000000);    
    }
}
