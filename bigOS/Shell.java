
package bigOS;


import Interpreter.Interpreter;
import Processor.Scheduler;
import Procesy.Process_container;
import RAM.Memory;
import FileModule.FileManagement;
import Security.*;

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
    User_list Security_UL=new User_list();
    Access_Control_Matrix Security_ACM=new Access_Control_Matrix();
    Scanner sc=new Scanner(System.in);
    String username;
    Shell()
    {
        system = false;
        session = false;
        command = null;
        parts = new ArrayList<>();
        username = null;
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
                if("/pid".equals(x)||"/im".equals(x)||"username".equals(x)||"-l".equals(x))
                    parts.set(1, x);
                execute();
            }
            system=!conf();
        }
    }

    boolean conf() //confirm
    {
        System.out.print("[Interface]: Confirm shutdown [Y/N]: ");
        Scanner co= new Scanner(System.in);
        String buf=co.next();
        buf=buf.toLowerCase();

        if (buf.equals("y"))
        {
            return true;
        }
        else
            return false;
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
        System.out.println("\n \t\t        bigOS 1.0\n\t***NAJSMACZNIEJSZY SYSTEM OPERACYJNY***");
    }
    void login()
    {
        session=Security_UL.login();
        username=Security_UL.current_user.getUsername();
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
                                + "LOGOFF                   - LOGS OUT CLIENT ;\n"
                                + "CP [P1] [P2] [P3]        - CREATES PROCESS [P1] WITH FILE [P2] AND PRIORITY [P3];\n"
                                + "CF [P1]                  - CREATES FILE CALLED [P1];\n"
                                + "WF [P1] [P2] ... [PN]    - ADDS TO FILE FOLLOWING ARGUMENTS;\n"
                                + "DF [P1]                  - DELETES FILE [P1];\n"
                                + "SHOW_MESSAGES [P1]       - SHOWS QUEUE OF MESSAGES OF PROCESS CALLED [P1];\n"
                                + "MESSAGES_SEM [P1]        - SHOWS SEMAPHORE'S KEY OF PROCESS WITH PID [P1];\n"
                                + "TASKLIST                 - SHOWS ALL PROCESSES;\n"
                                + "TASKLIST /PID [P1]       - SHOWS PROCESS WITH PID [P1];\n"
                                + "TASKLIST /IM [P1]        - sHOWS PROCESS CALLED [P1];\n"
                                + "TASKKILL                 - DELETES ALL PROCESSES;\n"
                                + "TASKKILL /PID [P1]       - DELETES PROCESS OF PID [P1];\n"
                                + "TASKKILL /IM [P1]        - DELETES PROCESS CALLED[P1];\n"
                                + "TYPE [P1]                - PRINTS FILE CALLED [P1];\n"
                                + "TYPE [P1] [P2] [P3]      - SHOWS [P2] NEXT BYTES OF FILE CALLED [P1] STARTING FROM [P3]. BYTE;\n"
                                + "TYPE [P1] >> [P2]        - CREATES NEW FILE CALLED [P2] AS COPY OF [P1];\n"
                                + "TYPE [P1] > [P2]         - ADDS TO FILE CALLED [P2] CONTENT OF FILE CALLED [P1];\n"
                                + "FAT                      - SHOWS FILE SYSTEM;\n"
                                + "FCB [P1]                 - SHOWS FCB OF FILE CALLED[P1];\n"
                                + "SEM [P1]                 - SHOWS SEMAPHORE OD FILE CALLED [P1];\n"
                                + "PRINT_MEMORY             - PRINTS MEMORY RAM;\n"
                                + "PRINT_MEMORY [P1]        - PRINTS MEMORY RAM ON ADRESS [P1];\n"
                                + "PRINT_MEMORY [P1] [P2]   - PRINTS MEMORY RAM BETWEEN ADRESSES [P1] AND [P2];\n"
                                + "SHOW_RUN                 - SHOWS RUNNING PROCESS;\n"
                                + "SHOW_READY               - SHOWS READY PROCESSES\n"
                                + "MOVE                     - COMPACTS RAM\n"
								+ "NET USER [P1]            - SHOWS INFO ABOUT [P1] USER\n"
								+ "NET USER [P1] /DELETE    - DELETES [P1] USER \n"
								+ "NET USER [P1] [P2] /ADD  - ADDS USER [P1] WITH PASSWORD [P2]\n"
								+ "NET USER [P1] [P2] /PWD  - CHANGE CURRENT USER PASSWORD [P1] TO [P2]\n"
								+ "NET USER [P1] [P2] /USN  - CHANGE CURRENT USERNAME TO [P2], CONFIRM WITH CURRENT PASSWORD [P1]\n"
								+ "MYACL                    - VIEWS CURRENT USER'S ACCESS CONTROL LIST\n"
								+ "ACM                      - VIEWS ACCESS CONTROL MATRIX CONTAINER\n"
								+ "EDITACM                  - [SYSONLY] EDIT ACCESS CONTROL MATRIX ENTRY\n"
								+ "ALLUSERS                 - LISTS ALL AVAILABLE USERS\n"

                        );

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
                    else if(parts.size()==2)
                    {
                        System.out.println("[Interface]: Prints memory RAM on adress:["+parts.get(1)+"]");
                        Memory.printMemory(Integer.parseInt(parts.get(1)));
                    }
                    else if(parts.size()==3)
                    {
                        System.out.println("[Interface]: Prints memory RAM between adresses:["+parts.get(1)+"] and["+parts.get(2)+"]");
                        Memory.printMemory(Integer.parseInt(parts.get(1)),Integer.parseInt(parts.get(2)));
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
                        System.out.println("[Interface]: Wywolanie funkcji wypisujacej wszystkie pliki");
                        // Zabezpieczeniowiec sprawdza
                        FileManagement.displayRoot();

                    }
                    else if (parts.size()==2 && parts.get(1).equals("-l"))
                    {
                        Security_ACM.dir_l(FileManagement.get_root());
                    }
                    else
                        System.out.println("[Interface]: Bledne argumenty");
                    break;
                }
                case "shutdown":
                {
                    if(parts.size()==1)
                    {
                        System.out.println("[Interface]: System shutdown");
                        session=false;
                        system = false;
                    }
                    else if(parts.size()==2 && parts.get(1).equals("-l"))
                    {
                        logout_interface();
                    }
                    else
                        System.out.println("[Interface]: Bledne argumenty");
                    break;
                }
                case "logoff": {
                    logout_interface();
                    break;
                }
                case "df":
                {
                    if(parts.size()==2)
                    {
                        if (Security_ACM.check_permission("df",Security_UL.current_user.getSaid(),parts.get(1)))
                        {
                            if(FileManagement.delete(parts.get(1)))
                            {
                                System.out.println("[Interface]: Function delting file named "+ parts.get(1)+" called.");
                            }
                        }
                    }
                    else
                        System.out.println("[Interface]: Wrong argument");

                    break;
                }
                case "myacl":
                {
                    Security_ACM.my_ACL(Security_UL.current_user.getSaid());
                    break;
                }
                case "allusers":
                {
                    Security_UL.allusers();
                    break;
                }
                case "editacm":
                {
                    System.out.println("[Interface]: Calling function for ACM edition.");
                    if (!Security_ACM.edit_ACM(Security_UL.current_user.getSaid()))
                    {
                        System.out.println("[Interface]: ACM edition failed.");
                        break;
                    }
                    else
                    {
                        System.out.println("[Interface]: ACM edition success.");
                        break;
                    }
                }
                case "acm": {
                    Security_ACM.view_ACM();
                    break;
                }
                case "cf":
                {
                    if(parts.size()==2)
                    {
                        System.out.println("[Interface]: Wywolanie funkcji tworzacej plik o nazwie "+parts.get(1));
                        if(Security_ACM.check_permission("cf",Security_UL.current_user.getSaid(),parts.get(1)))
                        {
                            FileManagement.create(parts.get(1),username);
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
                        if (Security_ACM.check_permission("wf",Security_UL.current_user.getSaid(),parts.get(1)))
                        {
                            if(FileManagement.write(parts.get(1),buffor)) {
                                System.out.println("[Interface]: Wywolanie funkcji dopisujacej do pliku " + parts.get(1) + " tekstu: " + buffor);
                            }
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
                        if (Security_ACM.check_permission("rf",Security_UL.current_user.getSaid(),parts.get(1)))
                        {
                            System.out.println(FileManagement.readFile(parts.get(1)));
                        }}
                    else if(parts.size()==4)
                    {
                        if((int)parts.get(2).charAt(0)==62)
                            if(parts.get(2).length()== 2)
                            {
                                if((int)parts.get(2).charAt(1)==62)
                                {
                                    System.out.println("[Interface]: Creating file called " + parts.get(3)+ " with text of file called " + parts.get(1));
                                    if (Security_ACM.check_permission("rf",Security_UL.current_user.getSaid(),parts.get(1)))
                                    {
                                        String x = FileManagement.readFile(parts.get(1));
                                        if (Security_ACM.check_permission("cf",Security_UL.current_user.getSaid(), parts.get(3)))
                                        {
                                            System.out.println("[Interface]: Calling function responsible for pizda");
                                            FileManagement.create(parts.get(3), username);
                                            FileManagement.write(parts.get(3),x);
                                        }
                                    }
                                }
                                else
                                {
                                    System.out.println("[Interface]: Wrong argument");

                                }
                            }
                            else if (parts.get(2).length()==1)
                            {
                                if (Security_ACM.check_permission("rf", Security_UL.current_user.getSaid(), parts.get(1)))
                                {
                                    String x = FileManagement.readFile(parts.get(1));
                                    if (Security_ACM.check_permission("wf", Security_UL.current_user.getSaid(),parts.get(3)))
                                    {
                                        FileManagement.write(x, parts.get(3));
                                    }
                                }}
                            else
                                System.out.println("[Interface]: Wrong argument");
                        else {
                            System.out.println("[Interface]: Showing " + parts.get(3) + " bytes of file called " + parts.get(1) + " starting from ["+parts.get(2)+"]");
                            if (Security_ACM.check_permission("rf",Security_UL.current_user.getSaid(),parts.get(1)))
                            {
                                System.out.println(FileManagement.read(parts.get(1), Integer.getInteger(parts.get(2)), Integer.getInteger(parts.get(3))));
                            }
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
                                System.out.println("[Interface]: Calling NET USER [P1].");
                                //Wypisanie info o uzytkowniku parts[2]
                                Security_UL.user_info(parts.get(2));
                            }
                            else if(parts.size()==4)
                            {
                                if("/delete".equals(parts.get(3)))
                                {
                                    System.out.println("[Interface]: Calling NET USER [P1] /DELETE.");
                                    Security_UL.del_acc(parts.get(2));
                                    //usuwa uzytkownika parts[2]
                                }
                                else
                                {
                                    System.out.println("[Interface]: Wrong argument.");
                                }
                            }
                            else if(parts.size()==5)
                            {
                                if("/add".equals(parts.get(4)))
                                {
                                    System.out.println("[Interface]: Calling NET USER [P1] [P2] /ADD.");
                                    if(Security_UL.create_acc(parts.get(2),parts.get(3)))
                                    {
                                        System.out.println("[Interface]: User created.");
                                    }
                                    else
                                    {
                                        System.out.println("[Interface]: User creation error.");
                                    }    //Dodaje uzytkownika o nazwie parts[2] i hasle parts[3]
                                }
                                else if ("/pwd".equals(parts.get(4))) {
                                    System.out.println("[Interface]: Calling NET USER [P1] [P2] /PWD.");
                                    if (Security_UL.change_pwd(parts.get(2),parts.get(3)))
                                    {
                                        System.out.println("[Interface]: User changed password.");
                                    }
                                    else
                                    {
                                        System.out.println("[Interface]: Password changing failed.");
                                    }
                                }
                                else if ("/usn".equals(parts.get(4))) {
                                    System.out.println("[Interface]: Calling NET USER [P1] [P2] /USN.");
                                    if (Security_UL.change_usrname(parts.get(2), parts.get(3)))
                                    {
                                        System.out.println("[Interface]: User changed his username.");
                                    }
                                    else
                                    {
                                        System.out.println("[Interface]: Changing username failed.");
                                    }
                                }
                                else
                                {
                                    System.out.println("[Interface]: NET USER command - wrong argument");
                                }
                            }
                            else
                            {
                                System.out.println("[Interface]: NET command- wrong argument");
                            }

                        }
                        else
                        {
                            System.out.println("[Interface]: Wrong argument");
                        }
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
                    break;
                }
                default:
                {
                    System.out.println("[Interface]: Wrong command");
                    System.out.println("'"+command+ "' is unknown command.");
                    break;
                }
            }

    }


    private void logout_interface() {
        if (Security_UL.logout()) {
            username = null;
            System.out.println("[Interface]: User logged out.");

            session = false;
        } else {
            System.out.println("[Interface]: User did not log out.");
        }
    }
}