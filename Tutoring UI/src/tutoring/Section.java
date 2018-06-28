//MUST LATER FIGURE OUT HOW TO ASSERT THAT NO 2 SECTIONS CAN BE CONCURRENT


package tutoring;

import java.util.ArrayList;
import java.sql.*;

/**
 * A class that represents a particular section that a tutor at Lehigh tutors
 *
 * @author Samuel Chebruch
 */
public class Section {

    protected ArrayList<Student> students = new ArrayList<>(); //Students currently in a section. Will be loaded at constructor (DB)
    protected String subj; //example: ECE
    protected int course_num;
    protected String day; //day of the week section is offered
    protected String time; //time that section is offered
    protected String semester; //fall or spring
    protected int year;
    protected static Connection con = Tutor.getConnection();
    protected static Statement s;

    /**
     *
     * @param subj represents the course subject. Example: MATH
     * @param course_num represents the course number of that subject
     * @param day represents the day of the week the class is tutored
     * @param time represents the time of day this class is tutored
     * @param semester represents the current semester. Example: 'Spring'
     * @param year respresents the current year. Example: 2018
     *
     * Initializes fields. Loads the list of students and the section into the
     * DB
     */
    public Section(String subj, int course_num, String day, String time, String semester, int year) {
        try {
            //  Class.forName("org.sqlite.JDBC");
         //   con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
           // con.createStatement().execute("PRAGMA foreign_keys = ON");
            s = con.createStatement();
        } catch (SQLException e) {
            System.out.println("Could not connect");
            System.exit(0);
        }
        assert (getSubjects().contains(subj)); //ensuring that a valid subject was passed in
        this.subj = subj;
        assert (getCourseNums().contains(course_num));
        this.course_num = course_num;
        this.day = day;
        this.time = time;
        this.semester = semester;
        this.year = year;
        loadSection(); //inserts the section into the DB if it's not already loaded
        loadStudents(); //loads the students already in this section into the students ArrayList
    }

    //may be useless
    public Section(String name, String day, String time, ArrayList<Student> students) {
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            s = con.createStatement();
            System.out.println("Connection successful");
        } catch (Exception e) {
            System.out.println("Could not connect");
            System.exit(0);
        }
        this.day = day;
        this.time = time;
        this.students = students;
        loadSection();
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    public String getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public String getSubj() {
        return subj;
    }

    public int getCourseNum() {
        return course_num;
    }

    public int getNumStudents() {
        return students.size();
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    /**
     * Removes all students from DB in this section and clears the ArrayList
     * students
     */
    public void clearStudents() {
        String deleteEnrolledIn = "delete from ENROLLED_IN where " + concatPKOfSection();
        String deleteStudents = null;
        try {
            s.executeUpdate(deleteEnrolledIn);
            students.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * gets the skip count of
     *
     * @param student
     * @return the skip count of student
     */
    public int getSkipCount(Student student) {
        String q = "select skip_count from ENROLLED_IN where LIN = " + student.getLIN() + " and " + concatPKOfSection();
        try {
            ResultSet r = s.executeQuery(q);
            if (!r.next()) {
                // System.out.println("Student: " + student.getFirst() + " " + student.getLast() + " is not a student in your session");
                return 0;
            } else {
                return r.getInt("skip_count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Allows the tutor to set the skip count for a student
     * @param student
     * @param skipCount 
     */
    public void setSkipCount(Student student, int skipCount)
    {
        assert(skipCount >= 0);
        String updateSkipCount = "update ENROLLED_IN set skip_count = " + skipCount + " where LIN = " + student.getLIN() + " and " + concatPKOfSection();
        try
        {
            s.executeUpdate(updateSkipCount);
        }catch(SQLException e)
        {
            
        }
        
    }

    /**
     * @param toRemove is the student we are removing Removes a student from the
     * section (and the DB)
     */
    public void removeStudent(Student toRemove) {
        String removeFromEnrolled = "delete from ENROLLED_IN where LIN = " + toRemove.getLIN() + " and " + concatPKOfSection();
       // String removeFromStudent = "delete from STUDENT where LIN = " + toRemove.getLIN();
        try {
            s.executeUpdate(removeFromEnrolled);
           // s.executeUpdate(removeFromStudent);
            students.remove(toRemove);
            System.out.println("Remove successful");
        } catch (SQLException e) {
              e.printStackTrace();
        }
    }

    /**
     * Returns a String representation of a section. Example: 'Class: MATH 205.
     * Day: Tues. Time: 2:00. Num Students: 9.
     *
     * @return String created
     */
    @Override
    public String toString() {
        return "Class: " + subj + " " + course_num + ".  Day: " + day + ".  Time: " + time + ".  Num Students: " + students.size() + ".";
    }

    /**
     *
     * @param tmp is a Student we are inserting to the DB Inserts into both
     * STUDENT and ENROLLED_IN
     */
    public void insertNewStudent(Student tmp) {
        //Update STUDENT
        String insertStudent = "insert into STUDENT values(" + tmp.getLIN() + ", '" + tmp.getFirst() + "', '" + tmp.getLast() + "', '" + tmp.getEmail() + "', '" + tmp.getAthleteStatus() + "')";
        try {
            s.executeUpdate(insertStudent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //Update ENROLLED_IN
        String insertEnrolledIn = "insert into ENROLLED_IN values('" + subj + "', " + course_num + ", '" + day + "', '" + time + "', '" + semester + "', " + year + ", " + tmp.getLIN() + ", 0)";
        try {
            s.executeUpdate(insertEnrolledIn);
            students.add(tmp);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Tests if 2 sections are concurrent, meaning they have the same
     * year, semester, day, and time
     * @param section
     * @return true if 2 sections are concurrent
     */
    
    //will put this in Tutor later for a list of sections. This doesn't belong here
    private boolean concurrent(Section section) {
        
        return section.getYear() == this.getYear() && section.getSemester().equals(this.getSemester()) && section.getDay().equals(this.getDay()) && section.getTime().equals(this.getTime());  
    }

    /**
     * Prints all students in a given section using toString()
     */
    public void printStudents() {
        for (int i = 0; i < students.size(); i++) {
            System.out.println(students.get(i).toString());
        }
    }

    /**
     * Allows for easier querying
     *
     * @return the String of the query that identifies this section. Should be
     * called after the "where" clause in the original query
     */
    protected String concatPKOfSection() {
        return "subj_name = '" + subj + "' and course_num = " + course_num + " and Day_of_Week = '" + day + "' and time_held = '" + time + "' and semester = '" + semester + "' and year = " + year;
    }

    /**
     * Evoked from constructor, populated the list of students in this section
     */
    private void loadStudents() {
        //gets LINs of students in this section
        String LINsQuery = "select * from ENROLLED_IN where " + concatPKOfSection();
        // String LINsQuery = "select LIN from ENROLLED_IN where subj_name = '" + subj + "' and course_num = " + course_num + " and Day_of_Week = '" + day + "' and time_held = '" + time + "' and semester = '" + semester + "' and year = " + year;
        ResultSet LINResult = null;
        ResultSet studentResult = null;
        try {
            LINResult = s.executeQuery(LINsQuery);
            Statement s2 = con.createStatement();
            while (LINResult.next()) {
                int LIN = LINResult.getInt("LIN");
                //gets the student with this LIN from DB
                String studentQuery = "select* from STUDENT where LIN = " + LIN;
                studentResult = s2.executeQuery(studentQuery);
                //add a student object for each student in the ResultSet
                while (studentResult.next()) {
                    String first = studentResult.getString("first_name");
                    String last = studentResult.getString("last_name");
                    String email = studentResult.getString("email");
                    String athleteStatus = studentResult.getString("athlete_status");
                    boolean isAthlete = false;
                    //athlete_status is a 'yes' or 'no' String in DB. Convert to boolean here
                    if (athleteStatus.equalsIgnoreCase("yes")) {
                        isAthlete = true;
                    }
                    students.add(new Student(first, last, email, LIN, isAthlete));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a section into the DB. Duplicate sections are thrown into the catch
     * block due to primary key constraint. Evoked from constructor. SQLite
     * error code for a primary key Exception is 19
     */
    private void loadSection() {
        String q = "insert into SECTION values('" + subj + "', " + course_num + ", '" + day + "', '" + time + "', '" + semester + "', " + year + ")";
        try {
            s.executeUpdate(q);
        } catch (SQLException e) {
            // assert (e instanceof SQLIntegrityConstraintViolationException);
            //this is ok if the section is already in the DB                
            //e.printStackTrace();
        }
    }

    /**
     * Helps test for foreign key violations
     *
     * @return a list of subjects
     */
    private ArrayList<String> getSubjects() {
        ArrayList<String> subjects = new ArrayList<>();
        String subjectQuery = "select subj_name from SUBJECT";
        ResultSet r = null;
        try {
            r = s.executeQuery(subjectQuery);
            while (r.next()) {
                subjects.add(r.getString("subj_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subjects;
    }

    /**
     * 
     * @return course numbers associated with this.subj
     */
    private ArrayList<Integer> getCourseNums() {
        ArrayList<Integer> course_nums = new ArrayList<>();
        String subjectQuery = "select course_num from CLASS where subj_name = '" + subj + "'";
        ResultSet r = null;
        try {
            r = s.executeQuery(subjectQuery);
            while (r.next()) {
                course_nums.add(r.getInt("course_num"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course_nums;
    }
}
