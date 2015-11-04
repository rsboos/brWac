package pagecontent;

import crawler.CrawledSites;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: iceman
 * Date: 9/10/12
 * Time: 4:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class FetchPage {

    public static void getContent(String url,CrawledSites crawledSites, int nome_arquivo, String outDirectory) throws MalformedURLException {
        boolean raiseFlag=false;
        String text=null;
        String currentDir = System.getProperty("user.dir");
        URL urlToGet = new URL(url);
        File outDir = new File(currentDir,outDirectory);
        //System.out.println(urlToGet);
        try{
        	crawledSites.addCrawledSites(url);
        	System.out.println("Viziou " + urlToGet);
            text = ArticleExtractor.INSTANCE.getText(urlToGet);
            
	    int fileName = text.hashCode();
	    
	    File outFile = new File(outDir,Integer.toString(fileName));
	    if (!outFile.exists()) {
            PrintWriter out = new PrintWriter(outFile);
            out.println(text);
            if (out != null) {
                            out.close();
                            if (outFile.length() < 5000L){
                            	System.out.println("DELETED (<5000L): " + text.length() + "\t" + urlToGet);
                                outFile.delete();
                            }else if (outFile.length() > 200000L){
                            	outFile.delete();
                            	System.out.println("DELETED (>200000L): " + text.length() + "\t" + urlToGet);
                            }
            }
	    }
        }
        catch(Exception e){
        	System.out.println("URL Failed: "+urlToGet);
            e.printStackTrace();
        }
        finally{
          //  if(raiseFlag==false)
            //    crawledSites.addContent(url,text);

            
        }
        
    }

}
