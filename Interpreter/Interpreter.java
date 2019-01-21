package Interpreter;
import Processor.Scheduler;
import Procesy.Process;
import Procesy.State;
import static Procesy.Process.make_porocess;
import static filemodule.FileManagement.*;
import Semaphore.Semaphore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static RAM.Memory.loadProgram;
import static RAM.Memory.readMemory;
//Procesor tworzy obiekt i wywoluje funkcje executeProgram()
public class Interpreter {

    private static int A,B,C,D, program_counter, base, limit, PID, etykietka;
    private static String rozkaz, calyRozkaz;
    private static String user;
    private  static Semaphore semaphore;

	/*
    public Interpreter(FileManagement fileManagement, Semaphore semaphore) {
            //tutaj pierwszy rozkaz programu
			// [KWITNONCY]: TEN KONSTRUKTOR SIE NIE PRZYDA 
    }
	*/
	
	public Interpreter(){
		// [KWITNONCY]: TEN TAK
	}

    private static void start(String filename) {
        if (!loadProgram(filename)) { //ladowac na pocz!!
            System.out.println("Blad! Nie udalo sie zaladowac programu do pamieci");
            Scheduler.running.change_state(State.Terminated);
        } else {
            //Scheduler.running.change_state(State.Running);
            program_counter = Processor.Scheduler.running.get_program_counter;
            PID = Processor.Scheduler.running.get_PID();
            base = Processor.Scheduler.running.get_base();
            limit = Scheduler.running.get_limit();
            A = Scheduler.running.get_AX();
            B = Scheduler.running.get_BX();
            C = Scheduler.running.get_CX();
            D = Scheduler.running.get_DX();
            getOrder();
        }
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
    private static void getOrder() //odczytywanie rozkazu z pamieci
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
    public static void go(int how_many) //
    {
        start(Scheduler.running.get_file_name());
        for (int i = 0;i<how_many||!rozkaz.equals("HT");i++)
        {
            //pierwszy rozkaz jest pobierany juz w start()
            if(!executeOrder())
            {
                return;
            }
            Scheduler.makeOlder(); //postarzanie procesu
            updateProcessor(); //zapisanie zmienionych wartosci rejestru
        }
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
                create(x, user);
                // usera mam miec
                program_counter++;
                break;
            }
            case "WF": {
                semaphore.wait_s(PID);
                write(x, y);
                semaphore.signal_s();
                program_counter++;
                break;
            }
            case "RF": {
                semaphore.wait_s(PID);
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
                        if(x.equals("A"))
                        {
                            A = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        if(x.equals("B"))
                        {
                            B = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        if(x.equals("C"))
                        {
                            C = Integer.parseInt(read(x,Integer.parseInt(y),Integer.parseInt(z)));
                        }
                        if(x.equals("D"))
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
                semaphore.signal_s();
                program_counter++;
                break;
            }
            case "DF": {
                semaphore.wait_s(PID);
                delete(x);
                semaphore.signal_s();
                program_counter++;
                break;
            }
            case "CP": {
                make_porocess(x,y,Integer.parseInt(z));
                //np. CP M file 7
                break;
            }
            case "SM": {
                //SM [nazwa/PID procesu odbiorcy] [rozmiar wiadomości] [wiadomość]
                //SM [nazwa/PID procesu odbiorcy] [rozmiar wiadomości] [adres]
                Pattern adres = Pattern.compile("\\[\\d+]");
                Matcher adresmatcher = adres.matcher(z);
                Pattern pid = Pattern.compile("\\d+");
                Matcher pidmatcher = pid.matcher(x);
                if (adresmatcher.matches())
                {
                    if (pidmatcher.matches()) {
                        Scheduler.running.send_message(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
                    }
                    else{
                        Scheduler.running.send_message(x, Integer.parseInt(y), Integer.parseInt(z));

                    }
                }
                else
                {
                    if (pidmatcher.matches()) {
                        Scheduler.running.send_message(Integer.parseInt(x), Integer.parseInt(y), z);
                    }
                    else{
                        Scheduler.running.send_message(x, Integer.parseInt(y), z);

                    }
                }
                break;
            }
            case "RM": {
                int size=0;
                //skad wielkosc odebranej wiadomosci
                Scheduler.running.read_message(size);
                //RM [nazwa\PID procesu który ma odczytać wiadomość] [rozmiar wiadomości]
                break;
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
            if (readMemory(Integer.parseInt(x))==0){
                System.out.println("Blad. Proba dzielenia przez zero. Koniec programu");
                Scheduler.running.change_state(State.Terminated);
                return false;
            }
            A = A/readMemory(Integer.parseInt(x));
            D = D%readMemory(Integer.parseInt(x));
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
            if(Integer.parseInt(x)>base + limit)
            {
                System.out.println("Blad. Proba wyjscoa poza pamiec programu. Koniec programu");
                Scheduler.running.change_state(State.Terminated);
                return false;
            }
            else
            {
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

