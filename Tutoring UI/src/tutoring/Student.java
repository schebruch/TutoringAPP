package tutoring;

import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;

public class Student
{
  private String first;
  private String last;
  private boolean isAthlete;
  private String email;
  private int LIN;

  public Student(String first, String last, String email, int LIN, boolean isAthlete)
  {
    this.first = first;
    this.last = last;
    this.email = email;
    this.LIN = LIN;
    this.isAthlete = isAthlete;
  }

  public String getFirst()
  {
    return first;
  }

  public String getLast()
  {
    return last;
  }

  public String getEmail()
  {
    return email;
  }

  public int getLIN()
  {
    return LIN;
  }
  
  public int getSkipCount(Statement s)
  {
      String q = "select skip_count from student where LIN = " + this.getLIN();
      try
      {
          ResultSet r = s.executeQuery(q);
          if(!r.next())
          {
              System.out.println("Student: " + this.getFirst() + " " + this.getLast() + " does not exist");
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
  }

  public boolean getAthleteStatus()
  {
    return isAthlete;
  }
  public String toString()
  {
    return "Name: " + first + " " + last + ".  Email: " + email +".  LIN: " + LIN + ".  Athlete Status: " + isAthlete + ".";
  }
}
