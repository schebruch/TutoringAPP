package test_backend;


/*
 * To start a J-Unit test for your project: right click on source packages -->new --> JUnit test
 * To add more J-Unit tests for your other classes, right click on <default package> under Test Packages and add a new JUnit test
 * Name your test classes Test*ClassName* ex) TestStudent
 */
import java.sql.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.Student;

/**
 *
 * @author Cameron Zurmuhl
 * @author Samuel Chebruch
 */
public class TestStudent {

    //don't worry about these things--they are just places to set up your environment.
    public TestStudent() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before //meaning before the test
    public void setUp() {
    }

    @After //meaning after the test
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testGetAthleteStatus() {
        //create a new student
        Student s = new Student("Zurmuhl", "Cameron", "zurmuhlc@lafayette.edu", 342522343, false);
        boolean isAthlete = s.getAthleteStatus();
        assertEquals(isAthlete, false); //using JUnit's "assertEquals" method to make sure the correct boolean was set and returned

        //run the file to see results. Green bar means passed test and method works
        //remember it is good to run multiple tests for one method--test the boundary cases not just the straight forward
        //JUnit assert API can be found here https://junit.org/junit4/javadoc/latest/
    }

    /**
     * Tests the skip count for a student not currently in the DB. Should return 0
     */
    @Test
    public void testGetSkipCountStudentNotExist() {
        Student student = new Student("Donald", "Trump", "schebruch@gmail.com", 123456789, false);
        Connection con = null;
        Statement s = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Skip count for non-existing student Donald Trump: " + student.getSkipCount(s));
    }

    /**
     * Tests the skip count for a student in the DB.  I verified with the DB that this passes
     */
    @Test
    public void testGetSkipCountStudentExists() {
        Student student = new Student("Cameron", "Zurmuhl", "zurmuhlc@lafayette.edu", 826568676, false);
        Student student2 = new Student("Sam", "Chebruch", "schebruch@gmail.com", 111111112, false);
        Student student3 = new Student("Sean", "King", "sean.king@lehigh.edu", 111111111, true);
        Connection con = null;
        Statement s = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Skip count for existing student Cameron Zurmuhl: " + student.getSkipCount(s));
        System.out.println("Skip count for existing student Sam Chebruch: " + student2.getSkipCount(s));
        System.out.println("Skip count for existing student Sean King: " + student3.getSkipCount(s));    
    }
}
