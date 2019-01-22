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

    private static int A,B,C,D, program_counter, base, limit, PID, etykietka, memory_counter;
    private static String rozkaz="", calyRozkaz;
    private static String user;
	
	public Interpreter(){

	}

    private static void start(String filename) {
            Scheduler.running.change_state(State.Running);
            program_counter = 0;
            PID = Processor.Scheduler.running.get_PID();
            base = Processor.Scheduler.running.get_base();
            limit = Scheduler.running.get_limit();
            A = Scheduler.running.get_AX();
            B = Scheduler.running.get_BX();
            C = Scheduler.running.get_CX();
            D = Scheduler.running.get_DX();
        //}
    }
    static int addresToliczba(String address)
    {
        Pattern liczba = Pattern.compile("(\\d+)");
        Matcher matcher = liczba.matcher(address);
        return Integer.parseInt(matcher.group(1));
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
        int i =memory_counter;
        rozkaz = "";
        calyRozkaz = "";
        Character newPart, nastepny1, nastepny2;
        String IfNew;
        Pattern newOrder = Pattern.compile("[A-Z][A-Z]");
        Matcher newOrderMatches = newOrder.matcher("");
        while(!newOrderMatches.matches())
        {
            newPart = readMemory(base + i);
            //odczytywanie calego rozkazu jako stringa
            calyRozkaz = calyRozkaz + newPart;
            if(i<2) rozkaz = rozkaz + newPart;
            //Sprawdzanie czy nastepne dane to nie kolejny rozkaz
            nastepny1 = readMemory(base + i + 2);
            nastepny2 = readMemory(base + i + 3);
            IfNew = ""+nastepny1 + nastepny2;
            newOrderMatches = newOrder.matcher(IfNew);
            i++;
            memory_counter++;
        }
        System.out.println("wykonywany rozkaz: "+calyRozkaz);
    }
    public static void go(int how_many) //
    {
        memory_counter = base;
        start(Scheduler.running.get_file_name());
        for (int i = 0;i<how_many&&!rozkaz.equals("HT");i++)
        {
            getOrder();
            if(!executeOrder())
            {
                return;
            }
            Scheduler.makeOlder(); //postarzanie procesu
            updateProcessor(); //zapisanie zmienionych wartosci rejestru
            display();
        }
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
        Pattern dane4 = Pattern.compile("([A-Z]+)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*)\\s(\\[*\\w+]*\\s(\\[*\\w+]*))");
        Matcher dane4matcher = dane4.matcher(calyRozkaz);
        if (dane4matcher.matches()) {
            x = dane3matcher.group(2);
            y = dane3matcher.group(3);
            z = dane3matcher.group(4);
            xx = dane3matcher.group(5);
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
                break;
            }
            case "CF": {
                if(!create(x, user))
                {
                    return false;
                }
                // usera mam miec
                program_counter++;
                break;
            }
            case "WF": {
                waitFile(x,PID);
                if(!write(x, y))
                {
                    return false;
                }
                signalFile(x);
                program_counter++;
                break;
            }
            case "RF": {
                waitFile(x,PID);
                if (xx.equals("")) {
                System.out.println(read(x,Integer.parseInt(y),Integer.parseInt(z)));}
                else {
                    Pattern rejestr = Pattern.compile("[A-D]");
                    Matcher rejestrmatcher = rejestr.matcher(xx);
                    if (rejestrmatcher.matches())
                    {
                        if (read(x,Integer.parseInt(y),Integer.parseInt(z))==null)
                        {
                            Scheduler.running.change_state(State.Terminated);
                            return false;
                        }
                        else if(x.equals("A"))
                        {
                            A = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        else if(x.equals("B"))
                        {
                            B = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        else if(x.equals("C"))
                        {
                            C = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        else if(x.equals("D"))
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
                program_counter++;
                break;
            }
            case "DF": {
                waitFile(x,PID);
                if(!delete(x))
                {
                    return false;
                }
                signalFile(x);
                program_counter++;
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
                    if (adresmatchery.matches()) //SM <PID odbiorcy> [adres]
                    {
                        if (pidmatcher.matches()) {
                            Scheduler.running.send_message(Integer.parseInt(x), addresToliczba(y));
                        }
                        else //SM <nazzwa odbiorcy> [adres]
                        {
                            Scheduler.running.send_message(x, addresToliczba(y));
                        }
                    }
                    else { ////SM <nazwa/PID odbiorcy> <tekst>
                        if(pidmatcher.matches())
                        {
                        Scheduler.running.send_message(Integer.parseInt(x), z);}
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
                Matcher adresmatcherx = adres.matcher(y);
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
                if (C == 0)
                {
                    if(!jump(String.valueOf(etykietka))){
                     return false;
                    }
                    break;
                }
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
                A = 1;
                D = 0;
            }
            if(x.equals("B"))
            {
                A = A/B;
                D = A%B;
            }
            if(x.equals("C"))
            {
                A = A/C;
                D = A%C;
            }
            if(x.equals("D"))
            {
                A = A/D;
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
        program_counter++;
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
                if(zmienna<10) {
                    char zmien = (char) (zmienna + '0');
                    writeMemory(zmien, addresToliczba(x));
                }
                else
                {
                    //???????????????????????????
                }
                //miejsce w pamieci ++
                //spytac Macieja czy w ogole jest taka mozliwosc
            }
        }
        program_counter++;
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
                //do pamieci o danym adresie = addresToliczba(y);
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
        program_counter++;
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

