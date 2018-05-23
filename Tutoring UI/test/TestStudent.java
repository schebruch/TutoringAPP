
/*
 * To start a J-Unit test for your project: right click on source packages -->new --> JUnit test
 * To add more J-Unit tests for your other classes, right click on <default package> under Test Packages and add a new JUnit test
 * Name your test classes Test*ClassName* ex) TestStudent
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.ui.Student;

/**
 *
 * @author Cameron Zurmuhl
 * @author Samuel Cheburch
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
    // @Test
    // public void hello() {}
    
    //this is where the testing happens
    @Test
    public void testGetAthleteStatus()
    {
        //create a new student
        Student s = new Student("Zurmuhl", "Cameron", "zurmuhlc@lafayette.edu", 34252, false);
        boolean isAthlete = s.getAthleteStatus();
        assertEquals(isAthlete, false); //using JUnit's "assertEquals" method to make sure the correct boolean was set and returned
        
        //run the file to see results. Green bar means passed test and method works
        //remember it is good to run multiple tests for one method--test the boundary cases not just the straight forward
        //JUnit assert API can be found here https://junit.org/junit4/javadoc/latest/
    }
}
