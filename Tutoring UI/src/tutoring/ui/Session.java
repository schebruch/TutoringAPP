import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Session extends Section
{

  private Date dateOfSession;
  private ArrayList<Student> attended = new ArrayList<>();
  private ArrayList<Student> missing = new ArrayList<>();
  private ArrayList<String> reasons = new ArrayList<>();

  public Session(String name, ArrayList<Student> students, String day, String time)
  {
    super(name, day, time, students);
    setDateOfSession();
  }

  private void setDateOfSession()
  {
    Scanner in = new Scanner(System.in);
    System.out.println("Please enter the date of this tutoring session (MM/DD/YYYY)");
    while(true)
    {
      try
      {
        dateOfSession = (new SimpleDateFormat("MM/DD/YYYY").parse(in.nextLine()));
        break;
      }
      catch(java.text.ParseException e)
      {
        System.out.println("Please enter the date in the proper format");
        continue;
      }
    }
  }
  //sets the students in the attended ArrayList and those that didn't attend in the missing ArrayList
  public void takeAttendance()
  {
    boolean showedUp = false;
    for(int i = 0; i < students.size(); i++)
    {
      showedUp = Tutor.getBoolOption("Did student " + students.get(i).getFirst() + " " + students.get(i).getLast() + " attend this session?");
      if(showedUp)
      {
        attended.add(students.get(i));
      }
      else
      {
        missing.add(students.get(i));
        reasons.add(getReason(students.get(i).getFirst(), students.get(i).getLast()));
      }
    }
  }

  public ArrayList<Student> getAttended()
  {
    return attended;
  }

  public ArrayList<Student> getMissing()
  {
    return missing;
  }

  public void printAttended()
  {
    printStudents(attended, false);
  }

  public void printMissing()
  {
    printStudents(missing, true);
  }

  private String getReason(String first, String last)
  {
    Scanner in = new Scanner(System.in);
    System.out.print("Reason for: " + first + " " + last + " missing: ");
    return in.nextLine();

  }

  public String missingToString(Student missing, String isAthlete, int idx)
  {
    return missing.getFirst() + "; " + missing.getLast() + "; " + missing.getLIN() + "; " + isAthlete + "; " + reasons.get(idx);
  }

  public String attendedToString(Student attended, String isAthlete)
  {
    return attended.getFirst() + "; " + attended.getLast() + "; " + attended.getLIN() + "; " + isAthlete;
  }

  public void printStudents(ArrayList<Student> students, boolean missing)
  {
    String isAthlete = null;
    for(int i = 0; i < students.size(); i++)
    {
      if(students.get(i).getAthleteStatus())
      {
        isAthlete = "yes";
      }
      else
      {
        isAthlete = "no";
      }
      if(!missing)
      {
        System.out.println(attendedToString(students.get(i), isAthlete));
      }
      else
      {
        System.out.println(missingToString(students.get(i), isAthlete, i));
      }
    }
  }
}
