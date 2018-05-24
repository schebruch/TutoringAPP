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
import java.sql.*;

/**
 *
 * @author Samuel Chebruch
 */
public class TestTutor {
    
    private Statement s;
    private Connection con;

    public TestTutor() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
         try {
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    /**The purpose of this method is to test if we can obtain a suitable connection to Tutoring.db*/
    public void testConnection() {
        assertTrue(con != null);
        assertTrue(s != null); 
    }
    
    public void testGetNumSections()
    {
    
    }

}
