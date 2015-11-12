package pagecontent;

import crawler.CrawledSites;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchPage {

    public static void getContent(String url, CrawledSites crawledSites, String outDirectory, String fileVisitedLinks, String fileAssociation) throws MalformedURLException, IOException {
        String text = null;
        String currentDir = System.getProperty("user.dir");
        URL urlToGet = new URL(url);
        File outDir = new File(currentDir, outDirectory);
        crawledSites.addCrawledSites(url, fileVisitedLinks);
        try {
            text = ArticleExtractor.INSTANCE.getText(urlToGet);
            int fileName = text.hashCode();
            File outFile = new File(outDir, Integer.toString(fileName));
            if (!outFile.exists()) {
                
                PrintWriter out = new PrintWriter(outFile);
                out.println(text);
                if (out != null) {
                    out.close();
                    if (outFile.length() < 5000L) {
                        System.out.println("DELETED (<5000L): " + text.length() + "\t" + urlToGet);
                        outFile.delete();
                    } else if (outFile.length() > 200000L) {
                        outFile.delete();
                        System.out.println("DELETED (>200000L): " + text.length() + "\t" + urlToGet);
                    }
                    else {
                        File fileOut = new File(currentDir, fileAssociation);
                        BufferedWriter outf = new BufferedWriter(new FileWriter(fileOut));
                        outf.write(fileName + "\t" + crawledSites.normalizeURL(url)+"\n");
                        outf.close();
                       
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("URL Failed: " + urlToGet);
            e.printStackTrace();
        } finally {
           
        }

    }

}
