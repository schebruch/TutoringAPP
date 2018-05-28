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

    /**
     * This tests if the user tries to pass in a non-existent section. A foreign
     * key should be violated
     */
    @Test
    public void testSectionNotExists() {
        Section s = new Section("AAA", 205, "Mon", "4:00", "Fall", 2018);
    }

    /**
     * This method tests if the section is properly loaded by checking DB after method call.
     */
    @Test
    public void testLoadSectionValid() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        Section s1 = new Section("CHM", 30, "Tues", "6:00", "Fall", 2018);
    }
    
    /**
     * Should fail if test is a success. Attempts insert of invalid sections
     */
    @Test
    public void testLoadSectionsInvalid()
    {
        Section s = new Section("MATH", 208, "Mon", "4:00", "Fall", 2018);
        Section s1 = new Section("MATHH", 205, "Mon", "4:00", "Fall", 2018);
    }
    

    /**
     * This method tests if 1 or more students is successfully loaded into the DB and
     * ArrayList of students
     */
    @Test
    public void testInsertStudents() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018); //should load the section
    //    Section s2 = new Section("ACCT", 151, "Mon", "6:00", "Fall", 2018);
        int prevSize = s.getStudents().size();
        Student student = new Student("Samuel", "Chebruch", "sac320@lehigh.edu", 112233221, false);
        Student student2 = new Student("Mike", "Smith", "mike.smith@lehigh.edu", 234543212, false);
        s.insertNewStudent(student);
        s.insertNewStudent(student2);
        int newSize = s.getStudents().size();
        assertEquals(prevSize + 2, newSize);
    }

    /**
     * Tests the skip count for a student not currently in the DB. Should return 0
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
        s.insertNewStudent(student);
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

    /**
     * tests to make sure the array list of students is cleared, and the database of students is cleared (that's tested
     * by inspection)
     */
    @Test
    public void testClearStudents() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        s.clearStudents();
        assertEquals(s.getStudents().size(), 0);

    }

}
