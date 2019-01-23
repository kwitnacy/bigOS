
package bigOS;


import Interpreter.Interpreter;
import Processor.Scheduler;
import Procesy.Process_container;
import RAM.Memory;
import FileModule.FileManagement;

import java.util.ArrayList;
import java.util.Scanner;
/**
 *
 * @author Konrad
 */
public class Shell {
    Boolean system;
    Boolean session;
    String command;
    ArrayList<String> parts;
    String user;
    Shell()
    {
        system = false;
        session = false;
        command = null;
        parts = new ArrayList<>();
        user = null;
    }
    void system()
    {
        Memory.memoryInit();
        Scheduler pro = new Scheduler();                    //TO NIECH TU BEDZIE KOMU TO SZKODZI?
        Process_container con = new Process_container();    //TO NIECH TU BEDZIE KOMU TO SZKODZI?
        system=true;
        System.out.println("[Interface]: Booting Interface");
        start();

        while(system)
        {

            login();
            while(session)
            {
                try
                {
                Scanner sc = new Scanner(System.in);
                System.out.print("bigOS:\\User>");
                command=sc.nextLine();

                //System.out.println("[Interface]: Przechwycenie bufforu");
                //System.out.println("[Interface]: Podzielenie bufforu na komendy i parametry");
                cut();
                String x="";
                if(!parts.isEmpty())
                {
                for(int i=0;i<parts.get(0).length();i++)
                {
                    if((int)parts.get(0).charAt(i)<=90&&(int)parts.get(0).charAt(i)>=65)
                    {
                        int help = (int)parts.get(0).charAt(i);
                        help=help+32;
                        char y = (char)help;
                        x=x+y;
                    }
                    else
                        x=x+parts.get(0).charAt(i);
                    
                }
                }
                else
                {
                   System.out.println("[Interface]: Wrong command");
                }
                parts.set(0, x);
                x="";
                if(parts.size()!=1&&!parts.isEmpty())
                {
                for(int i=0;i<parts.get(1).length();i++)
                {
                    if((int)parts.get(1).charAt(i)<=90&&(int)parts.get(1).charAt(i)>=65)
                    {
                        int help = (int)parts.get(1).charAt(i);
                        help=help+32;
                        char y = (char)help;
                        x=x+y;
                    }
                    else
                        x=x+parts.get(1).charAt(i);
                    
                }
                }
                if("/pid".equals(x)||"/im".equals(x))
                    parts.set(1, x);
                execute();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }
        }
    }
    void cut()
    {
        if(command.length()==0)
        {
            parts=new ArrayList<>();
            return;
        }
        parts.clear();
        String help="";
        for(int i=0;i<command.length();i++)
        {
            if(command.charAt(i)!=' ')
            {
                help=help+command.charAt(i);
                if(i==command.length()-1)
                {
                if(!"".equals(help))
                parts.add(help);
                }
            }
            else
            {
                if(i==command.length()-1)
                {
                    if(!"".equals(help))
                parts.add(help);
                }
                else if(command.charAt(i+1)!=' ')
                {
                     if(!"".equals(help))
                parts.add(help);
                help="";
                }
                
                  
            }
            

        }

    }
    void start()
    {
        System.out.println("\n \t\t        bigOS 1.0\n\t***NAJSMACZNIESZY SYSTEM OPERACYJNY***");
    }
    void login()
    {
        while(true)
        {
            Scanner sc = new Scanner(System.in);
            System.out.printf("User: ");
            user = sc.nextLine();
            System.out.printf("Password: ");
            String password = sc.nextLine();
            // Zabezpieczeniowiec sprawdza poprawnosc i laczy
            // Jezeli polaczyl jest return i session=true
            // Jezeli nie, pytanie czy chce zakonczyc prace systemu
            session=true;
            return;
        }
    }
    void execute(){
        if(parts.size()!=0)
        switch(parts.get(0))
        {
             case "move":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Calling RAM function to compact");
                Memory.move();}
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "show_run":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Showing running process");
                Scheduler.showRunning();}
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "show_ready":
            {
                if(parts.size()==1){
                    System.out.println("[Interface]: Showing ready processes");
                    Scheduler.showReadyProcesses();
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "show_messages":
            {
                if(parts.size()==2)
                {
                    System.out.println("[Interface]: Displaying queue of messages of process with PID: "+parts.get(1));
                    Process_container.get_by_name(parts.get(1)).display_messages_queue();
                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "messages_sem":
            {
                if(parts.size()==2)
                {
                    System.out.println("[Interface]: Displaying semaphore's key of process with PID: "+parts.get(1));
                    Process_container.get_by_name(parts.get(1)).display_messages_semaphore();
                }
                else
                    System.out.println("[Interface]: Wrong argument");
            }
            case "help":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Enable commands:");
                    System.out.println(""
                            + "GO                       - EXECUTING ONE COMMAND OF ASSEMBLER CODE;\n"
                            + "DIR                      - SHOWING ALL FILES;\n"
                            + "SHUTDOWN                 - SHUTDOWNS OPERATION SYSTEM;\n"
                            + "SHUTDOWN -L              - LOGS OUT CLIENT ;\n"
                            + "CP [P1] [P2] [P3]        - CREATES PROCESS [P1] WITH FILE [P2] AND PRIORITY [P3];\n"
                            + "CF [P1]                  - CREATES FILE CALLED [P1];\n"
                            + "WF [P1] [P2] ... [PN]    - ADDS TO FILE FOLLOWING ARGUMENTS;\n"
                            + "DF [P1]                  - DELETES FILE [P1];\n"
                            + "SHOW_MESSAGE /PID [P1]   - SHOWS QUEUE OF MESSAGES OF PROCESS WITH PID [P1];\n"
                            + "SHOW_MESSAGE /IM [P1]    - SHOWS QUEUE OF MESSAGES OF PROCESS CALLED [P1];\n"
                            + "MESSAGES_SEM [P1]        - SHOWS SEMAPHORE'S KEY OF PROCESS WITH PID [P1];\n"
                            + "TASKLIST                 - SHOWS ALL PROCESSES;\n"
                            + "TASKLIST /PID [P1]       - SHOWS PROCESS WITH PID [P1];\n"
                            + "TASKLIST /IM [P1]        - sHOWS PROCESS CALLED [P1];\n"
                            + "TASKKILL                 - DELETES ALL PROCESSES;\n"
                            + "TASKKILL /PID [P1]       - DELETES PROCESS OF PID [P1];\n"
                            + "TASKKILL /IM [P1]        - DELETES PROCESS CALLED[P1];\n"
                            + "TYPE [P1]                - PRINTS FILE CALLED [P1];\n"
                            + "TYPE [P1] [P2] [P3]      - SHOWS [P3] NEXT BYTES OF FILE CALLED [P1] STARTING FROM [P3]. BYTE;\n"
                            + "TYPE [P1] >> [P2]        - CREATES NEW FILE CALLED [P2] AS COPY OF [P1];\n"
                            + "TYPE [P1] > [P2]         - ADDS TO FILE CALLED [P2] CONTENT OF FILE CALLED [P1];\n"
                            + "FAT                      - SHOWS FILE SYSTEM;\n"
                            + "FCB [P1]                 - SHOWS FCB OF FILE CALLED[P1];\n"
                            + "SEM [P1]                 - SHOWS SEMAPHORE OD FILE CALLED [P1];\n"
                            + "PRINT_MEMORY             - PRINTS MEMORY RAM;\n"
                            + "SHOW_RUN                 - SHOWS RUNNING PROCESS;\n"
                            + "SHOW_READY               - SHOWS READY PROCESSES\n"
                            + "MOVE                     - COMPACTS RAM\n");
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "print_memory":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Prints memory RAM");
                    Memory.printMemory();
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                
                break;
            }
            case "fat":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Shows file system");
                    FileManagement.printFileSystem();
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "fcb":
                    {
                        if(parts.size()==2)
                        {
                            System.out.println("[Interface]: Showing FCB of "+parts.get(1));
                            FileManagement.displayFCB(parts.get(1));
                        }
                        else
                            System.out.println("[Interface]: Wrong argument");
                        break;
                    }
            case "sem":
            {
                if(parts.size()==2) {
                    System.out.println("[Interface]: Showing semaphore of file " + parts.get(1));
                    FileManagement.printSem(parts.get(1));
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "dir":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Showing all files");
                    // Zabezpieczeniowiec sprawdza
                    FileManagement.displayRoot();
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "shutdown":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Closing operation system");
                    session=false;
                    system = false;
                }
                else if(parts.size()==2 && parts.get(1).equals("-l"))
                {
                    System.out.println("[Interface]: Logging out client");
                    session=false;
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "df":
            {
                if(parts.size()==2)
                {
                    // Zabezpieczeniowiec -> sprawdza
                    boolean blad = FileManagement.delete(parts.get(1));
                    if(blad)
                        System.out.println("[Interface]: Deleting file called " + parts.get(1));
                    else
                    {
                        System.out.println("[Interface]: ERROR ");
                    }

                }
                else
                    System.out.println("[Interface]: Wrong argument");

                break;
            }
            case "cf":
            {
                if(parts.size()==2)
                {
                    System.out.println("[Interface]: Creating file called " + parts.get(1));
                    boolean blad = FileManagement.create(parts.get(1),user);
                    if(blad)
                    {
                        
                        // Zabezpieczeniowiec wpisuje
                    }
                    else
                    {
                    }

                }
                else
                    System.out.println("[Interface]: Wrong argument");

                break;
            }
            case "wf":
            {
                if(parts.size()>2)
                {
                    String buffor="";
                    for(int i=2;i<parts.size();i++)
                    {
                        buffor=buffor+parts.get(i);
                        if(i<parts.size()-1)
                            buffor=buffor+" ";
                    }
                    // Zabezpieczeniowiec sprawdza czy mozna wpisac
                    boolean blad = FileManagement.write(parts.get(1),buffor);
                    if(blad){
                        System.out.println("[Interface]: Adding to file called "+parts.get(1)+ " text: "+buffor);

                    }
                    else
                    {
                        System.out.println("[Interface]: ERROR ");
                    }

                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "taskkill":
            {
                if(parts.size()==3)
                {
                    if(parts.get(1).equals("/pid"))
                    {
                        System.out.println("[Interface]: Deleting process of PID: "+parts.get(2));
                        Process_container.delete(Integer.parseInt(parts.get(2)));
                    }
                    else if(parts.get(1).equals("/im"))
                    {
                        System.out.println("[Interface]: Deleting process called"+parts.get(2));
                        Process_container.delete(parts.get(2));
                    }
                    else
                        System.out.println("[Interface]: Wrong argument");
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "tasklist":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Showing all processes");
                    Process_container.show_all_processes();
                }
                else if(parts.size()==3)
                {
                    if(parts.get(1).equals("/pid"))
                    {
                        System.out.println("[Interface]: Showing process with PID " + parts.get(2));
                        Process_container.display_proces(Integer.parseInt(parts.get(2)));
                    }
                    else if(parts.get(1).equals("/im"))
                    {
                        System.out.println("[Interface]: Showing process called " + parts.get(2));
                        Process_container.display_proces(parts.get(2));
                    }
                    else
                        System.out.println("[Interface]: Wrong argument");
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "cp":
            {
                if(parts.size()==4)
                {
                    for(int i=0;i<parts.get(3).length();i++) {
                        if (Character.isDigit(parts.get(3).charAt(i))) ;
                        else {
                            System.out.println("[Interface]: Wrong argument");
                            break;
                        }
                    }
                    System.out.println("[Interface]: Creates process called " + parts.get(1) + " with file " + parts.get(2) + " and priority " + parts.get(3));
                    Process_container.create_process(parts.get(1),parts.get(2),Integer.parseInt(parts.get(3)));
                }
                else
                    System.out.println("[Interface]: Wrong argument");
                break;
            }
            case "type":
            {
                if(parts.size()==2)
                {
                    System.out.println("[Interface]: Showing file called "+ parts.get(1));
                    System.out.println(FileManagement.readFile(parts.get(1)));
                }
                else if(parts.size()==4)
                {
                    if((int)parts.get(2).charAt(0)==62)
                        if(parts.get(2).length()== 2)
                        {
                            if((int)parts.get(2).charAt(1)==62)
                            {
                    System.out.println("[Interface]: Creating file called " + parts.get(3)+ " with text of file called " + parts.get(1));
                    String x = FileManagement.readFile(parts.get(1));
                    FileManagement.create(parts.get(3), user);
                    FileManagement.write(parts.get(3),x);
                    // Zabezpieczeniowiec!!!
                            }
                            else
                            {
                                System.out.println("[Interface]: Wrong argument");
                                
                            }
                        }
                        else if (parts.get(2).length()==1)
                        {
                    System.out.println("[Interface]: Adding to file called "+ parts.get(3)+ " text of file "+parts.get(1));
                    String x = FileManagement.readFile(parts.get(1));
                    FileManagement.write(x, parts.get(3));
                    // Zabezpieczeniowiec!!!
                        }
                        else
                            System.out.println("[Interface]: Wrong argument");
                    else {
                        System.out.println("[Interface]: Showing " + parts.get(3) + " bytes of file called " + parts.get(1) + " starting from ["+parts.get(2)+"]");
                        String x = FileManagement.read(parts.get(1), Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)));
                        System.out.println(x);
                    }
                }
                else
                {
                    System.out.println("[Interface]: Wrong argument");
                }
                break;
            }
            case "net":
            {
                if(parts.size()>1)
                {

                    if("user".equals(parts.get(1)))
                    {
                        if(parts.size()==3)
                        {
                            //Wypisanie info o uzytkowniku parts[2]
                        }
                        else if(parts.size()==4)
                        {
                            if("/delete".equals(parts.get(3)))
                            {
                                //usuwa uzytkownika parts[2]
                            }
                            else
                            {
                                System.out.println("[Interface]: Wrong argument");
                            }
                        }
                        else if(parts.size()==5)
                        {
                            if("/add".equals(parts.get(4)))
                            {
                                //Dodaje uzytkownika o nazwie parts[2] i hasle parts[3]
                            }
                            else
                            {
                                System.out.println("[Interface]: Wrong argument");
                            }
                        }
                        else
                        {
                            System.out.println("[Interface]: Wrong argument");
                        }

                    }
                    else
                    {
                        System.out.println("[Interface]: Wrong argument");
                    }
                }
                else
                {

                }
                break;
            }
            case "go":
            {
                if(parts.size()==2)
                {
                Interpreter.go(Integer.getInteger(parts.get(2)));
                }
                else if (parts.size()==1)
                        {
                            Interpreter.go(1);
                        }
                else
                    System.out.println("[Interface]: Wrong argument");
                    
            }
            default:
            {
                System.out.println("[Interface]: Wrong command");
                System.out.println("'"+command+ "' is unknown command.");
                break;
            }
        }

    }
}