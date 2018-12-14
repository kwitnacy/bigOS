package Interpreter;
import Procesy.Process;
import Procesy.State;
import filemodule.FileManagement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static RAM.Memory.readMemory;

public class Interpreter {

    private int A,B,C,D, program_counter, base, limit, PID;
    private String rozkaz, calyRozkaz;
    private String user;
    private FileManagement fileManagement;
    public Interpreter(FileManagement fileManagement)
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
        char newPart, nastepny1, nastepny2;
        String IfNew;
        Pattern newOrder = Pattern.compile("[A-Z][A-Z]");
        Matcher newOrderMatches = newOrder.matcher("");
        while(!newOrderMatches.matches())
        {
            newPart = readMemory(base + program_counter);
            //odczytywanie calego rozkazu jako stringa
            calyRozkaz = rozkaz + newPart;
            if(i<2) rozkaz = rozkaz + newPart;
            //Sprawdzanie czy nastepne dane to nie kolejny rozkaz
            nastepny1 = readMemory(base + program_counter + 1);
            nastepny2 = readMemory(base + program_counter + 2);
            IfNew = ""+nastepny1 + nastepny2;
            newOrderMatches = newOrder.matcher(IfNew);
            i++;
        }
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
        //System.out.println("Blad. Nie ma takiego rozkazu. Koniec programu");
        //running.change_state(State.Terminated);
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
                System.out.println("Koniec programu");
                //running.change_state(State.Terminated);
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
                //running.make_porocess(x,y,Integer.parseInt(z));
                //np. CP M file 7
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
        Pattern adres = Pattern.compile("\\[\\d+]");
        Matcher adresmatcher = adres.matcher(x);
        if (!rejestrmatcher.matches()&&!adresmatcher.matches())
        {
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
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
                System.out.println("Blad. Koniec programu");
                //running.change_state(State.Terminated);
            }
            else
            {
                //miejsce w pamieci ++
                //spytac Macieja czy w ogole jest taka mozliwosc
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
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
        }
        if (base+Integer.parseInt(x) > base + limit)
        {
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
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
        Pattern adres = Pattern.compile("\\[\\d+]");
        Matcher adresmatchery = adres.matcher(y);
        Matcher adresmatcherx = adres.matcher(x);
        if (!rejestrmatcherx.matches()&&!adresmatcherx.matches())
        {
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
        }
        if(!liczbamatcher.matches()&&!adresmatchery.matches()&&!rejestrmatchery.matches())
        {
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
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
            dana = readMemory(Integer.parseInt(y));
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
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
        }
        Pattern liczba = Pattern.compile("\\d+");
        Matcher liczbamatcher = liczba.matcher(y);
        Pattern adres = Pattern.compile("\\[\\d+]");
        Matcher adresmatcher = adres.matcher(y);
        if(!liczbamatcher.matches()&&!adresmatcher.matches()&&!rejestrmatchery.matches())
        {
            System.out.println("Blad. Koniec programu");
            //running.change_state(State.Terminated);
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
            int dana = readMemory(Integer.parseInt(y));
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