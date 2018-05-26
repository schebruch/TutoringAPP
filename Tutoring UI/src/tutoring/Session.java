package tutoring;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.*;

/**
 * This class represents a particular session of a tutoring section, which is
 * just the date of the session.
 *
 * @author Samuel Chebruch
 */
public class Session extends Section {

    private Date dateOfSession;
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
    public Session(Section section, Date dateOfSession) {
        super(section.getSubj(), section.getCourseNum(), section.getDay(), section.getTime(), section.getSemester(), section.getYear());
        this.dateOfSession = dateOfSession;
        String tutorQuery = "select* from TUTOR";
        try {
            ResultSet r = s.executeQuery(tutorQuery);
            r.next();
            Tutor t = new Tutor(r.getString("tutor_name"), r.getInt("LIN"));
            loadInstructs(t);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Date getDateOfSession() {
        return dateOfSession;
    }

    public ArrayList<Student> getAttended() {
        return attended;
    }

    public ArrayList<Student> getMissing() {
        return missing;
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
            attended.add(student);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Allows the tutor to mark someon as absent
     * @param student refers to the student we are marking as absent
     * @param reason refers to the reason the student provides for being absent. By default, reason = "NA"
     */
    public void markSkipped(Student student, String reason) {
        try {
            String attendanceQuery = getAttendanceString("SKIPS", student, reason);
            s.executeUpdate(attendanceQuery);
            missing.add(student);
            reasons.add(reason);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDateOfSession(Date date) {
        this.dateOfSession = date;
    }

    /*
    public void takeAttendance() {
        boolean showedUp = false;
        for (int i = 0; i < students.size(); i++) {
            showedUp = Tutor.getBoolOption("Did student " + students.get(i).getFirst() + " " + students.get(i).getLast() + " attend this session?");
            if (showedUp) {
                attended.add(students.get(i));
            } else {
                missing.add(students.get(i));
                reasons.add(getReason(students.get(i).getFirst(), students.get(i).getLast()));
            }
        }
    }*/

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
     * @param attended refers to the attending student
     * @param isAthlete refers to the athlete status of the student
     * @return 
     */
    public String attendedToString(Student attended, String isAthlete) {
        return attended.getFirst() + "; " + attended.getLast() + "; " + attended.getLIN() + "; " + isAthlete;
    }

    /**
     * Prints students using toString()
     * @param students list of students to print
     * @param missing determines whether the student attended or not
     * If student didn't attend, print the reason as well
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
            return "insert into " + tableName + "values('" + getSubj() + "', " + getCourseNum() + ", '" + getDay() + "', '" + getTime() + "', '" + getSemester() + "', " + getYear() + ", " + tmp.getLIN() + ", '" + getDateOfSession() + "')";
        }
        return "insert into " + tableName + "values('" + getSubj() + "', " + getCourseNum() + ", '" + getDay() + "', '" + getTime() + "', '" + getSemester() + "', " + getYear() + ", " + tmp.getLIN() + ", '" + getDateOfSession() + "', '" + reason + "')";
    }
}
