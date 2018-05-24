/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_backend;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.WebScraper;

/**
 *
 * @author cameron
 */
public class TestWebParser {
    
    public TestWebParser() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    /**
     * testParserConnection() tests if a WebScraper object can establish a connection to the html file on the web
     */
    public void testParserConnection()
    {
      WebScraper ws = new WebScraper();
      assertTrue(ws.connectionEstablished());
    }
    
    @Test
    /**
     * testGetTitle() tests if the title of the HTML page can be read in correctly
     */
    public void testGetTitle()
    {
        WebScraper ws = new WebScraper();
        assertEquals(ws.getTitle(), "Courses with Tutoring | Student Affairs");
    }

            
    @Test
    /**
     * testParsing() tests if parsing is completed correctly.
     * To pass the test, the programmer should inspect that the array list of classes were loaded correctly by print inspection
     */
    public void testParsing()
    {
        WebScraper ws = new WebScraper();
        ws.printCourseList();
    }
    
    @Test
    /**
     * testNoInternet() tests if the HTML strings would be loaded if Internet is down
     * If this test passes, that means an exception was thrown
     */
    public void testNoInternet()
    {
        //WebScraper ws = new WebScraper(); //should throw an exception upon construction when wi-fi is turned off
    }
    
}
