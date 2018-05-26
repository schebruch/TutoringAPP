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
    private Connection con;
    private Statement s;

    public Student(String first, String last, String email, int LIN, boolean isAthlete) {
        this.first = first;
        this.last = last;
        this.email = email;
        this.LIN = LIN;
        this.isAthlete = isAthlete;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
            con.createStatement().execute("PRAGMA foreign_keys = ON");
            s = con.createStatement();
            System.out.println("Connection successful");
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

    /*
  public int getSkipCount()
  {
      String q = "select skip_count from student where LIN = " + this.getLIN();
      System.out.println(q);
      try
      {
          ResultSet r = s.executeQuery(q);
          if(!r.next())
          {
              System.out.println("Student: " + this.getFirst() + " " + this.getLast() + " is not a student in your session");
              return 0;
          }
          else
          {
              return r.getInt("skip_count");
          }
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return -1;
  }*/
    public String toString() {
        return "Name: " + first + " " + last + ".  Email: " + email + ".  LIN: " + LIN + ".  Athlete Status: " + isAthlete + ".";
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
}