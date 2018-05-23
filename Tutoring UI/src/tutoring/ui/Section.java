import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;

public class Section{

  protected String courseName;
  protected ArrayList<Student> students = new ArrayList<>();
  protected String subj;
  protected int course_num;
  protected String day;
  protected String time;
  protected String semester;
  protected int year;

  public Section(String name, String subj, int course_num, String day, String time, String semester, int year)
  {
    this.courseName = name;
    this.subj = subj;
    this.course_num = course_num;
    this.day = day;
    this.time = time;
    this.semester = semester;
    this.year = year;
  }

  public Section(String name, String day, String time, ArrayList<Student> students)
  {
    this.courseName = name;
    this.day = day;
    this.time = time;
    this.students = students;
  }

  public String getName()
  {
    return courseName;
  }

  public String getDay()
  {
    return day;
  }

  public String getTime()
  {
    return time;
  }
  
  public String getSemester()
  {
    return semester;
  }
    
  public int getYear()
  {
    return year;
  }
  
  public String getSubj()
  {
    return subj;
  }
  public int getCourseNum()
  {
    return course_num;
  }

  public int getNumStudents()
  {
    return students.size();
  }

  public ArrayList<Student> getStudents()
  {
    return students;
  }
  
  public void addStudentsToList(Statement s)
  {
      String q = "select* from STUDENT";
      ResultSet r = null;
      int count = 0;
      try
      {
          r = s.executeQuery(q);
          if(!r.next())
          {
              System.out.println("You currently have no students for this section");
          }
          else
          {
              boolean isAthlete = false;
              do
              {
                  if(r.getString("athlete_status").equalsIgnoreCase("yes"))
                  {
                      isAthlete = true;
                  }
                  students.add(new Student(r.getString("first_name"), r.getString("last_name"), r.getString("email"), r.getInt("LIN"), isAthlete));
              }while(r.next());
          }  
      }catch(Exception e)
      {
          e.printStackTrace();
      }
  }
  
  private void printStudents()
  {
      for(int i = 0; i < students.size(); i++)
      {
        System.out.println(students.get(i).toString());
      }
  }

  public void addStudents(Statement s)
  {
    Scanner in = new Scanner(System.in);
    System.out.println("How many students would you like to add?");
    int numStudents = Tutor.getOption(0, 12);
    for(int i = 0; i < numStudents; i++)
    {
      System.out.println("Please enter the student's first name for student: " + (i+1));
      String first = Tutor.getStringOption("[A-Z][a-z]+");
      System.out.println("Please enter " + first + "'s last name.");
      String last = Tutor.getStringOption("[A-Z][a-z]+");
      System.out.println("Please enter " + first + "'s' email address.");
      String email = Tutor.getStringOption("[A-Za-z0-9_.-]+[a-zA-Z0-9][@][A-Za-z_0-9-]+[.][a-zA-Z.]+[a-zA-Z]+");
      boolean isAthlete = Tutor.getBoolOption("Is " + first + " " + last + " an athlete? Answer 'yes' or 'no'.");
      System.out.println("Please enter " + first + "'s LIN");
      int LIN = Tutor.getOption(9);
      Student tmp = new Student(first, last, email, LIN, isAthlete);
      if(!insertNewStudent(tmp, s))
      {
          System.out.println("You have already used this LIN for another student. Please re-enter");
          i--;
          continue;
      }
      students.add(tmp);
    }
  }

  public void removeStudent()
  {
    System.out.println("Please enter the student you would like to remove");
    for(int i = 0; i < students.size(); i++)
    {
      System.out.println((i+1) + ". " + students.get(i).toString());
    }
    int removeIdx = Tutor.getOption(1, students.size());
    Student remove = students.remove(removeIdx-1);
    System.out.println(remove.getFirst() + " " + remove.getLast() + " successfully removed.");
  }

  public String toString()
  {
    return "Class: " + courseName + ".  Day: " + day + ".  Time: " + time + ".  Num Students: " + students.size() + ".";
  }
  
  private boolean insertNewStudent(Student tmp, Statement s)
  {
      String q = "insert into STUDENT values(" + tmp.getLIN() + ", '" + tmp.getFirst() + "', '" + tmp.getLast() + "', '" + tmp.getEmail() + "', '" + tmp.getAthleteStatus() + "', 0)";
      try
      {
          s.executeUpdate(q);
          return true;
      }catch(Exception e)
      {
          e.printStackTrace();
          return false;
      }
  }
}
