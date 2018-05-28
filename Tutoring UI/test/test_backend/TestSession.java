/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_backend;

import java.util.Date;
import java.sql.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.Section;
import tutoring.Session;
import tutoring.Student;

/**
 *
 * @author sam
 */
public class TestSession {

    public TestSession() {
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
    /**
     * Tests successful creation of Session object and DB consistency
     */
    @Test
    public void createSessionObject() {

        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        Date d = new Date();
        Session session = new Session(s, d);
    }

    /**
     * Tests if markAttended() updates database and Array List of attending
     * students consistently
     */
    @Test
    public void testMarkAttended() {

        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        Date d = new Date();
        Session session = new Session(s, d);
        int prevAttendedSize = session.getAttended().size();
        for (int i = 0; i < session.getStudents().size(); i++) {
            Student current = session.getStudents().get(i);
            session.markAttended(current);
        }
        int postAttendedSize = session.getAttended().size();
        assertEquals(prevAttendedSize + 2, postAttendedSize);
        session.printAttended();
    }

    /**
     * Tests if missing students are added to the array, the database, and if
     * skip count for those students in the database is updated properly for
     * students with NA reasons
     */
    @Test
    public void testMarkAbsent() {
        
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        Date d = new Date();
        Session session = new Session(s, d);
        int initialSkipCount = 0;
        int prevSkipSize = session.getMissing().size();
        for (int i = 0; i < session.getStudents().size(); i++) {
            Student current = session.getStudents().get(i);
            session.markSkipped(current, "NA");
            assertEquals(session.getSkipCount(current), initialSkipCount+1);
        }
        int postSkipSize = session.getMissing().size();
        assertEquals(prevSkipSize + 2, postSkipSize);
    }
}
