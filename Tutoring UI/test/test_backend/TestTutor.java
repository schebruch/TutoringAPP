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
 * @author scheb
 */
public class TestTutor {

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
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void testConnection() {
        Connection con = null;
        Statement s = null;
        try {
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (SQLException e) {
            e.printStackTrace();
           // System.exit(0);
        }
        assertFalse(con == null);
        
    }

}
