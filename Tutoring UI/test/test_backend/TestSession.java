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
import tutoring.*;

/**
 *
 * @author sam
 */
public class TestSession {

    private static Statement s;
    private static Connection con = Tutor.getConnection();

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
        try {

            //  Class.forName("org.sqlite.JDBC");
            //  con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Could not connect");
            //  System.exit(0);
        }
    }

    @After
    public void tearDown() {
    }

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

        int prevRowSize = getRowCount("INSTRUCTS");

        deleteSession(session);

        int postRowSize = getRowCount("INSTRUCTS");

        assertEquals(prevRowSize, postRowSize + 1);
    }

    /**
     * Tests if markAttended() updates database and Array List of attending
     * students consistently
     */
    @Test
    public void testMarkAttended() {

        //create the existing session
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);

        //insert 3 students into Section s
        Student student = new Student("Samuel", "Chebruch", "sac320@lehigh.edu", 112233221, false);
        Student student2 = new Student("Mike", "Smith", "mike.smith@lehigh.edu", 234543212, false);
        Student student3 = new Student("Ronny", "Davis", "ron.davis@tamu.edu", 123456554, true);

        s.insertNewStudent(student);
        s.insertNewStudent(student2);
        s.insertNewStudent(student3);

        //assert that there are 3 students now
        assertEquals(s.getStudents().size(), 3);

        int prevAttendedSize = session.getAttended().size();

        for (int i = 0; i < session.getStudents().size(); i++) {
            Student current = session.getStudents().get(i);
            session.markAttended(current);
        }
        int postAttendedSize = session.getAttended().size();
        assertEquals(prevAttendedSize + 3, postAttendedSize);

        //delete the students in ATTENDS for re-use
        for (int i = 0; i < session.getAttended().size(); i++) {

            deleteAttended(session, session.getAttended().get(i));

        }

        assertEquals(getRowCount("ATTENDS"), 0);
        //clears the list
        session.clearAttended();
        assertEquals(session.getAttended().size(), 0);
    }

    /**
     * Tests if the removeAttended() works by verifying that the student is
     * removed from the Array List and the student is no longer in the ATTENDED
     * relation
     */
    @Test
    public void testRemoveAttended() {

        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);

        //insert a temporary student for removal purposes
        Student student = new Student("Samuel", "Chebruch", "sac320@lehigh.edu", 112233221, false);
        s.insertNewStudent(student);
        session.markAttended(student);

        int prevAttendedSize = session.getAttended().size();
        int prevRowSize = getRowCount("ATTENDS where " + concatPKOfSession(session));

        //remove the first student from the attended list as a test
        session.removeAttended(session.getAttended().get(0));

        int postAttendedSize = session.getAttended().size();
        int postRowSize = getRowCount("ATTENDS where " + concatPKOfSession(session));

        //assert that one student was removed from the list
        assertEquals(prevAttendedSize, postAttendedSize + 1);
        assertEquals(postRowSize + 1, prevRowSize);

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
        int idx = session.getStudents().size();
        for (int i = 0; i < idx; i++) {
            Student current = session.getStudents().get(i);
            session.markSkipped(current, "NA");
            assertEquals(session.getSkipCount(current), 1);
        }
        int postSkipSize = session.getMissing().size();
        assertEquals(prevSkipSize + 3, postSkipSize);
    }

    /**
     * This tests if removeSkipped() works (removes the student from the list of
     * missing students, deletes the student from the SKIPS table, and updates
     * skip_count for that student if necessary)
     */
    @Test
    public void testRemoveSkip() {

        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Calendar cal = new GregorianCalendar(2018, 4, 28);
        String date = sdf.format(cal.getTime());
        Session session = new Session(s, date);


        int prevSkippedListSize = session.getMissing().size();

        Student current = session.getMissing().get(0);
        session.removeSkipped(current);

        int postSkippedListSize = session.getMissing().size();
        assertEquals(2, postSkippedListSize); //tests if the student was removed from the list

    }

    /**
     * Helper for where clauses involving a particular
     *
     * @param Session s
     * @return
     */
    private String concatPKOfSession(Session s) {
        return "subj_name = '" + s.getSubj() + "' and course_num = " + s.getCourseNum() + " and Day_of_Week = '" + s.getDay() + "' and time_held = '" + s.getTime() + "' and semester = '" + s.getSemester() + "' and year = " + s.getYear();
    }

    private void deleteSession(Session s) {
        String deleteUpdate = "delete from INSTRUCTS where date_held =  '" + s.getDateOfSession() + "' and " + concatPKOfSession(s);
        try {
            TestSession.s.executeUpdate(deleteUpdate);
        } catch (SQLException e) {
            System.out.println("Could not delete the session from INSTRUCTS");
        }
    }

    /**
     * Helper method to get rid of
     *
     * @param studet from
     * @param s
     *
     */
    private void deleteAttended(Session s, Student student) {

        String deleteUpdate = "delete from ATTENDS where date_attended =  '" + s.getDateOfSession() + "' and LIN = " + student.getLIN() + " and " + concatPKOfSession(s);
        try {
            this.s.executeUpdate(deleteUpdate);
        } catch (SQLException e) {
            System.out.println("Could not delete from ATTENDS");
        }
    }

    /**
     *
     * @param tableName
     * @return num rows
     */
    private int getRowCount(String tableName) {
        int rowCount = -1;
        String numRows = "select count(*) from " + tableName;
        try {
            ResultSet r = s.executeQuery(numRows);
            r.next();
            rowCount = r.getInt(1);
        } catch (SQLException e) {
            System.out.println("Couldn't get number of rows");
        }
        return rowCount;
    }
}
