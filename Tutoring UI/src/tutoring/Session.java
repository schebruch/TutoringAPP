package tutoring;

import java.util.ArrayList;
import java.util.Date;
import java.sql.*;

/**
 * This class represents a particular session of a tutoring section, which is
 * just the date of the session.
 *
 * @author Samuel Chebruch
 */
public class Session extends Section {

    private String dateOfSession;
    private ArrayList<Student> attended = new ArrayList<>();
    private ArrayList<Student> missing = new ArrayList<>();
    private ArrayList<String> reasons = new ArrayList<>();

    /*
  public Session(String name, ArrayList<Student> students, String day, String time)
  {
    super(name, day, time, students);
    setDateOfSession();
  }*/
    /**
     * Initializes fields
     *
     * @param section references a section that this is a session of
     * @param dateOfSession references the date of the session
     */
    public Session(Section section, String dateOfSession) {
        super(section.getSubj(), section.getCourseNum(), section.getDay(), section.getTime(), section.getSemester(), section.getYear());
        this.dateOfSession = dateOfSession;
        String tutorQuery = "select* from TUTOR";
        try {
            ResultSet r = s.executeQuery(tutorQuery);
            r.next();
            Tutor t = new Tutor(r.getString("tutor_name"), r.getInt("LIN"));
            loadInstructs(t);
            loadAttends();
            loadMissing();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getDateOfSession() {
        return dateOfSession;
    }

    public ArrayList<Student> getAttended() {
        return attended;
    }

    public ArrayList<Student> getMissing() {
        return missing;
    }

    public void setDateOfSession(String date) {
        this.dateOfSession = date;
    }

    /**
     * Updates the data base for attending students and adds the student to the
     * array list of attending students.
     *
     * @param student refers to the student to be marked as attended
     */
    public void markAttended(Student student) {
        try {
            String attendanceQuery = getAttendanceString("ATTENDS", student, null);
            s.executeUpdate(attendanceQuery);

        } catch (SQLException e) {
            //ok if attendance was already taken. May be if the user edits an initial erroneuous entry
        } finally {
            if (!attended.contains(student)) {

                attended.add(student);
            }
        }
    }

    /**
     * Allows the tutor to mark someone as absent Updates the skip count for
     * that student if the reason is NA
     *
     * @param student refers to the student we are marking as absent
     * @param reason refers to the reason the student provides for being absent.
     * By default, reason = "NA"
     */
    public void markSkipped(Student student, String reason) {
        try {
            String attendanceQuery = getAttendanceString("SKIPS", student, reason);
            s.executeUpdate(attendanceQuery);
            if (reason.equalsIgnoreCase("NA")) {

                int skipCountUpdate = 1 + getSkipCount(student);
                String skipUpdate = "update ENROLLED_IN set skip_count = " + skipCountUpdate + " where LIN = " + student.getLIN();
                s.executeUpdate(skipUpdate);
            }
        } catch (SQLException e) {
            //  e.printStackTrace();
        } finally {
            if (!missing.contains(student)) {

                missing.add(student);
                reasons.add(reason);
            }

        }
    }

    /**
     * Allows tutor to remove
     *
     * @param student from list of attending students
     */
    public void removeAttended(Student student) {
        String update = "delete from ATTENDS where LIN = " + student.getLIN();
        try {
            s.executeUpdate(update);
            attended.remove(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows tutor to remove
     *
     * @param student from the list of missing students and also reduces the
     * skip count if the reason was NA
     */
    public void removeSkipped(Student student) {
        String skippedQuery = "select* from SKIPS where LIN = " + student.getLIN();
        boolean lowerSkipCount = false;
        try {
            ResultSet r = s.executeQuery(skippedQuery);
            r.next();
            String reason = r.getString("Reason");
            if (reason.equalsIgnoreCase("NA")) {
                lowerSkipCount = true;
            }
            Statement s2 = con.createStatement();
            Statement s3 = con.createStatement();
            String deleteSkipped = "delete from SKIPS where LIN = " + student.getLIN();
            s2.executeUpdate(deleteSkipped);
            String skipDecrement = null;
            if (lowerSkipCount) {
                skipDecrement = "update ENROLLED_IN set skip_count = " + (getSkipCount(student) - 1) + " where LIN = " + student.getLIN();
                s3.executeUpdate(skipDecrement);
            }
            s2.close();
            s3.close();
            missing.remove(student);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the list of attending students in the required format
     */
    public void printAttended() {
        printStudents(attended, false);
    }

    /**
     * Prints the list of missing students in the required format.
     */
    public void printMissing() {
        printStudents(missing, true);
    }

    /**
     * Returns attendance String in desired format
     *
     * @param missing refers to the missing student
     * @param isAthlete refers to whether the student is an athlete or not
     * @param reason refers to the reason the student gave for missing
     * @return
     */
    public String missingToString(Student missing, String isAthlete, String reason) {
        return missing.getFirst() + "; " + missing.getLast() + "; " + missing.getLIN() + "; " + isAthlete + "; " + reason;
    }

    /**
     * Returns the String of attending students in the desired format
     *
     * @param attended refers to the attending student
     * @param isAthlete refers to the athlete status of the student
     * @return
     */
    public String attendedToString(Student attended, String isAthlete) {
        return attended.getFirst() + "; " + attended.getLast() + "; " + attended.getLIN() + "; " + isAthlete;
    }

    /**
     * Prints students using toString()
     *
     * @param students list of students to print
     * @param missing determines whether the student attended or not If student
     * didn't attend, print the reason as well
     */
    private void printStudents(ArrayList<Student> students, boolean missing) {
        String isAthlete = null;
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getAthleteStatus()) {
                isAthlete = "yes";
            } else {
                isAthlete = "no";
            }
            if (!missing) {
                System.out.println(attendedToString(students.get(i), isAthlete));
            } else {
                System.out.println(missingToString(students.get(i), isAthlete, reasons.get(i)));
            }
        }
    }

    /**
     * Invoked at construction. Loads the instructs relation
     */
    private void loadInstructs(Tutor t) {
        ResultSet r = null;
        String instructsUpdate = "insert into INSTRUCTS values('" + getSubj() + "', " + getCourseNum() + ", '" + getDay() + "', '" + getTime() + "', '" + getSemester() + "', " + getYear() + ", " + t.getLIN() + ", '" + getDateOfSession() + "')";
        try {
            s.executeUpdate(instructsUpdate);
        } catch (SQLException e) {
            //  e.printStackTrace();
        }
    }

    /**
     * Loads the list of attending students, done at construction
     */
    private void loadAttends() {
        ResultSet r = null;
        String attendsQuery = "select* from ATTENDS natural join STUDENT where time_held = '" + getTime() + "' and DATE_ATTENDED = '" + getDateOfSession() + "' and year = " + getYear() + " and semester = '" + getSemester() +"'";
        try {
            r = s.executeQuery(attendsQuery);
            while (r.next()) {
                if (!Student.getAthleteStatus(r.getInt("LIN"))) {
                    Student tmp = new Student(r.getString("first_name"), r.getString("last_name"), r.getString("email"), r.getInt("LIN"), false);
                    if (!attended.contains(tmp)) {
                        attended.add(tmp);
                    }
                } else {
                    Student tmp = new Student(r.getString("first_name"), r.getString("last_name"), r.getString("email"), r.getInt("LIN"), true);
                    if (!attended.contains(tmp)) {
                        attended.add(tmp);
                    }
                }
            }
        } catch (SQLException e) {

        }
    }

    /**
     * Loads the list of missing students and reasons, done at construction Does
     * not update skip count, this is done when the tutor takes attendance
     */
    private void loadMissing() {
        ResultSet r = null;
        String missingQuery = "select* from SKIPS natural join STUDENT where time_held = '" + getTime() + "' and DATE_SKIPPED = '" + getDateOfSession() + "' and year = " + getYear() + " and semester = '" + getSemester() + "'";
        try {
            r = s.executeQuery(missingQuery);
            while (r.next()) {
                String reason = r.getString("Reason");
                if (!Student.getAthleteStatus(r.getInt("LIN"))) {
                    Student tmp = new Student(r.getString("first_name"), r.getString("last_name"), r.getString("email"), r.getInt("LIN"), false);
                    if (!missing.contains(tmp)) {
                        
                        missing.add(tmp);
                        reasons.add(reason);
                    }

                } else {
                    Student tmp = new Student(r.getString("first_name"), r.getString("last_name"), r.getString("email"), r.getInt("LIN"), true);
                    if (!missing.contains(tmp)) {
                        missing.add(tmp);
                        reasons.add(reason);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a query for taking attendance
     *
     * @param tableName refers to either ATTENDS or SKIPS
     * @param tmp refers to the student we are taking attendance for
     * @return references the query we are returning
     */
    private String getAttendanceString(String tableName, Student tmp, String reason) {
        if (reason == null) {
            return "insert into " + tableName + " values('" + getSubj() + "', " + getCourseNum() + ", '" + getDay() + "', '" + getTime() + "', '" + getSemester() + "', " + getYear() + ", " + tmp.getLIN() + ", '" + getDateOfSession() + "')";
        }
        return "insert into " + tableName + " values('" + getSubj() + "', " + getCourseNum() + ", '" + getDay() + "', '" + getTime() + "', '" + getSemester() + "', " + getYear() + ", " + tmp.getLIN() + ", '" + getDateOfSession() + "', '" + reason + "')";
    }
}
