import java.util.Scanner;
import java.util.ArrayList;
import java.sql.*;
import java.util.Date;
import java.util.Calendar;

public class Tutor
{
  private String name;
  private ArrayList<Section> sections = new ArrayList<>();
  private int LIN;

  public Tutor(String name, int LIN, Statement s, boolean existingTutor)
  {
    this.LIN = LIN;
    this.name = name;
    if(!existingTutor)
    {
        String q = "insert into TUTOR values(" + LIN + ", '" + name + "')";
        try{
           s.executeUpdate(q);
       }catch(Exception e)
        {
           e.printStackTrace();
        }
    }
   } 

  //returns num rows in CLASS relation
  public int getNumSections(Statement s)
  {
      ResultSet r = null;
      try
      {
          String q = "select count(*) as tot from SECTION";
          r = s.executeQuery(q);
          return r.getInt("tot");
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return -1;
  }
/*
  public ArrayList<Section> getSections()
  {
    return sections;
  }*/
  
  public static boolean isTutor(Statement s)
  {
      String q = "select* from tutor";
      try
      {
          ResultSet r = s.executeQuery(q);
          if(!r.next())
          {
              return false;
          }
          return true;
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return false;
  }
 //displays all days of the week
  private String getDoW()
  {
    Scanner in = new Scanner(System.in);
    String[] days = {"Mon", "Tues", "Wed", "Thurs", "Fri", "Sat", "Sun"};
    for(int i =0; i < days.length; i++)
    {
      System.out.println((i+1) + ".\t"+ days[i]);
    }
    int option = Tutor.getOption(1,7);
    return days[option-1];
  }
  
  private int getYear()
  {
      return Calendar.getInstance().get(Calendar.YEAR);
  }
  
  private String getSemester()
  {
    Scanner in = new Scanner(System.in);
    String[] semesters = {"Fall", "Spring"};
    for(int i =0; i < semesters.length; i++)
    {
      System.out.println((i+1) + ".\t"+ semesters[i]);
    }
    int option = Tutor.getOption(1,2);
    return semesters[option-1];
  }

  private String getTime()
  {
    Scanner in = new Scanner(System.in);
    String[] times ={"12:00", "2:00", "4:00", "6:00", "8:00"};
    for(int i =0; i < times.length; i++)
    {
      System.out.println((i+1) + ".\t"+ times[i]);
    }
    int option = Tutor.getOption(1,5);
    return times[option-1];
  }

  public static int getOption(int lower, int upper)
  {
    Scanner in = new Scanner(System.in);
    int option = 0;
    while(true)
    {
      try
      {
        option = Integer.parseInt(in.nextLine());
        if(option < lower || option > upper)
        {
          System.out.println("Please select an option from " + lower + " to " + upper + ".");
          continue;
        }break;
      }catch(NumberFormatException e)
      {
        System.out.println("Please enter a valid integer.");
      }
    }
    return option;
  }

  public static int getOption(int length)
  {
    Scanner in = new Scanner(System.in);
    int option = 0;
    while(true)
    {
      try
      {
        option = Integer.parseInt(in.next());
        if(Integer.toString(option).length() != length)
        {
          System.out.println("Please enter an integer of exactly " + length + " characters.");
          continue;
        }break;
      }catch(Exception e)
      {
        System.out.println("Please enter a valid integer.");
      }
    }
    return option;
  }

  public static String getStringOption(String regex)
  {
    Scanner in = new Scanner(System.in);
    String retVal = null;
    while(true)
    {
      retVal = in.nextLine();
      if(!retVal.matches(regex))
      {
        System.out.println("Please enter a valid input");
        continue;
      }break;
    }
    return retVal;
  }

  public static boolean getBoolOption(String question)
  {
    Scanner in = new Scanner(System.in);
    System.out.println(question);
    String resp = null;
    while(true)
    {
      resp = in.next();
      if(!resp.equalsIgnoreCase("yes") && !resp.equalsIgnoreCase("no"))
      {
        System.out.println("Please enter 'yes' or 'no' to the given question.");
        continue;
      }break;
    }
    if(resp.equalsIgnoreCase("yes"))
    {
      return true;
    }
    return false;
  }
  
  public static String getClassName(int idx, Statement s)
  {
      int count = 0;
      try
      {
          ResultSet r = s.executeQuery("select* from Class");
          do
          {
              r.next();
              count++;
          }while(count < idx);
          return r.getString("class_name");
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return null;
  }
  
  public static String getSubjName(int idx, Statement s)
  {
      int count = 0;
      try
      {
          ResultSet r = s.executeQuery("select* from Class");
          do
          {
              r.next();
              count++;
          }while(count < idx);
          return r.getString("subj_name");
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return null;
  }
  
  public static int getCourseNum(int idx, Statement s)
  {
      int count = 0;
      try
      {
          ResultSet r = s.executeQuery("select* from Class");
          do
          {
              r.next();
              count++;
          }while(count < idx);
          return r.getInt("course_num");
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return 0;
  }
  
  public Section getSection(int idx, Statement s)
  {
      int count = 0;
      try
      {
          ResultSet r = s.executeQuery("select* from Section natural join class");
          do
          {
              r.next();
              count++;
          }while(count < idx);
          return new Section(r.getString("class_name"), r.getString("subj_name"), r.getInt("course_num"), r.getString("Day_of_Week"), r.getString("time_held"), r.getString("semester"), r.getInt("year"));
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return null;
  }
  
  
  public static void displayOptions()
  {
    System.out.println("Would you like to:\n1. Register as a tutor\n2. Add classes\n3. Add students to a class\n4. Take attendance\n5. Remove a student\n6. Quit");
  }

  public boolean displayCurrentSections(Statement s)
  {
    ResultSet r = null;
    try
    {
        String q = "select* from SECTION where year = " + this.getYear() + " and semester = '" + this.getSemester() + "'";
        r = s.executeQuery(q);
        if(!r.next())
        {
            System.out.println("You have no classes currently. Consider creating some");
            return false;
        }
        else
        {
            do
            {
                System.out.println(r.getString("subj_name") + " " + r.getInt("course_num") + ", " + r.getString("Day_of_Week") + ", " + r.getString("time_held") + ", " + r.getInt("year"));
            }while(r.next());
            return true;
        }
    }catch(Exception e)
    {
        e.printStackTrace();
    }
    /*
    for(int i = 0; i < sections.size(); i++)
    {
      System.out.println((i+1) + ". " + sections.get(i).toString());
    }*/
    return false;
  }
  
  //returns num rows
  public int displayOfferedClasses(Statement s)
  {
      System.out.printf("%-10s\t%-10s\t%-10s\n\n", "COURSE_NUM", "SUBJ_NAME", "CLASS_NAME");
      String q = "select* from Class";
      try
      {
          ResultSet r = s.executeQuery(q);
          int idx = 1;
          while(r.next())
          {
              System.out.printf("%d. ", idx);
              idx++;
              System.out.printf("%-10s\t%-10s\t%-10s\n", r.getInt("COURSE_NUM"), r.getString("SUBJ_NAME"), r.getString("CLASS_NAME"));
          }
          return idx-1;
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return 0;
  }
  
  private boolean canTeach(Section tmp, Statement s)
  {
      String q = "select* from SECTION where semester = '" + tmp.getSemester() + "' and year = " + tmp.getYear() + " and Day_of_Week = '" + tmp.getDay() + "' and time_held = '" + tmp.getTime() + "'";
      try
      {
          ResultSet r = s.executeQuery(q);
          if(r.next())
          {
              return false;
          }
      }catch(Exception e)
      {
          e.printStackTrace();
      }
      return true;
  }
//for class object, need class name, date/time
  private void addSections(Statement s)
  {
    int numClasses = 0;
    Scanner in = new Scanner(System.in);
    System.out.println("Please enter the number of classes you are tutoring. Note that tutoring the same course on Monday and Tuesday is considered 2 courses");
    numClasses = getOption(1, 6);
    String className = null;  
    for(int i = 0; i < numClasses; i++)
    {
      int maxCourses = displayOfferedClasses(s);
      System.out.println("Please enter the corresponding number given for this class");
      int classIdx = getOption(1, maxCourses);
      className = getClassName(classIdx, s);
      System.out.println("What day of the week is this class? Please select an option");
      String day = getDoW();
      System.out.println("What time is this class being tutored? Please select an option");
      String time = getTime();
      System.out.println("What semester is this being tutored? Please select an option");
      String semester = getSemester();
      int year = getYear();
      String subj = getSubjName(classIdx, s);
      int course_num = getCourseNum(classIdx, s);
      Section tmp = new Section(className, subj, course_num,  day, time, semester, year);
      if(!updateSection(s, tmp))
      {
          i--;
          continue;
      }
   //   sections.add(tmp);
    }
  }

  private boolean updateSection(Statement s, Section tmp)
  {
      try
      {
        if(!canTeach(tmp,s))
        {
            System.out.println("You are already tutoring a course that conflicts with this time. Please select another option.");
            return false;
        }
        String update = "insert into SECTION values('" + tmp.getSubj() + "', + " + tmp.getCourseNum() + ", '" + tmp.getDay() + "', '" + tmp.getTime() + "', '" + tmp.getSemester() + "', " + tmp.getYear() +")";
        s.executeUpdate(update);    
        return true;
      }catch(Exception e)
      {
          System.out.println("You're already tutoring this course at this time for this semester. Please re-enter");
          return false;
      }
  }
 
  public void addStudents(Statement s)
  {
    if(this.displayCurrentSections(s))
    {
      int classNum = getOption(1, this.getNumSections(s));
      this.getSection(classNum,s).addStudents(s);
    }
    else
    {
        System.out.println("You have no classes yet. Create one by pressing '2'!");
    }
  }

  public void takeAttendance(Statement s)
  {
    if(this.getNumSections(s) > 0)
    {
      System.out.println("Which class are you taking attendance for? Please select a number.");
      int classNum = getOption(1, this.getNumSections(s));
      Section current = getSection(classNum, s);
      current.addStudentsToList(s);
      Session attendance = new Session(current.getName(), current.getStudents(), current.getDay(), current.getTime());
      attendance.takeAttendance();
      System.out.println("Attending Students:\n");
      attendance.printAttended();
      System.out.println("Missing Students:\n");
      attendance.printMissing();
    }
    else
    {
        System.out.println("You currently don't have any classes. Create one!");
    }
  }

  public void removeStudent(Statement s)
  {
    if(this.getNumSections(s) > 0)
    {
      int classNum = getOption(1, getNumSections(s));
      Section current = sections.get(classNum - 1);
      current.removeStudent();
    }
  }

  public static void main(String[] args){
    Connection con = null;
    Statement s = null;
    try
    {
      con = DriverManager.getConnection("jdbc:sqlite:Tutoring.db");
      s = con.createStatement();
      System.out.println("Connection successful");
    }catch(Exception e)
    {
      e.printStackTrace();
      System.exit(0);
    }
    Tutor tutor = null;
    boolean isTutor = Tutor.isTutor(s);
    if(isTutor)
    {
        try
        {
            String q = "select* from tutor";
            ResultSet r = s.executeQuery(q);
            r.next();
            int LIN = r.getInt("LIN");
            String name = r.getString("tutor_name");
            tutor = new Tutor(name, LIN, s, true);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    while(true)
    {
      displayOptions();
      int option = getOption(1,6);
      if(option == 1 && !isTutor)
      {
        System.out.println("Please enter your full name in format: 'First Last', that is, space between first and last name and capital first letters");
        String name = Tutor.getStringOption("[A-Z][a-z]+[ ][A-Z][a-z]+");
        System.out.println("Please enter your LIN: ");
        int LIN = Tutor.getOption(9);
        tutor = new Tutor(name, LIN, s, false);
        isTutor = true;
        System.out.println("Congratulations! " + name  + " is now registered as a tutor! Now type '2' in to add classes!");
        continue;
      }
      if(option == 1 && isTutor)
      {
          System.out.println("You are already a tutor!");
          continue;
      }
      if(option > 1 &&  option < 6 && !isTutor)
      {
        System.out.println("Please become a tutor before doing anything else");
        continue;
      }
      if(option == 2)
      {
        tutor.addSections(s);
        System.out.println("All of your sections are as follows:\n");
        tutor.displayCurrentSections(s);
        continue;
      }
      if(option == 3)
      {
        tutor.addStudents(s);
        continue;
      }
      if(option == 4)
      {
        if(!tutor.displayCurrentSections(s))
        {
            System.out.println("You don't have any classes currently created. Please create some by pressing '2'");
            continue;
        }
        tutor.takeAttendance(s);
        continue;
      }
      if(option == 5)
      {
        if(!tutor.displayCurrentSections(s))
        {
            System.out.println("You don't have any classes currently created. Please create some by pressing '2'");
            continue;
        }
        tutor.removeStudent(s);
        continue;
      }
      if(option == 6)
      {
        System.out.println("Have a great day!");
        try
        {
            s.close();
            con.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        break;
      }
    }
  }
}
