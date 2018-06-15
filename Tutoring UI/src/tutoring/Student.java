package tutoring;

import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;

/**
 * A class that represents a student being tutored at Lehigh
 *
 * @author Samuel Chebruch
 */
public class Student {

    private String first;
    private String last;
    private boolean isAthlete;
    private String email;
    private int LIN;
    private static Connection con = Tutor.getConnection();
    private static Statement s;

    /**
     * Constructs a student object. Initializes fields and asserts that they are valid. Exits if connection to DB fails
     * @param first
     * @param last
     * @param email
     * @param LIN
     * @param isAthlete 
     */
    public Student(String first, String last, String email, int LIN, boolean isAthlete) {
        assert(first.matches("[A-Z][a-z]+"));
        assert(last.matches("[A-Z][a-z]+"));
        assert(email.matches("[A-Za-z0-9_.-]+[a-zA-Z0-9][@][A-Za-z_0-9-]+[.][a-zA-Z.]+[a-zA-Z]+"));
        assert(getCharactersLIN(LIN) == 9);
        this.first = first;
        this.last = last;
        this.email = email;
        this.LIN = LIN;
        this.isAthlete = isAthlete;
        try {
           // Class.forName("org.sqlite.JDBC");
           // con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
           // con.createStatement().execute("PRAGMA foreign_keys = ON");
            s = con.createStatement();
        } catch (Exception e) {
            System.out.println("Could not connect");
            System.exit(0);
        }
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getEmail() {
        return email;
    }

    public int getLIN() {
        return LIN;
    }

    public boolean getAthleteStatus() {
        return isAthlete;
    }

    
    public String toString() {
        return "Name: " + first + " " + last + ".  Email: " + email + ".  LIN: " + LIN + ".  Athlete Status: " + isAthlete + ".";
    }

    /**
     * Allows the return of athlete status for given student
     * @param LIN
     * @return true if student is an athlete
     */
    public static boolean getAthleteStatus(int LIN)
    {
        String q = "select athlete_status from STUDENT where LIN = " + LIN;
        ResultSet r = null;
        try
        {
            r = s.executeQuery(q);
            r.next();
            if(r.getString("athlete_status").equalsIgnoreCase("no"))
            {
                return false;
            }
            return true;
        }catch(SQLException e)
        {}
        return false;
    }
    /**
     * Two students are equal if they have the same LIN
     * @param o represents the Student
     * @return true if 2 students are equal
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof Student)
        {
            Student s = (Student) o;
            return (s.getLIN() == this.getLIN());
        }
        return false;
    }
    
    /**
     * Returns the number of characters in a LIN number.
     * @return
     */
    private int getCharactersLIN(int LIN)
    {
        String LINString = Integer.toString(LIN);
        return LINString.length();
    }
}