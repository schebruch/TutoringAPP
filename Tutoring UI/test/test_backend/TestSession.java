/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_backend;

import java.util.Date;
import java.sql.*;
import java.text.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.Section;
import tutoring.Session;
import tutoring.Student;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

        //GregorianCalendar(year, month - 1, day);
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);
    }

    /**
     * Tests if markAttended() updates database and Array List of attending
     * students consistently
     */
    @Test
    public void testMarkAttended() {

        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);
        int prevAttendedSize = session.getAttended().size();
        System.out.println("Size: " + session.getStudents().size());
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
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);
        int prevSkipSize = session.getMissing().size();
        System.out.println("prevSkipSize: " + session.getMissing().size());
        System.out.println("Num students in section: " + session.getStudents().size());
        for (int i = 0; i < session.getStudents().size(); i++) {
            Student current = session.getStudents().get(i);
            session.markSkipped(current, "NA");
            assertEquals(session.getSkipCount(current), 1);
        }
        int postSkipSize = session.getMissing().size();
        assertEquals(prevSkipSize + 2, postSkipSize);
        session.printMissing();
    }

    /**
     * This tests if removeAttended() removes the attended student from the
     * database
     */
    @Test
    public void testRemoveAttended() {
        
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);
        System.out.println(session.getStudents().get(0));
        session.removeAttended(session.getStudents().get(0));
    }

    /**
     * This tests if removeSkipped() works (removes the student from the list of
     * missing students, deletes the student from the SKIPS table, and updates
     * skip_count for that student if necessary
     */
    @Test
    public void testRemoveSkip() {

        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);

        System.out.println("missing size: " + session.getMissing().size());

        int prevSkippedListSize = session.getMissing().size();
        int prevSkipCount = session.getSkipCount(session.getMissing().get(0));
        session.removeSkipped(session.getMissing().get(0)); // removing the first student from skipped
        int postSkipCount = session.getSkipCount(session.getMissing().get(0));
        int postSkippedListSize = session.getMissing().size();
        assertEquals(prevSkippedListSize - 1, postSkippedListSize); //tests if the student was removed from the list
        assertEquals(postSkipCount, prevSkipCount - 1);

    }
}
