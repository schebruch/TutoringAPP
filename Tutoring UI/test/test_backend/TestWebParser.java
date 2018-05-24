/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_backend;

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
     * testGetTitle tests if the title of the HTML page can be read in correctly
     */
    public void testGetTitle()
    {
        WebScraper ws = new WebScraper();
        assertEquals(ws.getTitle(), "Courses with Tutoring | Student Affairs");
    }
    
    @Test
    /**
     * testParsing tests if parsing is completed correctly.
     * There should be a .csv file with correctly formated text in the working directory
     */
    public void testParsing()
    {
        WebScraper ws = new WebScraper();
        ws.loadStrings();
    }
}
