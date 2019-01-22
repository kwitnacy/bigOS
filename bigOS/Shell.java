
package bigOS;


import Procesy.Process_container;
import RAM.Memory;
import filemodule.FileManagement;

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
        system=true;
        System.out.println("[Interface]: Rozpoczecie pracy interfejsu");
        start();
        while(system)
        {

            login();
            while(session)
            {
                Scanner sc = new Scanner(System.in);
                System.out.print("bigOS:\\User>");
                command=sc.nextLine();
                System.out.println("[Interface]: Przechwycenie bufforu");
                for(int i=0;i<command.length();i++)
                {
                    if((int)command.charAt(i)<=90&&(int)command.charAt(i)>=65)
                    {
                        int help = (int)command.charAt(i);
                        help=help+32;
                        String newCommand = command.substring(0,i)+(char)help+command.substring(i+1);
                        command=newCommand;
                    }
                }
                System.out.println("[Interface]: Podzielenie bufforu na komendy i parametry");
                cut();
                execute();
            }
        }
    }
    void cut()
    {
        parts.clear();
        String help="";
        for(int i=0;i<command.length();i++)
        {
            if(command.charAt(i)!=' ')
            {
                help=help+command.charAt(i);
            }
            else
            {
                parts.add(help);
                help="";
            }
            if(i==command.length()-1)
            {
                parts.add(help);
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
        switch(parts.get(0))
        {

            case "help":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Wypisanie dostepnych komend");
                    System.out.println(""
                            + "GO                       - WYKONANIE PRZEZ INTERPRETER JEDNEGO ROZKAZU ASSEMBLEROWEGO;*czekam na funkcje*\n"
                            + "DIR                      - WYSWIETLA WSZYSTKIE PLIKI W SCIEZCE;\n"
                            + "SHUTDOWN                 - KONCZY PRACE SYSTEMU;\n"
                            + "SHUTDOWN -L              - WYLOGOWANIE KLIENTA;\n"
                            + "CP [P1] [P2] [P3]        - TWORZY PROCES [P1] Z PLIKU [P2] O PRIORYTECIE [P3];\n"
                            + "CP [P1] [P2] [P3] [P4]   - TWORZY PROCES [P1] Z PLIKU [P2] O PRIORYTECIE [P3] ZAJMUJACY [P4] BAJTOW W PAMIECI RAM;\n"
                            + "CF [P1]                  - TWORZY PLIK O NAZWIE P1;\n"
                            + "WF [P1] [P2] ... [PN]    - DOPISUJE DO PLIKU [P1] ZAWARTOSC KOLEJNYCH PARAMETROW;\n"
                            + "DF [P1]                  - USUWA PLIK [P1];\n"
                            + "TASKLIST                 - WYSWIETLA WSZYSTKIE PROCESY;\n"
                            + "TASKLIST /PID [P1]       - WYSWIETLA PROCES O PID [P1];\n"
                            + "TASKLIST /IM [P1]        - WYSWIETLA PROCES O NAZWIE [P1];\n"
                            + "TASKKILL                 - USUWA WSZYSTKIE PROCESY;\n"
                            + "TASKKILL /PID [P1]       - USUWA PROCES O PID [P1];\n"
                            + "TASKKILL /IM [P1]        - USUWA PROCES O NAZWIE [P1];\n"
                            + "TYPE [P1]                - WYSWIETLA ZAWARTOSC PLIKU [P1];\n"
                            + "TYPE [P1] >> [P2]        - TWORZY NOWY PLIK [P2] Z ZAWARTOSCIA PLIKU [P1];\n"
                            + "TYPE [P1] > [P2]         - DOPISUJE DO PLIKU [P2] ZAWARTOSC PLIKU [P1];\n"
                            + "FAT                      - WYSWIETLA ZAWARTOSC MODULU PLIKOW;\n"
                            + "FCB [P1]                 - WYSWIETLA FCB PLIKU [P1];\n"
                            + "SEM [P1]                 - WYSWIETLA WARTOSC SEMAFORA PLIKU [P1];\n"
                            + "PRINT_MEMORY             - WYSWIETLA INFORMACJE NA TEMAT RAMU;\n");
                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "print_memory":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Wywolanie funkcji wypisujacej informacje na temat RAM'u");
                    Memory.printMemory();
                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                
                break;
            }
            case "fat":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Wywolanie funkcji wypisujacej zawartość modułu plikow");
                    FileManagement.printFileSystem();
                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "fcb":
                    {
                        if(parts.size()==2)
                        {
                            System.out.println("[Interface]: Wywolanie funkcji wypisujacej FCB pliku "+parts.get(1));
                            FileManagement.displayFCB(parts.get(1));
                        }
                        else
                            System.out.println("[Interface]: Bledne argumenty");
                        break;
                    }
            case "sem":
            {
                if(parts.size()==2)
                        {
                            System.out.println("[Interface]: Wywolanie funkcji wypisujacej wartosc semafora pliku" + parts.get(1));
                            FileManagement.displayFCB(parts.get(1));
                        }
                        else
                            System.out.println("[Interface]: Bledne argumenty");
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
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "shutdown":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Zamkniecie systemu");
                    session=false;
                    system = false;
                }
                else if(parts.size()==2 && parts.get(1).equals("-l"))
                {
                    System.out.println("[Interface]: Wylogowanie uzytkownika");
                    session=false;
                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "df":
            {
                if(parts.size()==2)
                {
                    // Zabezpieczeniowiec -> sprawdza
                    boolean blad = FileManagement.delete(parts.get(1));
                    if(blad)
                        System.out.println("[Interface]: Wywolanie funkcji usuwajacej plik o nazwie "+parts.get(1));
                    else
                    {
                        System.out.println("[Interface]: Blad "+blad);
                    }

                }
                else
                    System.out.println("[Interface]: Bledne argumenty");

                break;
            }
            case "cf":
            {
                if(parts.size()==2)
                {
                    System.out.println("[Interface]: Wywolanie funkcji tworzacej plik o nazwie "+parts.get(1));
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
                    System.out.println("[Interface]: Bledne argumenty");

                break;
            }
            case "wf":
            {
                if(parts.size()>2)
                {
                    String buffor="";
                    for(int i=2;i<parts.size();i++)
                        buffor=buffor+parts.get(i);
                    // Zabezpieczeniowiec sprawdza czy mozna wpisac
                    boolean blad = FileManagement.write(parts.get(1),buffor);
                    if(blad){
                        System.out.println("[Interface]: Dopisano "+buffor);

                    }
                    else
                    {
                        System.out.println("[Interface]: Blad "+blad);
                    }

                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "taskkill":
            {
                if(parts.size()==3)
                {
                    if(parts.get(1).equals("/pid"))
                    {
                        System.out.println("[Interface]: Wywolanie funkcji usuwajacej proces o PID "+parts.get(2));
                        Process_container.delete(Integer.parseInt(parts.get(2)));
                    }
                    else if(parts.get(1).equals("/im"))
                    {
                        System.out.println("[Interface]: Wywolanie funkcji usuwajacej proces o nazwie "+parts.get(2));
                        Process_container.delete(parts.get(2));
                    }
                    else
                        System.out.println("[Interface]:Bledne argumenty");
                }
                else
                    System.out.println("[Interface]:Bledne argumenty");
                break;
            }
            case "tasklist":
            {
                if(parts.size()==1)
                {
                    System.out.println("[Interface]: Wywolanie funkcji wyswietlajacej wszystkie procesy");
                    Process_container.show_all_processes();
                }
                else if(parts.size()==3)
                {
                    if(parts.get(1).equals("/pid"))
                    {
                        System.out.println("[Interface]: Wywolanie funkcji wyswietlajacej proces o PID "+parts.get(2));
                        Process_container.display_proces(Integer.parseInt(parts.get(2)));
                    }
                    else if(parts.get(1).equals("/im"))
                    {
                        System.out.println("[Interface]: Wywolanie funkcji wyswietlajacej proces o nazwie "+parts.get(2));
                        Process_container.display_proces(parts.get(2));
                    }
                    else
                        System.out.println("[Interface]:Bledne argumenty");
                }
                else
                    System.out.println("[Interface]:Bledne argumenty");
                break;
            }
            case "cp":
            {
                if(parts.size()==4)
                {
                    for(int i=0;i<parts.get(3).length();i++) {
                        if (Character.isDigit(parts.get(3).charAt(i))) ;
                        else {
                            System.out.println("[Interface]: Bledne argumenty");
                            break;
                        }
                    }

                    Process_container.create_process(parts.get(1),parts.get(2),Integer.parseInt(parts.get(3)));
                }
                else if(parts.size()==5)
                {
                    for(int i=0;i<parts.get(3).length();i++) {
                        if (Character.isDigit(parts.get(3).charAt(i))) ;
                        else {
                            System.out.println("[Interface]: Bledne argumenty");
                        }
                    }
                    for(int i=0;i<parts.get(4).length();i++) {
                        if (Character.isDigit(parts.get(4).charAt(i))) ;
                        else {
                            System.out.println("[Interface]: Bledne argumenty");
                        }
                    }
                    Process_container.create_process(parts.get(1),parts.get(2),Integer.parseInt(parts.get(3)),Integer.parseInt(parts.get(4)));
                }
                else
                    System.out.println("[Interface]: Bledne argumenty");
                break;
            }
            case "type":
            {
                if(parts.size()==2)
                {
                    System.out.println("[Interface]: Wywolanie funkcji wyswietlajacej zawartosc pliku "+ parts.get(1));
                    System.out.println(FileManagement.readFile(parts.get(1)));
                }
                else if(parts.size()==4)
                {
                    if((int)parts.get(2).charAt(0)==62)
                        if(parts.get(2).length()== 2)
                        {
                            if((int)parts.get(2).charAt(1)==62)
                            {
                    System.out.println("[Interface]: Wywolanie funkcji tworzacych nowy plik "+ parts.get(3)+ " z zawartoscia pliku "+parts.get(3));
                    String x = FileManagement.readFile(parts.get(1));
                    FileManagement.create(x, user);
                    // Zabezpieczeniowiec!!!
                            }
                            else
                                System.out.println("[Interface]: Bledne argumenty");
                        }
                        else if (parts.get(2).length()==1)
                        {
                    System.out.println("[Interface]: Wywolanie funkcji dopisujacych do pliku "+ parts.get(3)+ " zawartosc pliku "+parts.get(3));
                    String x = FileManagement.readFile(parts.get(2));
                    FileManagement.write(x, parts.get(3));
                    // Zabezpieczeniowiec!!!
                        }
                        else
                            System.out.println("[Interface]: Bledne argumenty");
                    else {
                        System.out.println("[Interface]: Bledne argumenty");
                    }
                }
                else
                {
                    System.out.println("[Interface]: Bledne argumenty");
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
                                System.out.println("[Interface]: Bledne argumenty");
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
                                System.out.println("[Interface]: Bledne argumenty");
                            }
                        }
                        else
                        {
                            System.out.println("[Interface]: Bledne argumenty");
                        }

                    }
                    else
                    {
                        System.out.println("[Interface]: Bledne argumenty");
                    }
                }
                else
                {

                }
                break;
            }
            default:
            {

                System.out.println("[Interface]: '"+command+ "' jest nieznana komenda.");
                break;
            }
        }

    }



}