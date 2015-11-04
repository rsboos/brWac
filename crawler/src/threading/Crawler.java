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
    private String visitedLinks;
    private String outDirectory;

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

    Crawler(CrawledSites crawledSites, int number, String visitedLinks,String outDirectory){
        this.crawledSites = crawledSites;
        this.numberOfLinksToCrawl=number;
        this.visitedLinks = visitedLinks;
        this.outDirectory = outDirectory;
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

            while(crawledSites.getListOfSites().size()<numberOfLinksToCrawl) {
            	HTMLParser.getLinks(crawledSites,true,visitedLinks,outDirectory);
            }
          } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }

    public static void initializeCrawling(int numberOfThreads,CrawledSites crawledSites,int maximumLimit,String visitedLinks, String outDirectory){
       for(int i=0;i<numberOfThreads;++i){
        new Thread(new Crawler(crawledSites,maximumLimit,visitedLinks,outDirectory)).start();
        }
    }

    public static void addSeedPages(CrawledSites crawledSites, String url) throws MalformedURLException{
                     crawledSites.addListOfSites(url);
    }
 
    public static void main(String[] args) throws IOException{
        if (args.length == 3) {
	    int NumberOfThreads = 100;
        CrawledSites crawledSites = new CrawledSites();
        String currentDir = System.getProperty("user.dir");
        File seedsFile = new File(currentDir,args[0]);
        BufferedReader br = new BufferedReader(new FileReader(seedsFile));
        String line;
        
        while ((line = br.readLine()) != null) {
           crawledSites.addListOfSites(line);
        }

		File visitedFile = new File(currentDir,args[2]);
		br = new BufferedReader(new FileReader(visitedFile));
		while ((line = br.readLine()) != null) {
		   crawledSites.addCrawledSites(line);
		}

        br.close();
        initializeCrawling(NumberOfThreads,crawledSites,2000000000,args[2],args[1]);
        }
        else {
            System.out.println("The execution must be started in this form: java -jar dist/craw_simple.jar seedsFile.txt outDirectory visitedLinks.txt (optional)");
            
        }
    }
}
