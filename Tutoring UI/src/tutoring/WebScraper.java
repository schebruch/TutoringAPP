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
 * A class that scrapes the Lehigh University web page for classes that are tutored: https://studentaffairs.lehigh.edu/content/courses-tutoring
 *
 * @author Cameron Zurmuhl, Lafayette College '20 E-mail: zurmuhlc@lafayette.edu Phone:267-945-6473 Github: https://github.com/20zurmca
 */
public class WebScraper {

    private Document doc; //the HTML document for parsing

    private Queue<String> allCourses; //the text as Strings from the HTML to process

    private ArrayList<Course> parsedCourses; //a list of all parsed courses to cross-check with the database

    /**
     * Constructor for class WebScraper
     */
    public WebScraper() {
        establishConnection();
        allCourses = new LinkedList<>();
        parsedCourses = new ArrayList<>();
        if (doc.title() != null) { //if connection is established
            loadStrings();
            parse();
        }
    }

    /**
     * Allows the user of a different class to add a course not parsed
     */
    /*
    public void addCourse(Course c) {
        parsedCourses.add(c);
    }*/

    /**
     * checkDB() cross references the database to perform necessary updates
     */
    public void checkDB() {
        Connection con = null;
        Statement s = null;
        //attemps DB connection
        try {
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (SQLException e) //DB Connection failed
        {
            System.out.println("DB connection failed");
            return;
        }

        boolean insertedSomething = false;
        //for each parsed course, check if it is already in the DB. Add it if not
        for (int i = 0; i < parsedCourses.size(); i++) {
            Course current = parsedCourses.get(i);
            // System.out.println(current.toString());
            String checkForExistingDuplicate = "select* from CLASS where course_num = " + Integer.parseInt(current.course_num) + " and subj_name = '" + current.subj_name + "'";
         //   if (Integer.parseInt(current.course_num) == 231) {
           //     System.out.println(checkForExistingDuplicate);
           // }
            try {
                ResultSet r = s.executeQuery(checkForExistingDuplicate);
                if (!r.next()) //if there are no existing courses in the DB
                {
                    System.out.println("Inserting: " + current.course_num + " " + current.subj_name);
                    insertCourse(s, Integer.parseInt(current.course_num), current.subj_name); //insert this course into the CLASS relation
                    insertedSomething = true;
                }
            }catch(SQLException e) //Query failed
            {
                //   System.out.println("Could not check for existing courses");
            }
        }
        if (!insertedSomething) {
            System.out.println("No new courses are being tutored");
        } else {
            System.out.println("We had to insert some courses");
        }
        System.out.println("All courses are up to date");
    }

    /**
     * connectionsEstablished() returns if this.doc has the web page
     *
     * @return if the connection to the doc was established by returning that the title != null
     */
    public boolean connectionEstablished() {
        return doc.title() != null;
    }

    /**
     * getTitle() returns the title of the HTML doc
     *
     * @return doc.title()
     */
    public String getTitle() {
        return doc.title();
    }

    /**
     * printCourseList() prints the list of courses that were parsed into the ArrayList
     */
    public void printCourseList() {
        for (Course c : parsedCourses) {
            System.out.println(c);
        }

    }

    /**
     * establishConnection() initializes a connection to the HTML file
     */
    private void establishConnection() {
        try {
            //connecting to the HTML document
            doc = Jsoup.connect("https://studentaffairs.lehigh.edu/content/courses-tutoring").get();
        } catch (IOException e) {
            System.out.println("Error: Could not access https://studentaffairs.lehigh.edu/content/courses-tutoring. Check Internet connection.");
            System.out.println(e.getLocalizedMessage());

        } catch (Exception e) {
            System.out.println("Exception occurred in class WebScraper: parseDocument: " + e.getLocalizedMessage());
        }
    }

    /**
     * loadStrings() parses the <div class = "body field> element in the HTML file for the tutored classes. These classes are loaded into this.classesAsStrings queue for processing
     */
    private void loadStrings() {
        Element classBody = doc.getElementsByAttributeValue("class", "body field").first(); //retrieving the element of the HTML code that has the courses 
        Elements classes = classBody.children(); //retrieving all child elements from classBody (obtaining individual courses)
        Iterator<Element> itr = classes.iterator();

        itr.next(); //skipping the first element b/c it doesn't have class info
        while (itr.hasNext()) {
            String html = itr.next().html();
            //string cleaning
            html = html.replaceAll("&amp;", "and")
                    .replaceAll("\\*", "")
                    .replaceAll("</p>", "")
                    .replaceAll(" +", " ")
                    .trim();

            String[] separatedClasses = html.split("<br>"); //split by break in the html

            for (int i = 1; i < separatedClasses.length; i++) //skipping the first index because it is a header
            {
                //formatting the string so that the course number, course subject, and course name are separated by '+' for easier parsing

                int spaceCounter = 0; //only want to substitute the first two spaces in the string with a '+'
                StringBuilder sb = new StringBuilder(separatedClasses[i]); //create a StringBuilder of the class string to easily change characters in the string

                for (int j = 0; j < sb.length(); j++) {
                    if (spaceCounter < 2) {
                        if ((int) sb.charAt(j) == 32) { //checking if the character in the class string is a space--only going to the first 2 spaces
                            sb.setCharAt(j, '+');
                            spaceCounter++;
                        }
                    }
                }
                allCourses.add(sb.toString());
            }
        }
    }

    /**
     * parse() processes the allClasses queue and adds the courses to an ArrayList
     */
    private void parse() {
        while (!allCourses.isEmpty()) {
            String[] course = allCourses.poll().split("\\+");

            /* The course array is arranged as following:
                     subj_name   is course[0]
                     course_num  is course[1]
             */
            Course c = new Course(course[1], course[0]);
            //create a course by handling appropriate cases
             if (hasDoubleCourse(c.course_num)) {
                 processDoubleCourse(c);
            } else {
                parsedCourses.add(c);
            }
        }
    }

    /**
     * processDoubleCourse() processes a situation where there is two courses in one line
     *
     * @param c the course that contains two courses in a course name
     */
    private void processDoubleCourse(Course c) {
        String courseSubject = c.subj_name;

        //separating course number by '/' character ie. 51/52 will be separated into 51 52
        String first_num = c.course_num.substring(0, c.course_num.indexOf('/'));
        String second_num = c.course_num.substring(c.course_num.indexOf('/') + 1, c.course_num.length());

        //adding to array list of courses
        Course one = new Course(first_num, courseSubject);
        Course two = new Course(second_num, courseSubject);
                
        parsedCourses.add(one);
        parsedCourses.add(two);

    }

    /**
     * hasDoubleCourse() checks if a course number contains a '/' character, indicating two courses were described in one line
     *
     * @param courseNum the course number as a string
     * @return whether the course number contains '/'
     */
    private boolean hasDoubleCourse(String courseNum) {
        for (int i = 0; i < courseNum.length(); i++) {
            if (courseNum.charAt(i) == '/') {
                return true;
            }
        }
        return false;
    }

    /**
     * insertCourse() inserts a course that needs to be inserted into the DB
     */
    private void insertCourse(Statement s, int course_num, String subj_name) {
        String insertThisCourse = "insert into CLASS(course_num, subj_name) values(" + course_num + ", '" + subj_name + "')";
        try {
            s.executeUpdate(insertThisCourse);
        } catch (SQLException e) //insert failed
        {
            System.out.println("Insert failed");
        }
    }

    /**
     * Course represents a course from the Website
     */
    private static class Course {

        String course_num;     //course number
        String subj_name; //for example: MATH

        public Course(String num, String subjName) {
            course_num = num;
            subj_name = subjName;
        }

        @Override
        public String toString() {
            return course_num + " " + subj_name;
        }

    }
}
