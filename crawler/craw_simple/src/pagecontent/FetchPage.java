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

    public static void getContent(String url,CrawledSites crawledSites, int nome_arquivo) throws MalformedURLException {
        boolean raiseFlag=false;
        String text=null;
        String currentDir = System.getProperty("user.dir");
        URL urlToGet = new URL(url);
        File outDir = new File(currentDir,"out/");
        //System.out.println(urlToGet);
        try{
            text = ArticleExtractor.INSTANCE.getText(urlToGet);
	    fileName = text.hashCode();
	    File outFile = new File(outDir,fileName);
            PrintWriter out = new PrintWriter(outFile);
            out.println(text);
            if (out != null) {
                            out.close();
                            if (outFile.length() < 5000L)
                                outFile.delete();
                            else if (outFile.length() > 200000L)
            			outFile.delete();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
          //  if(raiseFlag==false)
            //    crawledSites.addContent(url,text);

            
        }
        
    }

}
