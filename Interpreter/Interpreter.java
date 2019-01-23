package Interpreter;
import Processor.Scheduler;
import Procesy.State;
import static Procesy.Process.make_porocess;
import static RAM.Memory.writeMemory;
import static FileModule.FileManagement.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static RAM.Memory.readMemory;

public class Interpreter {

    private static int A,B,C,D, program_counter, base, limit, PID, etykietka;
    private static String rozkaz="", calyRozkaz;
    private static String user;
	
	public Interpreter(){

	}

    private static void start(String filename) {
            PID = Processor.Scheduler.running.get_PID();
            base = Processor.Scheduler.running.get_base();
            System.out.println(base);
            limit = Scheduler.running.get_limit();
            program_counter = Scheduler.running.get_program_counter();
            A = Scheduler.running.get_AX();
            B = Scheduler.running.get_BX();
            C = Scheduler.running.get_CX();
            D = Scheduler.running.get_DX();
        //}
    }
    static int addresToliczba(String address)
    {
        String ad = address.substring(1,address.length()-1);
        return Integer.parseInt(ad);
    }
    private void set_user()
    {

    }
    private static void updateProcessor()
    {
        Scheduler.running.set_AX(A);
        Scheduler.running.set_BX(B);
        Scheduler.running.set_CX(C);
        Scheduler.running.set_DX(D);
        Scheduler.running.set_program_counter(program_counter);
    }
    private static void getOrder() //odczytywanie rozkazu z pamieci
    {
        int totwocounter=0;
        rozkaz = "";
        calyRozkaz = "";
        Character newPart, nastepny1, nastepny2;
        String IfNew;
        Pattern newOrder = Pattern.compile("[A-Z][A-Z]");
        Matcher newOrderMatches = newOrder.matcher("");
        Pattern slowo = Pattern.compile("\\w+");
        while(!newOrderMatches.matches())
        {
            newPart = readMemory(program_counter);
            //odczytywanie calego rozkazu jako stringa
            calyRozkaz = calyRozkaz + newPart;
            if(totwocounter<2) rozkaz = rozkaz + newPart;
            if(rozkaz.equals("JC"))
            {
                Matcher slowomatcher = slowo.matcher(calyRozkaz);
                while (slowomatcher.matches()) {
                    calyRozkaz = calyRozkaz + readMemory(program_counter+1);
                    program_counter = program_counter+1;
                    slowomatcher = slowo.matcher(calyRozkaz);
                }
            }
            if (rozkaz.equals("HT"))
            {
                break;
            }
            //Sprawdzanie czy nastepne dane to nie kolejny rozkaz
            nastepny1 = readMemory(program_counter + 2);
            nastepny2 = readMemory(program_counter + 3);
            IfNew = ""+nastepny1 + nastepny2;
            newOrderMatches = newOrder.matcher(IfNew);
            totwocounter++;
            program_counter++;
        }
        program_counter++;
        System.out.println("[Interpreter] wykonywany rozkaz: "+calyRozkaz);
    }
    public static void go(int how_many) //
    {
        start(Scheduler.running.get_file_name());
        //if(readMemory(program_counter)=='.')
        //            {
        //
        //            }
        int i = 0;
        do
        {
            getOrder();
            if (!executeOrder()) {
                return;
            }
            Scheduler.makeOlder(); //postarzanie procesu
            updateProcessor(); //zapisanie zmienionych wartosci rejestru
            display();
            i++;
        } while (i+1<how_many && !rozkaz.equals("HT"));
    }
    private static void display()
    {
        System.out.println("Stan po wykonanym rozkazie ");
        System.out.println("PID: "+PID);
        System.out.println("rejestr A: "+A);
        System.out.println("rejestr B: "+B);
        System.out.println("rejestr C: "+C);
        System.out.println("rejestr D: "+D);
        System.out.println("program_counter: "+ program_counter);
    }
    private static boolean executeOrder() {
        Pattern dane2 = Pattern.compile("([A-Z]+)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)");
        Matcher dane2matcher = dane2.matcher(calyRozkaz);
        String x = "", y = "", z = "", xx="";
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
        Pattern dane4 = Pattern.compile("([A-Z]+)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)");
        Matcher dane4matcher = dane4.matcher(calyRozkaz);
        if (dane4matcher.matches()) {
            x = dane4matcher.group(2);
            y = dane4matcher.group(3);
            z = dane4matcher.group(4);
            xx = dane4matcher.group(5);
        }
        Pattern etykieta = Pattern.compile("\\w+:");
        Matcher etykietamatcher = etykieta.matcher(calyRozkaz);
        switch (rozkaz) {
            case "AD": {
                if(!add(x, y))
                {
                    return false;
                }
                break;
            }
            case "MO": {
                if(!mov(x, y))
                {
                    return false;
                }
                break;
            }
            case "IC": {
                if(!increment(x)){
                 return false;
                }
                break;
            }
            case "JP": {
                if (!jump(x))
                {
                 return false;
                }
                break;
            }
            case "HT": {
                System.out.println("Koniec programu");
                Scheduler.running.change_state(State.Terminated);
                return true;
            }
            case "CF": {
                if(!create(x, user))
                {
                    return false;
                }
                // usera mam miec
                break;
            }
            case "WF": {
                waitFile(x,PID);
                if(!write(x, y))
                {
                    return false;
                }
                signalFile(x);
                break;
            }
            case "RF": {
                waitFile(x,PID);
                if (dane3matcher.matches()) {
                System.out.println(read(x,Integer.parseInt(y),Integer.parseInt(z)));}
                else if (dane4matcher.matches()){
                    Pattern rejestr = Pattern.compile("[A-D]");
                    Matcher rejestrmatcher = rejestr.matcher(xx);
                    if (rejestrmatcher.matches())
                    {
                        if (read(x,Integer.parseInt(y),Integer.parseInt(z))==null)
                        {
                            Scheduler.running.change_state(State.Terminated);
                            return false;
                        }
                        else if(xx.equals("A"))
                        {
                            A = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        else if(xx.equals("B"))
                        {
                            B = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        else if(xx.equals("C"))
                        {
                            C = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        else if(xx.equals("D"))
                        {
                            D = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                    }
                    else {
                        System.out.println("Bledny rozkaz - czwarty argument niezgodny. Koniec programu");
                        Scheduler.running.change_state(State.Terminated);
                        return false;
                    }
                }
                signalFile(x);
                break;
            }
            case "DF": {
                waitFile(x,PID);
                if(!delete(x))
                {
                    return false;
                }
                signalFile(x);
                break;
            }
            case "CP": {
                make_porocess(x,y,Integer.parseInt(z));
                //np. CP M file 7
                break;
            }
            case "SM": {
                             //x           //y    //z
                //SM <nazwa/PID odbiorcy> <tekst>
                //SM <nazwa/PID odbiorcy> <size> [adres]
                //SM <nazwa/PID odbiorcy> [adres]
                Pattern adres = Pattern.compile("\\[\\d+]");
                Pattern liczba = Pattern.compile("\\d+");
                Matcher pidmatcher = liczba.matcher(x);
                Matcher sizematcher = liczba.matcher(y);
                Matcher adresmatchery = adres.matcher(y);
                Matcher adresmatcherz = adres.matcher(z);
                if (dane2matcher.matches())
                {
                    System.out.println("nananan");
                    if (adresmatchery.matches()) //SM <PID odbiorcy> [adres]
                    {
                        if (pidmatcher.matches()) {
                            Scheduler.running.send_message(Integer.parseInt(x), addresToliczba(y));
                        }
                        else //SM <nazwa odbiorcy> [adres]
                        {
                            Scheduler.running.send_message(x, addresToliczba(y));
                        }
                    }
                    else { ////SM <nazwa/PID odbiorcy> <tekst>
                        if(pidmatcher.matches())
                        {
                            Scheduler.running.send_message(Integer.parseInt(x), y);
                        }
                        else
                        {
                            Scheduler.running.send_message(x, z);
                        }
                    }
                }
                if (dane3matcher.matches())
                {
                    if (!sizematcher.matches())
                    {
                        System.out.println("Blad. Niezgodny drugi argument. Koniec programu");
                        Scheduler.running.change_state(State.Terminated);
                        return false;
                    }
                    if (adresmatcherz.matches())
                    {
                        if (pidmatcher.matches()) { //SM <PID odbiorcy> <size> [adres]
                        Scheduler.running.send_message(Integer.parseInt(x), Integer.parseInt(y), addresToliczba(z));
                        }
                        else{ //SM <nazwa odbiorcy> <size> [adres]
                            Scheduler.running.send_message(x, Integer.parseInt(y), addresToliczba(z));
                        }
                    }
                    else {
                        System.out.println("Bledny rozkaz. Niezgodny trzeci argument. Koniec programu");
                        Scheduler.running.change_state(State.Terminated);
                        return false;
                    }
                }
                break;
            }
            case "RM": {
                Pattern adres = Pattern.compile("\\[\\d+]");
                Pattern liczba = Pattern.compile("\\d+");
                Matcher sizematcher = liczba.matcher(x);
                Matcher adresmatchery = adres.matcher(y);
                Matcher adresmatcherx = adres.matcher(x);
                if (dane2matcher.matches()) //RM <size> [adres]
                {
                    if (sizematcher.matches()&&adresmatchery.matches())
                    {
                        Scheduler.running.read_message(Integer.parseInt(x),addresToliczba(y));
                    }
                    else
                    {
                        System.out.println("Blad! Argumenty sa niezgodne. Koniec programu");
                        Scheduler.running.change_state(State.Terminated);
                        return false;
                    }
                }
                if(dane1matcher.matches())//RM [adres]
                {
                    System.out.println("xxx:"+x);
                    if (adresmatcherx.matches())
                    {
                        Scheduler.running.read_message(addresToliczba(x));
                    }
                    else
                    {
                        System.out.println("Blad! Argument jest niezgodny. Koniec programu");
                        Scheduler.running.change_state(State.Terminated);
                        return false;
                    }
                }
            }
            case "DI":
            {
                if(!div(x))
                {
                    return false;
                }
                break;
            }
            case "JC":
            {
                if (C != 0)
                {
                    System.out.println("etykietka:"+String.valueOf(etykietka));
                    if(!jump(String.valueOf(etykietka))){
                     return false;
                    }
                    break;
                }
                return true;
            }
            default: {
                if (etykietamatcher.matches()) {
                    etykietka = base + program_counter;
                } else {
                    System.out.println("Bledny rozkaz. Koniec programu");
                    Scheduler.running.change_state(State.Terminated);
                    return false;
                }
                break;
            }
        }
        return true;
    }
    private static boolean div(String x)
    {
        Pattern rejestr = Pattern.compile("[A-D]");
        Matcher rejestrmatcher = rejestr.matcher(x);
        Pattern adres = Pattern.compile("\\[\\d+]");
        Matcher adresmatcher = adres.matcher(x);
        if (!rejestrmatcher.matches()&&!adresmatcher.matches())
        {
            System.out.println("Bledny rozkaz. Niezgodny pierwszy argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
        }
        if(rejestrmatcher.matches())
        {
            if(x.equals("A"))
            {
                D = 0;
            }
            if(x.equals("B"))
            {
                D = A%B;
            }
            if(x.equals("C"))
            {
                D = A%C;
            }
            if(x.equals("D"))
            {
                D = A%D;
            }
        }
        else if(adresmatcher.matches())
        {
            if (readMemory(addresToliczba(x))==0){
                System.out.println("Blad. Proba dzielenia przez zero. Koniec programu");
                Scheduler.running.change_state(State.Terminated);
                return false;
            }
            A = A/readMemory(addresToliczba(x));
            D = D%readMemory(addresToliczba(x));
        }
        return true;
    }
    private static boolean increment(String x)
    {
        Pattern rejestr = Pattern.compile("[A-D]");
        Matcher rejestrmatcher = rejestr.matcher(x);
        Pattern adres = Pattern.compile("\\[\\d+]");
        Matcher adresmatcher = adres.matcher(x);
        if (!rejestrmatcher.matches()&&!adresmatcher.matches())
        {
            System.out.println("Bledny rozkaz. Niezgodny pierwszy argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
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
            }
        }
        else if (adresmatcher.matches())
        {
            if(addresToliczba(x)>base + limit)
            {
                System.out.println("Blad. Proba wyjscia poza pamiec programu. Koniec programu");
                Scheduler.running.change_state(State.Terminated);
                return false;
            }
            else
            {
                int zmienna = readMemory(addresToliczba(x));
                zmienna++;
                char c;
                String wynik = Integer.toString(zmienna);
                int ii;
                for(int i=0;i<wynik.length();i++) {
                    c = wynik.charAt(i);
                    ii = addresToliczba(x)+i;
                    writeMemory(c,ii);
                }
            }
        }
        return true;
    }
    private static boolean jump(String x)
    {
        Pattern jump = Pattern.compile("\\d+");
        Matcher jumpmatcher = jump.matcher(x);
        if (!jumpmatcher.matches())
        {
            System.out.println("Bledny rozkaz. Niezgodny argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
        }
        if (base+Integer.parseInt(x) > base + limit)
        {
            System.out.println("Blad. Proba wyjscoa poza pamiec programu. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
        }
        else
        {
            program_counter = Integer.parseInt(x);
        }
        return true;
    }
    private static boolean mov(String x, String y)
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
            System.out.println("Bledny rozkaz. Niezgodny pierwszy argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
        }
        if(!liczbamatcher.matches()&&!adresmatchery.matches()&&!rejestrmatchery.matches())
        {
            System.out.println("Bledny rozkaz. Niezgodny drugi argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
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
                char c;
                int ii;
                for(int i=0;i<y.length();i++) {
                    c = y.charAt(i);
                    ii = addresToliczba(x)+i;
                    writeMemory(c,ii);
                }
            }
        }
        if(adresmatchery.matches())
        {
            int dana = 0;
            dana = readMemory(addresToliczba(y));
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
                char c;
                String a = Integer.toString(A);
                int ii;
                for(int i=0;i<a.length();i++) {
                    c = a.charAt(i);
                    ii = addresToliczba(x)+i;
                    writeMemory(c,ii);
                }
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
                char c;
                String b = Integer.toString(B);
                int ii;
                for(int i=0;i<b.length();i++) {
                    c = b.charAt(i);
                    ii = addresToliczba(x)+i;
                    writeMemory(c,ii);
                }
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
                char c;
                String cc = Integer.toString(C);
                int ii;
                for(int i=0;i<cc.length();i++) {
                    c = cc.charAt(i);
                    ii = addresToliczba(x)+i;
                    writeMemory(c,ii);
                }
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
                char c;
                String d = Integer.toString(D);
                int ii;
                for(int i=0;i<d.length();i++) {
                    c = d.charAt(i);
                    ii = addresToliczba(x)+i;
                    writeMemory(c,ii);
                }
            }
        }
        return true;
    }
    private static boolean add(String x, String y)
    {
        Pattern rejestr = Pattern.compile("[A-D]");
        Matcher rejestrmatcherx = rejestr.matcher(x);
        Matcher rejestrmatchery = rejestr.matcher(y);
        if (!rejestrmatcherx.matches())
        {
            System.out.println("Bledny rozkaz. Niezgodny pierwszy argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
        }
        Pattern liczba = Pattern.compile("\\d+");
        Matcher liczbamatcher = liczba.matcher(y);
        Pattern adres = Pattern.compile("\\[\\d+]");
        Matcher adresmatcher = adres.matcher(y);
        if(!liczbamatcher.matches()&&!adresmatcher.matches()&&!rejestrmatchery.matches())
        {
            System.out.println("Bledny rozkaz. Niezgodny drugi argument. Koniec programu");
            Scheduler.running.change_state(State.Terminated);
            return false;
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
            int dana = readMemory(addresToliczba(x));
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
        return true;
    }
}
/* Dzialanie poszczegolnych programow
1) NWD
2) przepisywanie do plików wybranych wartości, sumowanie danych z pliku,

Wg Moodle'a, jeszcze dopracuje
3) różne sposoby komunikacji międzyprocesowej, odpowiedniki wait/exit???

Musze jeszcze ogarnac:
.data i .text!
User - skad? (potrzebuje do tworzenia plikow)
ReadMessage - skad wielkosc wiadomosci ktora odczytuje
Zapisywanie danych do pamieci*/