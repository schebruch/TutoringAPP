package tutoring;

import java.io.IOException;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.LinkedList;
import java.util.ArrayList;
import java.sql.*;
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
    
    private Queue<String> allCourses; //the text as Strings from the HTML to process
    
    private ArrayList<Course> parsedCourses; //a list of all parsed courses to cross-check with the database
    
    /**
     * Constructor for class WebScraper
     */
    public WebScraper()
    {
        establishConnection();
        allCourses = new LinkedList<>();
        
        if(doc.title() != null){ //if connection is established
            loadStrings();
            parse();
        }
    }
    
    /**
     * checkDB() cross references the database to perform necessary updates
     */
    public void checkDB()
    {
        
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
     * queueSize() returns the size of the allClasses queue
     * @return allClasses.size()
     */
    public int queueSize()
    {
        return allCourses.size();
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
     * establishConnection initializes a connection to the HTML file
     */
    private void establishConnection()
    {
        try{
            //connecting to the HTML document
            doc = Jsoup.connect("https://studentaffairs.lehigh.edu/content/courses-tutoring").get();
        }catch(IOException e)
        {
            System.out.println("Error: Could not access https://studentaffairs.lehigh.edu/content/courses-tutoring. Check Internet connection.");
            System.out.println(e.getLocalizedMessage());

        }catch(Exception e)
        {
            System.out.println("Exception occurred in class WebScraper: parseDocument: " + e.getLocalizedMessage());
        }
    }
    
    
    /**
     * loadStrings parses the <div class = "body field> element in the HTML file for the tutored classes.
     * These classes are loaded into this.classesAsStrings queue for processing
     */
    private void loadStrings()
    {
        Element classBody = doc.getElementsByAttributeValue("class", "body field").first(); //retrieving the element of the HTML code that has the courses 
        Elements classes = classBody.children(); //retrieving all child elements from classBody (obtaining individual courses)
        Iterator<Element> itr = classes.iterator();
        
        itr.next(); //skipping the first element b/c it doesn't have class info
        while(itr.hasNext())
        {
            String html = itr.next().html();
            html = html.replaceAll("&amp;", "and").replaceAll("\\*", "").replaceAll("</p>", "").replaceAll(" +", " ").trim(); //string cleaning
            String [] separatedClasses = html.split("<br>"); //split by break in the html
            
            for(int i = 1; i < separatedClasses.length; i++) //skipping the first index because it is a header
            {
                //formatting the string so that the course number, course subject, and course name are separated by '+' for easier parsing
                
                int spaceCounter = 0; //only want to substitute the first two spaces in the string with a '+'
                StringBuilder sb = new StringBuilder(separatedClasses[i]); //create a StringBuilder of the class string to easily change characters in the string
                
                for(int j = 0; j<sb.length(); j++)
                {
                    if(spaceCounter < 2 ){
                        if((int)sb.charAt(j) == 32) { //checking if the character in the class string is a space--only going to the first 2 spaces
                            sb.setCharAt(j, '+');
                            spaceCounter++;
                        }
                    }   
                }
                
                System.out.println(sb.toString());
                allCourses.add(sb.toString());
            }
        }
       
    }
     
    /**
     * parse() processes the allClasses queue and adds the courses to an ArrayList 
     */
    private void parse()
    {
        while(!allCourses.isEmpty())
        {
            String [] course = allCourses.poll().split("\\+");
            
            //create a course by handling appropriate cases
            if(course.length == 2) //no course name
            {
                Course c = new Course(Integer.parseInt(course[1]), course[0], null);
                
                //check for a double course in one line situation
                
                
            } else {
                Course c = new Course(Integer.parseInt(course[1]), course[0], course[2]);

            }
        }
    }
    
    /**
     * processDoubleCourse() processes a situation where there is two courses in one line
     * @param c the course that contains two courses in a course name
     */
    private void processDoubleCourse(Course c)
    {
        
    }
    
    
    /**
     * Class represents a course from the Website
     */
    private static class Course {
        int courseNum;     //course number
        String courseName; //course name
        String courseAbbr; //course abbreviation
        
        public Course(int num, String name, String abr)
        {
            courseNum = num;
            courseName = name;
            courseAbbr = abr;
        }
        
    }
}
