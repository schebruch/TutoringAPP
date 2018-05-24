package tutoring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.LinkedList;
import java.util.Queue;
/**
 *A class that scrapes the Lehigh University web page for classes that are tutored: https://studentaffairs.lehigh.edu/content/courses-tutoring
 * @author Cameron Zurmuhl, Lafayette College '20
 * E-mail: zurmuhlc@lafayette.edu
 * Phone:267-945-6473
 * Github: https://github.com/20zurmca
 */
public class WebScraper {
    
    private Document doc; //the HTML document for parsing
    
    private Queue<String> allClasses; //the text as Strings from the HTML to process
    /**
     * Constructor for class WebScraper
     */
    public WebScraper()
    {
        establishConnection();
        allClasses = new LinkedList<String>();
    }
    
    /**
     * establishConnection initializes a connection to the HTML file
     */
    private void establishConnection()
    {
        try{
        //connecting to the HTML document
        doc = Jsoup.connect("https://studentaffairs.lehigh.edu/content/courses-tutoring").get();
        
        }catch(IOException e)
        {
            System.out.println("IOException occurred in class WebScraper: parseDocument: " + e.getLocalizedMessage());
        }catch(Exception e)
        {
            System.out.println("Exception occurred in class WebScraper: parseDocument: " + e.getLocalizedMessage());
        }
    }
    
    /**
     * connectionsEstablished() returns if this.doc has the web page
     * @return if the connection to the doc was established by returning that the title != null
     */
    public boolean connectionEstablished()
    {    
        return doc.title() != null;  
    }
    
    /**
     * getTitle() returns the title of the HTML doc
     * @return doc.title()
     */
    public String getTitle()
    {
        return doc.title();
    }
        
    
    /**
     * loadStrings parses the <div class = "body field> element in the HTML file for the tutored classes.
     * These classes are loaded into this.classesAsStrings queue for processing
     */
    public void loadStrings()
    {
        Element classBody = doc.getElementsByAttributeValue("class", "body field").first(); //retrieving the section of the HTML code that has the courses 
        Elements classes = classBody.children(); //retrieving all child elements from classBody (obtaining individual courses)
        Iterator<Element> itr = classes.iterator();
        
        itr.next(); //skipping the first element b/c it doesn't have class info
        while(itr.hasNext())
        {
            String html = itr.next().html();
            html = html.replaceAll("&amp;", "and").replaceAll("\\*", "").replaceAll("</p>", ""); //string cleaning
            String [] separatedClasses = html.split("<br>"); //split by break in the html
            
            for(int i = 1; i < separatedClasses.length; i++) //skipping the first index because it is a header
            {
                System.out.println(separatedClasses[i]);
                allClasses.add(separatedClasses[i]);
            }
        }
       
    }
}
