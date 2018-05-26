/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_backend;

import java.sql.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.Section;
import tutoring.Student;

/**
 *
 * @author Sam Chebruch
 */
public class TestSection {

    private static Statement s;
    private static Connection con;

    public TestSection() {

    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        /*
        try {

            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Could not connect");
            System.exit(0);
        }*/
    }

    @After
    public void tearDown() {
    }

    @Test
    /**
     * Testing to make sure Connection is successful
     */
    public void testConnection() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
    }

    /**
     * This method tests if the section is properly loaded if it's not already
     * in the DB. Verified by checking DB after method call.
     */
    @Test
    public void testLoadSectionNotYetDB() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
    }

    /**
     * Tests if everything is still fine in the DB if the section is already
     * loaded
     */
    @Test
    public void testLoadSectionAlreadyInDB() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
    }

    /**
     * This method tests if a student is successfully loaded into the DB and
     * ArrayList of students
     */
    @Test
    public void testInsertStudent() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
        int prevSize = s.getStudents().size();
        Student student = new Student("Samuel", "Chebruch", "sac320@lehigh.edu", 112233221, false);
        s.insertNewStudent(student);
        int newSize = s.getStudents().size();
        assertEquals(prevSize + 1, newSize);
    }

    //@Test
    /**
     * helper method to get number of instances of a relation. used for various
     * tests
     */
    public int getNumInstances(String tableName) {
        //connect to DB
        tableName = "CLASS";
        String rows = "select count(*) as TOTAL from " + tableName;
        ResultSet r = null;
        int numInstances = 0;
        try {
            r = s.executeQuery(rows);
            r.next();
            numInstances = r.getInt("TOTAL");
            System.out.println(numInstances);
        } catch (SQLException e) {
        }
        assertEquals(Integer.parseInt(rows), 45);
        return Integer.parseInt(rows);
    }

    /**
     * Tests the skip count for a student not currently in the DB. Should return
     *
     */
    @Test
    public void testGetSkipCountStudentNotExist() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        Student student = new Student("Donald", "Trump", "schebruch@gmail.com", 123456789, false);
        assertEquals(s.getSkipCount(student), 0);
    }

    /**
     * Tests the skip count for a student in the DB. I verified with the DB that
     * this passes
     */
    @Test
    public void testGetSkipCountStudentExists() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        Student student = new Student("Samuel", "Chebruch", "sac320@lehigh.edu", 112233221, false);
        assertEquals(s.getSkipCount(student), 2); //passes
        assert (s.getStudents().contains(student));
    }

    /**
     * Ensures the correct skip count is returned for a skip count > 0
     */
    @Test
    public void testGetSkipCountGreaterThanZero() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        Student student = new Student("Sam", "Chebruch", "schebruch@gmail.com", 112233221, false);
        assertEquals(s.getSkipCount(student), 2);
    }

    /**
     * Tests to make sure the specified student is removed from the Array List,
     * and the ENROLLED_IN and STUDENT relations. Note, to use, the student must
     * already exist.
     */
    @Test
    public void testRemoveStudent() {
        Student student = new Student("Sam", "Chebruch", "schebruch@gmail.com", 112233221, false);
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        int prevSize = 0;
        if (s.getStudents().size() > 0) {
            prevSize = s.getStudents().size();
            s.removeStudent(student);
            assertEquals(s.getStudents().size(), prevSize - 1);
        } else {
            assertEquals(s.getStudents().size(), prevSize);
        }
    }

    @Test
    public void testClearStudents() {
        Student student = new Student("Sam", "Chebruch", "schebruch@gmail.com", 112233221, false);
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        s.clearStudents();
        assertEquals(s.getStudents().size(), 0);
        
    }

}
