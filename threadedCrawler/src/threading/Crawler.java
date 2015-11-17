package threading;

import crawler.CrawledSites;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import urlconnection.HTMLParser;

import java.io.IOException;

public class Crawler implements Runnable {

    private int numberOfLinksToCrawl;
    private CrawledSites crawledSites;
    public String visitedLinks;
    public String outDirectory;
    public String associationFile;

    public CrawledSites getCrawledSites() {
        return crawledSites;
    }

    public void setCrawledSites(CrawledSites crawledSites, int numberOfLinksToCrawl) {
        this.crawledSites = crawledSites;
        this.numberOfLinksToCrawl = numberOfLinksToCrawl;
    }

    Crawler(int number) {
        this.numberOfLinksToCrawl = number;
    }

    Crawler(CrawledSites crawledSites, int number, String visitedLinks, String outDirectory, String associationFile) {
        this.crawledSites = crawledSites;
        this.numberOfLinksToCrawl = number;
        this.visitedLinks = visitedLinks;
        this.outDirectory = outDirectory;
        this.associationFile = associationFile;
    }

    public int getNumberOfLinksToCrawl() {
        return numberOfLinksToCrawl;
    }

    public void setNumberOfLinksToCrawl(int numberOfLinksToCrawl) {
        this.numberOfLinksToCrawl = numberOfLinksToCrawl;
    }

    @Override
    public void run() {
        try {
            while (crawledSites.getListOfSites().size() < numberOfLinksToCrawl)
                HTMLParser.getLinks(crawledSites, visitedLinks, outDirectory, associationFile);
        } catch (IOException e) {
            e.printStackTrace();  
        } catch (BoilerpipeProcessingException e) {
            e.printStackTrace();
        }

    }

    public static void initializeCrawling(int numberOfThreads, CrawledSites crawledSites, int maximumLimit, String visitedLinks, String outDirectory, String associationFile) {
        for (int i = 0; i < numberOfThreads; ++i) {
            //System.out.println("new thread");
            new Thread(new Crawler(crawledSites, maximumLimit, visitedLinks, outDirectory, associationFile)).start();
        }
    }

    public static void addSeedPages(CrawledSites crawledSites, String url) throws MalformedURLException {
        crawledSites.addListOfSites(url);
    }

    public static void main(String[] args) throws IOException {
        
        if (args.length == 5) {
            int NumberOfThreads = Integer.parseInt(args[4]);
            CrawledSites crawledSites = new CrawledSites();
            String currentDir = System.getProperty("user.dir");
            File seedsFile = new File(currentDir, args[0]);
            BufferedReader br = new BufferedReader(new FileReader(seedsFile));
            String line;
            while ((line = br.readLine()) != null) {
                crawledSites.addListOfSites(line);
               // System.out.println("adding ");
            }
                
            File visitedFile = new File(currentDir, args[3]);
            br = new BufferedReader(new FileReader(visitedFile));
            while ((line = br.readLine()) != null && line.trim().length()>0) {
                crawledSites.addCrawledSites(line,args[3]);
            }
            br.close();
            initializeCrawling(NumberOfThreads, crawledSites, 2000000000, args[3], args[1], args[2]);
        } else {
            System.out.println("The execution must be started in this form: java -jar dist/craw_simple.jar seedsFile.txt outDirectory associationFile.txt visitedLinks.txt numberOfThreads");

        }
    }
}
