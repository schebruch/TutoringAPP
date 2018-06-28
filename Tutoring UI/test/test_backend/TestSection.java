/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_backend;

import java.sql.*;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import tutoring.Section;
import tutoring.*;

/**
 *
 * @author Sam Chebruch
 */
public class TestSection {

    private static Statement s;
    private static Connection con = Tutor.getConnection();

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
     * This tests if the user tries to pass in a non-existent section (no such
     * subject). No rows are added to the Section relation since there is an
     * assertion in the Section constructor that prohibits an invalid course
     * being entered.
     */
    @Test
    public void testSectionNotExists() {

        Section s = new Section("AAA", 205, "Mon", "4:00", "Fall", 2018);
        //ensures no rows are added to the relation by inspection
    }

    /**
     * This tests if the user tries to pass in a non-existent section (no course
     * number for the given subject). No rows are added to the Section relation
     * since there is an assertion in the Section constructor that prohibits an
     * invalid course being entered.
     */
    @Test
    public void testSectionNotExists2() {
        Section s = new Section("MATH", 200, "Mon", "4:00", "Fall", 2018);
        //ensures no rows are added to the relation by inspection
    }

    /**
     * This method tests if the section is properly loaded by checking DB after
     * method call. Ensures that there is one additional row in the section
     * relation, and then removes that row for reuse of this test
     */
    @Test
    public void testLoadSectionValid() {
        int previousRowCount = getRowCount("Section"); //gets number of rows in Section relation
        assertTrue(previousRowCount >= 0);

        //if we create 2 sections that already exist, then there should be no change in row count
        if (sectionExists("MATH", 205, "Mon", "4:00", "Fall", 2018) && sectionExists("CHM", 30, "Tues", "6:00", "Fall", 2018)) {
            Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
            Section s1 = new Section("CHM", 30, "Tues", "6:00", "Fall", 2018);
            int postRowCount = getRowCount("Section");
            assertEquals(previousRowCount, postRowCount);
        }

        //This section does not exist and should add to the existing row count. This section is then deleted to reuse this Junit test.
        Section s2 = new Section("MATH", 21, "Mon", "8:00", "Fall", 2018);
        int postRowCount = getRowCount("Section");
        assertEquals(previousRowCount + 1, postRowCount);

        deleteSection(s2);
        assertEquals(postRowCount - 1, getRowCount("SECTION"));
    }

    /**
     * This method tests if 1 or more students is successfully loaded into the
     * DB and ArrayList of students. Students deleted at the end of the method
     * for reuse.
     */
    @Test
    public void testInsertStudents() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        Section s1 = new Section("CHM", 30, "Tues", "6:00", "Fall", 2018);

        int prevSizeS = s.getStudents().size();
        int prevSizeS1 = s1.getStudents().size();
        if (numEnrolled(s) == 0) {
            assertEquals(prevSizeS, 0);
        }

        Student student = new Student("Samuel", "Chebruch", "sac320@lehigh.edu", 112233221, false);
        Student student2 = new Student("Mike", "Smith", "mike.smith@lehigh.edu", 234543212, false);
        Student student3 = new Student("Ronny", "Davis", "ron.davis@tamu.edu", 123456554, true);

        //insert 3 students into Section s
        s.insertNewStudent(student);
        s.insertNewStudent(student2);
        s.insertNewStudent(student3);

        assertEquals(numEnrolled(s), 3);
        assertEquals(numEnrolled(s1), 0);

        //insert Student student into Section s1
        s1.insertNewStudent(student);

        //assert 1 student is in s1
        assertEquals(numEnrolled(s1), 1);

        //asserts array list sizes are consistent
        assertEquals(prevSizeS + 3, s.getStudents().size());
        assertEquals(prevSizeS1 + 1, s1.getStudents().size());

        for (int i = 0; i < s.getStudents().size(); i++) {
            String deleteFromEnrolled = "delete from ENROLLED_IN where subj_name =  '" + s.getSubj() + "' and course_num = " + s.getCourseNum() + " and Day_of_Week = '" + s.getDay() + "' and time_held = '" + s.getTime() + "' and semester = '" + s.getSemester() + "' and year = " + s.getYear() + " and LIN = " + s.getStudents().get(i).getLIN();
            try {
                TestSection.s.executeUpdate(deleteFromEnrolled);
            } catch (SQLException e) {
                System.out.println("Could not delete the student from enrolled_in");
            }
        }

        for (int i = 0; i < s1.getStudents().size(); i++) {
            String deleteFromEnrolled = "delete from ENROLLED_IN where subj_name =  '" + s1.getSubj() + "' and course_num = " + s1.getCourseNum() + " and Day_of_Week = '" + s1.getDay() + "' and time_held = '" + s1.getTime() + "' and semester = '" + s1.getSemester() + "' and year = " + s1.getYear() + " and LIN = " + s1.getStudents().get(i).getLIN();
            try {
                TestSection.s.executeUpdate(deleteFromEnrolled);
            } catch (SQLException e) {
                System.out.println("Could not delete the student from enrolled_in");
            }
        }
    }

    /**
     * Tests the skip count for a student not currently in the DB. Should return
     * 0
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
        assertEquals(s.getSkipCount(student), 0); //passes
        assert (s.getStudents().contains(student));
    }

    /**
     * Ensures the correct skip count is returned for a skip count > 0
     */
    @Test
    public void testGetSkipCountGreaterThanZero() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        Student student = new Student("Sam", "Chebruch", "schebruch@gmail.com", 112233221, false);
        s.setSkipCount(student, 2);
        assertEquals(s.getSkipCount(student), 2);
    }

    /**
     * Tests to make sure the specified student is removed from the Array List,
     * and the ENROLLED_IN relation. Note, to use, the student must
     * already exist.
     */
    @Test
    public void testRemoveStudent() {
        Student student = new Student("Sam", "Chebruch", "schebruch@gmail.com", 112233221, false);
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        int prevSize = 0;
        if (s.getStudents().size() > 0) {
            int prevRowCount = numEnrolled(s);
            prevSize = s.getStudents().size();
            s.removeStudent(student);
            int postRowCount = numEnrolled(s);
            assertEquals(s.getStudents().size(), prevSize - 1);
            assertEquals(postRowCount + 1, prevRowCount); //ensures this student was removed from ENROLLED_IN
            
            //add the student back for re-use of unit test
            s.insertNewStudent(student);
            
            //ensure the student was added back to the ArrayList
            assertEquals(prevSize, s.getStudents().size());
        } else {
            assertEquals(s.getStudents().size(), prevSize);
        }
        
    }

    /**
     * tests to make sure the ArrayList of students is cleared, and the
     * database of students is cleared (that's tested by inspection)
     */
    @Test
    public void testClearStudents() {
        Section s = new Section("MATH", 205, "Mon", "4:00", "Fall", 2018);
        ArrayList<Student> addAfterDeletion = new ArrayList<>(s.getStudents());
        
        s.clearStudents();
        assertEquals(s.getStudents().size(), 0);
        assertEquals(numEnrolled(s), 0);
        
        //add back the students
        for(int i = 0; i < addAfterDeletion.size(); i++)
        {
            s.insertNewStudent(addAfterDeletion.get(i));
        }      
        
        assertEquals(numEnrolled(s), s.getStudents().size());
    }

    /**
     * Helper to test whether a section of the given parameters exist
     */
    private boolean sectionExists(String subject, int courseNum, String day, String time, String semester, int year) {
        String sections = "select * from SECTION";
        try {
            ResultSet r = s.executeQuery(sections);
            while (r.next()) {
                if (r.getString("subj_name").equals(subject) && r.getInt("course_num") == (courseNum) && r.getString("Day_of_Week").equals(day) && r.getString("time_held").equals(time) && r.getString("semester").equals(semester) && r.getInt("year") == year) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Couldn't get SECTION tuples");
        }
        return false;
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

    /**
     * Deletes a section from SECTION
     */
    private void deleteSection(Section sec) {
        String deleteUpdate = "delete from SECTION where subj_name = '" + sec.getSubj() + "' and course_num = " + sec.getCourseNum() + " and Day_of_Week = '" + sec.getDay() + "' and time_held = '" + sec.getTime() + "' and semester = '" + sec.getSemester() + "' and year = " + sec.getYear();
        try {
            s.executeUpdate(deleteUpdate);
        } catch (SQLException e) {
            System.out.println("Unable to delete the given section");
        }

    }

    /**
     *
     * @param sec is the section we are testing
     * @return num students in this section
     */
    private int numEnrolled(Section sec) {
        //adds where predicate to find the students enrolled in SECTION sec
        return getRowCount("ENROLLED_IN where subj_name = '" + sec.getSubj() + "' and course_num = " + sec.getCourseNum() + " and Day_of_Week = '" + sec.getDay() + "' and time_held = '" + sec.getTime() + "' and semester = '" + sec.getSemester() + "' and year = " + sec.getYear());
    }

}
