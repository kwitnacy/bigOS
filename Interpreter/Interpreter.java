package Interpreter;
import Procesy.Process_container;
import filemodule.FileManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {

    private int A,B,C,D, program_counter, base, limit, PID;
    private String rozkaz, calyRozkaz;
    private String user;
    private FileManagement fileManagement;
    private Process_container process_container;
    public Interpreter(FileManagement fileManagement, Process_container process_container)
    {
        //A = klasaJonasza running getAX();
        //B = klasaJonasza.get_BX();
        //C = klasaJonasza.get_CX();
        //D = klasaJonasza.get_DX();
        //base = klasaJonasza.get_Base();
        //limit = klasaJonasza.get_Limit();
        program_counter = 0;
        //PID = klasaJonasza.get_PID
        getOrder(); //tutaj pierwszy rozkaz programu
        this.fileManagement = fileManagement;
        this.process_container = process_container;
    }
    public void updateProcessor()
    {
        //klasaJonasza running setAX(A);
        //klasaJonasza.set_BX(B);
        //klasaJonasza.set_CX(C);
        //klasaJonasza.set_DX(D);
    }
    public void display()
    {
        System.out.println("PID: "+PID);
        System.out.println("rejestr A: "+A);
        System.out.println("rejestr B: "+B);
        System.out.println("rejestr C: "+C);
        System.out.println("rejestr D: "+D);
        System.out.println("program_counter: "+ program_counter);
    }
    private void getOrder()
    {
        int i =0;
        /* while(rozkaz nie jest caly)
        {
            calyRozkaz = rozkaz + pamiec[base + program_counter];
            if(i<2) rozkaz = rozkaz + pamiec[base + program_counter]
            i++;
        } */
        System.out.println("wykonywany rozkaz: "+rozkaz);
    }
    public void executeProgram() //to bedzie wykonywane w main
    {
        while(!rozkaz.equals("HT"))
        {
            executeOrder();
            updateProcessor();
            display();
            getOrder();
        }
    }
    private void executeOrder() {
        Pattern dane2 = Pattern.compile("([A-Z]+)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)");
        Matcher dane2matcher = dane2.matcher(calyRozkaz);
        String x = "", y = "", z = "";
        if (dane2matcher.matches()) {
            x = dane2matcher.group(2);
            y = dane2matcher.group(3);
        }
        Pattern dane1 = Pattern.compile("([A-Z]+)\\s(\\[*\\w+]*)");
        Matcher dane1matcher = dane1.matcher(calyRozkaz);
        if (dane1matcher.matches()) {
            x = dane1matcher.group(2);
        }
        Pattern dane3 = Pattern.compile("([A-Z]+)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)");
        Matcher dane3matcher = dane3.matcher(calyRozkaz);
        if (dane3matcher.matches()) {
            x = dane3matcher.group(2);
            y = dane3matcher.group(3);
            z = dane3matcher.group(4);
        }
        //if(!rozkaz.equals("AD")&&!rozkaz.equals("MO")&&itd.)
        //{
        //blad -koniec programu
        //}
        switch (rozkaz) {
            case "AD": {
                add(x, y);
                break;
            }
            case "MO": {
                mov(x, y);
                break;
            }
            case "IC": {
                increment(x);
                break;
            }
            case "JP": {
                jump(x);
                break;
            }
            case "HT": {
                //koniec programu
                break;
            }
            case "CF": {
                fileManagement.create(x, user);
                program_counter++;
                break;
            }
            case "WF": {
                fileManagement.write(x, y);
                program_counter++;
                break;
            }
            case "RF": {
                fileManagement.read();
                program_counter++;
                break;
            }
            case "DF": {
                fileManagement.delete(x);
                program_counter++;
                break;
            }
            case "CP": {
                process_container.create_process(x, y, Integer.parseInt(z));
                //np. CP M file 7
                break;
            }
            case "DP": {
                Process_container.delete(Integer.parseInt(x));
                //np. DP 3
                //tu bedzie tez delete po nazwie jak zostanie zrobiona
                break;
            }
            case "SM": {
                //SM [nazwa/PID procesu odbiorcy] [rozmiar wiadomości] [wiadomość]
                break;
            }
            case "RM": {
                //RM [nazwa\PID procesu który ma odczytać wiadomość] [rozmiar wiadomości]
                break;
            }
            default:break;
        }
    }
    private void increment(String x)
    {
        Pattern rejestr = Pattern.compile("[A-D]");
        Matcher rejestrmatcher = rejestr.matcher(x);
        Pattern adres = Pattern.compile("\\[\\d+\\]");
        Matcher adresmatcher = adres.matcher(x);
        if (!rejestrmatcher.matches()&&!adresmatcher.matches())
        {
            //blad - zakonczenie programu
        }
        if (rejestrmatcher.matches()) {
            switch (x) {
                case "A":{
                    A++;
                    break;
                }
                case "B": {
                    B++;
                    break;
                }
                case "C": {
                    C++;
                    break;
                }
                case "D": {
                    D++;
                    break;
                }
                default:
                    break;
            }
        }
        else if (adresmatcher.matches())
        {
            if(Integer.parseInt(x)>base + limit)
            {
                //blad - zakonczenie programu
            }
            else
            {
                //miejsce w pamieci ++
            }
        }
        program_counter++;
    }
    private void jump(String x)
    {
        Pattern jump = Pattern.compile("\\d+");
        Matcher jumpmatcher = jump.matcher(x);
        if (!jumpmatcher.matches())
        {
            //blad - zakonczenie programu
        }
        if (base+Integer.parseInt(x) > base + limit)
        {
            //blad - zakonczenie programu
        }
        else
        {
            program_counter = Integer.parseInt(x);
        }
    }
    private void mov(String x, String y)
    {
        Pattern rejestr = Pattern.compile("[A-D]");
        Matcher rejestrmatcherx = rejestr.matcher(x);
        Matcher rejestrmatchery = rejestr.matcher(y);
        Pattern liczba = Pattern.compile("\\d+");
        Matcher liczbamatcher = liczba.matcher(y);
        Pattern adres = Pattern.compile("\\[\\d+\\]");
        Matcher adresmatchery = adres.matcher(y);
        Matcher adresmatcherx = adres.matcher(x);
        if (!rejestrmatcherx.matches()&&!adresmatcherx.matches())
        {
            //blad - zakonczenie programu
        }
        if(!liczbamatcher.matches()&&!adresmatchery.matches()&&!rejestrmatchery.matches())
        {
            //blad - zakonczenie programu
        }
        if(liczbamatcher.matches())
        {
            if(x.equals("A"))
            {
                A = Integer.parseInt(y);
            }
            else if(x.equals("B"))
            {
                B = Integer.parseInt(y);
            }
            else if(x.equals("C"))
            {
                C = Integer.parseInt(y);
            }
            else if(x.equals("D"))
            {
                D = Integer.parseInt(y);
            }
            else if (adresmatcherx.matches())
            {
                //do pamieci o danym adresie = Integer.parseInt(y);
            }
        }
        if(adresmatchery.matches())
        {
            int dana = 0;
            //dana = pobieranie z pamieci
            switch(x){
            case "A":
            {
                A = dana;
                break;
            }
                case "B":
            {
                B = dana;
                break;
            }
                case "C":
            {
                C = dana;
                break;
            }
                case("D"):
            {
                D = dana;
                break;
            }
                default: break;
            }
        }
        if (y.equals("A"))
        {
            if(x.equals("B"))
            {
                B = A;
            }
            else if(x.equals("C"))
            {
                C = A;
            }
            else if(x.equals("D"))
            {
                D = A;
            }
            else if (adresmatcherx.matches())
            {
                //do pamieci o danym adresie = A;
            }
        }
        if (y.equals("B"))
        {
            if(x.equals("A"))
            {
                A = B;
            }
            else if(x.equals("C"))
            {
                C = B;
            }
            else if(x.equals("D"))
            {
                D = B;
            }
            else if (adresmatcherx.matches())
            {
                //do pamieci o danym adresie = B;
            }
        }
        if(y.equals("C"))
        {
            if(x.equals("A"))
            {
                System.out.println("bardziej");
                A = C;
            }
            else if(x.equals("B"))
            {
                B = C;
            }
            else if(x.equals("D"))
            {
                D = C;
            }
            else if (adresmatcherx.matches())
            {
                //do pamieci o danym adresie = C;
            }
        }
        if (y.equals("D"))
        {
            if(x.equals("A"))
            {
                A = D;
            }
            else if(x.equals("B"))
            {
                B = D;
            }
            else if(x.equals("C"))
            {
                C = D;
            }
            else if (adresmatcherx.matches())
            {
                //do pamieci o danym adresie = D;
            }
        }
        program_counter++;
    }
    private void add(String x, String y)
    {
        Pattern rejestr = Pattern.compile("[A-D]");
        Matcher rejestrmatcherx = rejestr.matcher(x);
        Matcher rejestrmatchery = rejestr.matcher(y);
        if (!rejestrmatcherx.matches())
        {
            //blad - zakonczenie programu
        }
        Pattern liczba = Pattern.compile("\\d+");
        Matcher liczbamatcher = liczba.matcher(y);
        Pattern adres = Pattern.compile("\\[\\d+\\]");
        Matcher adresmatcher = adres.matcher(y);
        if(!liczbamatcher.matches()&&!adresmatcher.matches()&&!rejestrmatchery.matches())
        {
            //blad - zakonczenie programu
        }
        if(liczbamatcher.matches())
        {
            switch (x){
                case "A":
            {
                A = A+Integer.parseInt(y);
                break;
            }
                case "B":
            {
                B = B+Integer.parseInt(y);
                break;
            }
                case "C":
            {
                C = C+Integer.parseInt(y);
                break;
            }
                case "D":
            {
                D = D+Integer.parseInt(y);
                break;
            }
            default:break;
        }
        }
        if(adresmatcher.matches())
        {
            int dana = 0;
            //dana = pobieranie z pamieci
            switch(x){
                case "A":
            {
                A = A+dana;
                break;
            }
                case "B":
            {
                B = B + dana;
                break;
            }
                case "C":
            {
                C = C+dana;
                break;
            }
                case "D":
            {
                D = D+dana;
                break;
            }
            default: break;
            }
        }
        if (y.equals("A"))
        {
            switch(x){
                case "B":
            {
                B = B+A;
                break;
            }
                case "C":
            {
                C = C+A;
                break;
            }
                case "D":
            {
                D = D+A;
                break;
            }
            default:break;
        }
        }
        if (y.equals("B")) {
            switch (x) {
                case "A":
                {
                    A = A + B;
                    break;
                }
                case "C": {
                    C = C + B;
                    break;
                }case "D": {
                    D = D + B;
                    break;
                }
                default:break;
            }
        }
        if(y.equals("C")) {
            switch (x) {
                case "A":{
                    A = A + C;
                    break;
                }case "B": {
                    B = B + C;
                    break;
                }case "D": {
                    D = D + C;
                    break;
                }
                default:break;
            }
        }
        if (y.equals("D")) {
            switch (x) {
                case "A": {
                    A = A + D;
                    break;
                }case "B": {
                    B = B + D;
                    break;
                }case "C": {
                    C = C + D;
                    break;
                }
                default:break;
            }
        }
        program_counter++;
    }
}
